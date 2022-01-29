package com.boredream.baseapplication.net;

import android.os.NetworkOnMainThreadException;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.NetworkUtils;
import com.boredream.baseapplication.base.BaseResponse;
import com.google.gson.JsonParseException;

import java.io.EOFException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.HttpException;

public class ErrorConstants {

    /**
     * 成功
     */
    public static final int SUCCESS = 0;

    public static boolean compareApiError(Throwable throwable, int targetCode) {
        boolean result = false;
        if (throwable instanceof ApiException) {
            ApiException apiException = (ApiException) throwable;
            BaseResponse<?> body = apiException.getBody();
            if (body != null) {
                result = body.getCode() == targetCode;
            }
        }
        return result;
    }

    public static boolean compareApiError(Throwable throwable, @NonNull String targetMsg) {
        boolean result = false;
        if (throwable instanceof ApiException) {
            ApiException apiException = (ApiException) throwable;
            BaseResponse<?> body = apiException.getBody();
            if (body != null && body.getMsg() != null) {
                result = body.getMsg().equals(targetMsg);
            }
        }
        return result;
    }

    /**
     * 解析服务器错误信息
     */
    public static String parseHttpErrorInfo(Throwable throwable) {
        String errorInfo = throwable.getMessage();
        if (!NetworkUtils.isConnected()) {
            errorInfo = "网络未连接";
        } else if (throwable instanceof HttpException) {
            // 如果是Retrofit的Http错误,则转换类型,获取信息
            HttpException exception = (HttpException) throwable;
            errorInfo = "服务器错误" + exception.code();
        } else if (throwable instanceof NetworkOnMainThreadException) {
            errorInfo = "网络请求不能在主线程";
        } else if (throwable instanceof ApiException) {
            ApiException apiException = (ApiException) throwable;
            // 优先使用服务端返回错误
            BaseResponse<?> errorBody = apiException.getBody();
            errorInfo = errorBody.getMsg();
        } else if (throwable instanceof ConnectException) {
            errorInfo = "无法连接服务器";
        } else if (throwable instanceof UnknownHostException) {
            errorInfo = "服务器连接失败";
        } else if (throwable instanceof SocketTimeoutException) {
            errorInfo = "服务器连接超时";
        } else if ("The mapper function returned a null value.".equals(throwable.getMessage())
                || throwable instanceof JsonParseException
                || throwable instanceof EOFException) {
            errorInfo = "数据解析错误 " + throwable.getMessage();
        }

        // 缺省处理
        if (errorInfo == null || errorInfo.length() == 0) {
            errorInfo = "未知错误 " + throwable.getMessage();
        }

        return errorInfo;
    }

}
