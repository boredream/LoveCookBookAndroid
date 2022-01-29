package com.boredream.baseapplication.base;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.trello.rxlifecycle3.components.support.RxFragment;

public class BaseFragment extends RxFragment implements BaseView {

    public String TAG;
    public BaseActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = getClass().getSimpleName();
        activity = (BaseActivity) getActivity();
    }

    public void showLog(String msg) {
        Log.i(TAG, msg);
    }

    @Override
    public Activity getViewContext() {
        return activity;
    }

    @Override
    public void showTip(String msg) {
        activity.showTip(msg);
    }

    @Override
    public void showProgress() {
        activity.showProgress();
    }

    @Override
    public void dismissProgress() {
        activity.dismissProgress();
    }

    @Override
    public <T> LifecycleTransformer<T> getLifeCycleTransformer() {
        return bindUntilEvent(FragmentEvent.DESTROY_VIEW);
    }

}
