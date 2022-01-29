package com.boredream.baseapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.base.BaseActivity;
import com.boredream.baseapplication.entity.User;
import com.boredream.baseapplication.entity.event.UserUpdateEvent;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.utils.UserKeeper;
import com.boredream.baseapplication.view.TitleBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InviteCpActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.tv_invite_code)
    TextView tvInviteCode;
    @BindView(R.id.tv_cp_status)
    TextView tvCpStatus;
    @BindView(R.id.et_cp_invite_code)
    EditText etCpInviteCode;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    public static void start(Context context) {
        Intent intent = new Intent(context, InviteCpActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_cp);
        ButterKnife.bind(this);

        initExtras();
        initView();
        initData();
    }

    private void initExtras() {
    }

    private void initView() {
        titleBar.setTitle("邀请码")
                .hideBg()
                .setLeftBack();
    }

    private void initData() {
        User user = UserKeeper.getSingleton().getUser();
        tvInviteCode.setText(user.getShowId());

    }

    private void bindCp() {
        String code = etCpInviteCode.getText().toString();
        if (StringUtils.isEmpty(code)) {
            showTip("邀请码不能为空");
            return;
        }

        HttpRequest.getInstance()
                .getApiService()
                .bindUserCp(Long.parseLong(code))
                .compose(RxComposer.commonProgress(this))
                .subscribe(new SimpleObserver<User>() {
                    @Override
                    public void onNext(User cp) {
                        // 绑定成功
                        User user = UserKeeper.getSingleton().getUser();
                        user.setCpUserId(cp.getId());
                        user.setCpUser(cp);
                        UserKeeper.getSingleton().setUser(user);

                        EventBus.getDefault().post(new UserUpdateEvent());
                        showTip("另一半绑定成功");
                        finish();
                    }
                });
    }

    @OnClick({R.id.iv_copy, R.id.btn_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_copy:
                String code = tvInviteCode.getText().toString();
                ClipboardUtils.copyText(code);
                showTip("邀请码已复制");
                break;
            case R.id.btn_commit:
                bindCp();
                break;
        }
    }
}