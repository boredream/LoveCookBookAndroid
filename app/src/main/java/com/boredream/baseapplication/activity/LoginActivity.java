package com.boredream.baseapplication.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.base.BaseActivity;
import com.boredream.baseapplication.base.BaseResponse;
import com.boredream.baseapplication.entity.LoginRequest;
import com.boredream.baseapplication.entity.User;
import com.boredream.baseapplication.entity.VerifyCodeRequest;
import com.boredream.baseapplication.net.GlideHelper;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.utils.TokenKeeper;
import com.boredream.baseapplication.utils.UMengUtils;
import com.boredream.baseapplication.utils.UserKeeper;
import com.boredream.baseapplication.view.EditTextWithClear;
import com.umeng.commonsdk.UMConfigure;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.etwc_username)
    EditTextWithClear etUsername;
    @BindView(R.id.etwc_password)
    EditTextWithClear etPassword;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.etwc_verify_code)
    EditTextWithClear etVerifyCode;
    @BindView(R.id.ll_verify_code)
    LinearLayout llVerifyCode;
    @BindView(R.id.ll_password)
    LinearLayout llPassword;
    @BindView(R.id.tv_change_login)
    TextView tvChangeLogin;
    @BindView(R.id.tv_send_verify_code)
    TextView tvSendVerifyCode;

    private boolean isVerifyCodeLogin = true;

    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        GlideHelper.loadRoundedImg(ivLogo, R.mipmap.ic_launcher, 16);
        etPassword.getEt().setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btnLogin.setOnClickListener(v -> login());
        tvChangeLogin.setOnClickListener(v -> {
            if (!isVerifyCodeLogin) {
                // 当前是密码模式，切换到验证码登录
                isVerifyCodeLogin = true;
                tvChangeLogin.setText("账号密码登录");
                llPassword.setVisibility(View.GONE);
                llVerifyCode.setVisibility(View.VISIBLE);
            } else {
                isVerifyCodeLogin = false;
                tvChangeLogin.setText("手机验证码登录");
                llPassword.setVisibility(View.VISIBLE);
                llVerifyCode.setVisibility(View.GONE);
            }
        });
        tvSendVerifyCode.setOnClickListener(v -> requestMsg());
    }

    @SuppressLint("CheckResult")
    private void requestMsg() {
        String username = etUsername.getText().toString().trim();
        if (StringUtils.isEmpty(username)) {
            showTip("手机号不能为空");
            return;
        }
        HttpRequest.getInstance()
                .getApiService()
                .sendVerifyCode(username)
                .compose(RxComposer.commonProgress(this))
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean response) {
                        requestMsgSuccess();
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void requestMsgSuccess() {
        showTip("验证码发送成功，5分钟内填写都有效");
        Observable.interval(1, TimeUnit.SECONDS)
                .startWith(0L)
                .take(61)
                .map(aLong -> 60 - aLong)
                .doOnSubscribe(disposable -> tvSendVerifyCode.setEnabled(false))
                .compose(RxComposer.lifecycle(this))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        aLong -> tvSendVerifyCode.setText(String.format(Locale.getDefault(), "%ds", aLong)),
                        Throwable::printStackTrace,
                        () -> {
                            tvSendVerifyCode.setEnabled(true);
                            tvSendVerifyCode.setText("获取验证码");
                        });
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        if (StringUtils.isEmpty(username)) {
            showTip("手机号不能为空");
            return;
        }

        if (isVerifyCodeLogin) {
            loginWithVerifyCode(username);
        } else {
            loginWithPassword(username);
        }
    }

    private void loginWithVerifyCode(String username) {
        String verifyCode = etVerifyCode.getText().toString().trim();
        if (StringUtils.isEmpty(verifyCode)) {
            showTip("验证码不能为空");
            return;
        }

        VerifyCodeRequest request = new VerifyCodeRequest();
        request.setPhone(username);
        request.setCode(verifyCode);

        getUserInfo(HttpRequest.getInstance()
                .getApiService()
                .loginWithVerifyCode(request));
    }

    private void loginWithPassword(String username) {
        String password = etPassword.getText().toString().trim();
        if (StringUtils.isEmpty(password)) {
            showTip("密码不能为空");
            return;
        }

        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        getUserInfo(HttpRequest.getInstance()
                .getApiService()
                .postUserLogin(request));
    }

    private void getUserInfo(Observable<BaseResponse<String>> compose) {
        compose.compose(RxComposer.commonProgress(this))
                .flatMap((Function<String, ObservableSource<User>>)
                        token -> {
                            TokenKeeper.getSingleton().setToken(token);
                            return HttpRequest.getInstance()
                                    .getApiService()
                                    .getUserInfo()
                                    .compose(RxComposer.commonProgress(LoginActivity.this));
                        })
                .subscribe(new SimpleObserver<User>() {
                    @Override
                    public void onNext(User user) {
                        loginSuccess(user);
                    }
                });
    }

    private void loginSuccess(User user) {
        UserKeeper.getSingleton().setUser(user);
        finish();
        MainActivity.start(this);
    }

    @OnClick({R.id.tv_protocol, R.id.tv_privacy})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_protocol:
                WebViewActivity.start(this, "http://www.papikoala.cn/lovebook/userprotocol.html", "用户协议");
                break;
            case R.id.tv_privacy:
                WebViewActivity.start(this, "http://www.papikoala.cn/lovebook/privacy.html", "隐私政策");
                break;
        }
    }
}
