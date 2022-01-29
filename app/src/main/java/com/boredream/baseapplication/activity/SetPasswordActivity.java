package com.boredream.baseapplication.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.base.BaseActivity;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.view.EditTextWithClear;
import com.boredream.baseapplication.view.TitleBar;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetPasswordActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.etwc_password)
    EditTextWithClear etwcPassword;
    @BindView(R.id.etwc_password_confirm)
    EditTextWithClear etwcPasswordConfirm;

    public static void start(Context context) {
        Intent intent = new Intent(context, SetPasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true);
        setContentView(R.layout.activity_set_password);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        titleBar.setTitle("设置登录密码").setLeftBack();
    }

    @OnClick(R.id.btn_confirm)
    public void onClick() {
        String password = etwcPassword.getText().toString().trim();
        if (StringUtils.isEmpty(password)) {
            showTip("密码不能为空");
            return;
        }

        String passwordConfirm = etwcPasswordConfirm.getText().toString().trim();
        if (StringUtils.isEmpty(passwordConfirm)) {
            showTip("密码确认不能为空");
            return;
        }

        if(!passwordConfirm.equals(password)) {
            showTip("两次输入的密码不一致");
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("password", password);
        HttpRequest.getInstance()
                .getApiService()
                .setPassword(params)
                .compose(RxComposer.commonProgress(this))
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean aBoolean) {
                        showTip("密码设置成功");
                        finish();
                    }
                });
    }
}
