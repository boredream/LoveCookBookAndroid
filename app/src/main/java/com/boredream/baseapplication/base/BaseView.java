package com.boredream.baseapplication.base;

import android.app.Activity;

import com.trello.rxlifecycle3.LifecycleTransformer;

public interface BaseView {

    Activity getViewContext();

    void showTip(String msg);

    void showProgress();

    void dismissProgress();

    <T> LifecycleTransformer<T> getLifeCycleTransformer();
}
