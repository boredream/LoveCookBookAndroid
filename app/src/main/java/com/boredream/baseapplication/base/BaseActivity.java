package com.boredream.baseapplication.base;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

public class BaseActivity extends RxAppCompatActivity implements BaseView {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("加载中...");
        setStatusBar(false);
        setScreenOrientation();
    }

    protected void setStatusBar(boolean lightMode) {
        BarUtils.setStatusBarLightMode(this, lightMode);
        BarUtils.transparentStatusBar(this);
    }

    protected void setScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public <T> LifecycleTransformer<T> getLifeCycleTransformer() {
        return bindUntilEvent(ActivityEvent.DESTROY);
    }

    @Override
    public Activity getViewContext() {
        return this;
    }

    @Override
    public void showTip(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        if (progressDialog == null || progressDialog.isShowing()) return;
        progressDialog.show();
    }

    @Override
    public void dismissProgress() {
        if (progressDialog == null || !progressDialog.isShowing()) return;
        progressDialog.dismiss();
    }
}
