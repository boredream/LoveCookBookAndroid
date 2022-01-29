package com.boredream.baseapplication.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.boredream.baseapplication.R;
import com.boredream.baseapplication.base.BaseActivity;
import com.boredream.baseapplication.entity.User;
import com.boredream.baseapplication.net.GlideHelper;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.utils.TokenKeeper;
import com.boredream.baseapplication.utils.UserKeeper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

public class SplashActivity extends BaseActivity {
    public static final int DUR_TIME_MIN = 1000;

    private ImageView iv;
    private long startLoginTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        // 避免从桌面启动程序后，会重新实例化入口类的activity
        if (!this.isTaskRoot()) {
            Intent intent = getIntent();
            if (intent != null) {
                String action = intent.getAction();
                if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(action)) {
                    finish();
                    return;
                }
            }
        }

        setContentView(R.layout.activity_splash);

        init();
    }

    private void init() {
//        AppHelper.isNormalEnter = true;
        iv = findViewById(R.id.iv);
        GlideHelper.loadRoundedImg(iv, R.mipmap.ic_launcher, 16);
        checkPermission();
    }

    private void checkPermission() {
        AndPermission.with(SplashActivity.this).runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(permissions -> startLogin())
                .onDenied(permissions -> {
                    Toast.makeText(this, "请同意应用的必要权限申请 [写入手机存储] ，否则无法正常使用App！", Toast.LENGTH_LONG).show();
                    finish();
                })
                .start();
    }

    private void startLogin() {
        startLoginTime = SystemClock.elapsedRealtime();

        String token = TokenKeeper.getSingleton().getToken();
        if (token != null) {
            HttpRequest.getInstance()
                    .getApiService()
                    .getUserInfo()
                    .compose(RxComposer.schedulers())
                    .compose(RxComposer.lifecycle(this))
                    .compose(RxComposer.defaultResponse())
                    .subscribe(new SimpleObserver<User>() {
                        @Override
                        public void onNext(User user) {
                            UserKeeper.getSingleton().setUser(user);
                            loginSuccess();
                        }

                        @Override
                        public void onError(Throwable e) {
                            // 拿缓存
                            User user = UserKeeper.getSingleton().getUser();
                            if (user != null) {
                                loginSuccess();
                            } else {
                                loginError(e);
                            }
                        }
                    });
        } else {
            loginError(null);
        }
    }

    @Override
    public void showProgress() {
        // do nothing
    }

    @Override
    public void dismissProgress() {
        // do nothing
    }

    public void loginSuccess() {
        long dur = SystemClock.elapsedRealtime() - startLoginTime;
        if (dur < DUR_TIME_MIN) {
            iv.postDelayed(this::startMainAct, DUR_TIME_MIN - dur);
        } else {
            startMainAct();
        }
    }

    public void loginError(Throwable e) {
        long dur = SystemClock.elapsedRealtime() - startLoginTime;
        if (dur < DUR_TIME_MIN) {
            iv.postDelayed(this::startLoginAct, DUR_TIME_MIN - dur);
        } else {
            startLoginAct();
        }
    }

    private void startMainAct() {
        MainActivity.start(this);
        finish();
    }

    private void startLoginAct() {
        LoginActivity.start(this);
        finish();
    }
}
