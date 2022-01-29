package com.boredream.baseapplication.net;

import com.boredream.baseapplication.base.BaseResponse;
import com.boredream.baseapplication.base.BaseView;
import com.boredream.baseapplication.view.loading.ILoadingView;

import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Rx組件。一般情况下直接使用组合类compose，如果有特殊需要自行组装基础compose
 */
public class RxComposer {

    ////////////////////////////// 常用组合compose //////////////////////////////

    /**
     * 常规显示进度框样式
     */
    public static <T> ObservableTransformer<BaseResponse<T>, T> commonProgress(final BaseView view) {
        return upstream -> upstream.compose(schedulers())
                .compose(lifecycle(view))
                .compose(defaultResponse())
                .compose(defaultFailed(view))
                .compose(handleProgress(view));
    }

    public static <T> ObservableTransformer<BaseResponse<T>, T> commonRefresh(
            BaseView view, ILoadingView loadingView, boolean loadMore) {
        return upstream -> upstream.compose(schedulers())
                .compose(lifecycle(view))
                .compose(defaultResponse())
                .compose(defaultFailed(view))
                .compose(handleRefresh(loadingView, loadMore));
    }


    ////////////////////////////// 基础compose //////////////////////////////

    /**
     * schedulers线程分发处理
     */
    public static <T> ObservableTransformer<T, T> schedulers() {
        return upstream -> upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * lifecycle防止页面销毁后继续回调处理view
     */
    public static <T> ObservableTransformer<T, T> lifecycle(final BaseView view) {
        return upstream -> upstream
                .compose(view.<T>getLifeCycleTransformer());
    }

    /**
     * 返回内容统一处理
     */
    public static <T> ObservableTransformer<BaseResponse<T>, T> defaultResponse() {
        return upstream -> upstream.map(response -> {
            // 至此网络请求正常，但可能自定义的数据里有code=xxx，代表着业务类错误，在此处理
            int code = response.getCode();
            if (code != ErrorConstants.SUCCESS) {
                throw new ApiException(response);
            }
            return response.getData();
        });
    }

    /**
     * error统一处理，自动提示Toast
     */
    public static <T> ObservableTransformer<T, T> defaultFailed(final BaseView view) {
        return upstream -> upstream.observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> {
                    String error = ErrorConstants.parseHttpErrorInfo(throwable);
                    throwable.printStackTrace();
                    view.showTip(error);
                });
    }

    /**
     * 进度框统一处理，发送请求时自动 showProgress，请求成功/失败时自动 dismissProgress
     */
    public static <T> ObservableTransformer<T, T> handleProgress(final BaseView view) {
        return upstream -> upstream.observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> view.showProgress()) // 开始订阅时 = 接口发出时
                .doOnError(throwable -> view.dismissProgress()) // 错误返回时
                .doOnNext((Consumer<T>) t -> view.dismissProgress()); // 正常返回时
    }

    /**
     * 刷新控件统一处理
     */
    public static <T> ObservableTransformer<T, T> handleRefresh(ILoadingView loadingView, boolean loadMore) {
        return upstream -> upstream.observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> loadingView.doOnSubscribe(loadMore))
                .doOnError(throwable -> loadingView.doOnError(throwable, loadMore))
                .doOnNext(t -> loadingView.doOnNext(t, loadMore));
    }
}
