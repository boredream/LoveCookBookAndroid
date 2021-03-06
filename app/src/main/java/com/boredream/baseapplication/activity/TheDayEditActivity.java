package com.boredream.baseapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.base.BaseActivity;
import com.boredream.baseapplication.base.BaseResponse;
import com.boredream.baseapplication.entity.TheDay;
import com.boredream.baseapplication.entity.event.TheDayUpdateEvent;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.utils.DialogUtils;
import com.boredream.baseapplication.view.EditTextWithClear;
import com.boredream.baseapplication.view.SettingItemView;
import com.boredream.baseapplication.view.TitleBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

public class TheDayEditActivity extends BaseActivity {

    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.etwc_name)
    EditTextWithClear etwcName;
    @BindView(R.id.siv_date)
    SettingItemView sivDate;
    @BindView(R.id.siv_notify_type)
    SettingItemView sivNotifyType;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    private TheDay info;
    private boolean isEdit;

    public static void start(Context context, TheDay info) {
        Intent intent = new Intent(context, TheDayEditActivity.class);
        intent.putExtra("info", info);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_day_edit);
        ButterKnife.bind(this);

        initExtras();
        initView();
        initData();
    }

    private void initExtras() {
        info = (TheDay) getIntent().getSerializableExtra("info");
        isEdit = info != null;
        if (info == null) {
            info = new TheDay();
        }
    }

    private void initView() {
        titleBar.setTitle(isEdit ? "???????????????" : "???????????????").setLeftBack();
        if (isEdit) {
            titleBar.setRight("??????", v -> delete());
        }
        sivDate.setDateAction(date -> info.setTheDayDate(date));
        sivNotifyType.setSpinnerAction(data -> info.setNotifyTypeStr(data), "????????????", "????????????");
        btnCommit.setText(isEdit ? "??????" : "??????");
        btnCommit.setOnClickListener(v -> commit());
    }

    private void initData() {
        if (!isEdit) {
            return;
        }
        etwcName.setText(info.getName());
        sivDate.setText(info.getTheDayDate());
        sivNotifyType.setText(info.getNotifyTypeStr());
    }

    private void commit() {
        String name = etwcName.getText().toString().trim();
        if (StringUtils.isEmpty(name)) {
            showTip("??????????????????");
            return;
        }
        info.setName(name);

        Observable<BaseResponse<String>> observable;
        if (isEdit) {
            observable = HttpRequest.getInstance()
                    .getApiService()
                    .putTheDay(info.getId(), info);
        } else {
            observable = HttpRequest.getInstance()
                    .getApiService()
                    .postTheDay(info);
        }

        observable.compose(RxComposer.commonProgress(this))
                .subscribe(new SimpleObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        showTip("????????????");
                        EventBus.getDefault().post(new TheDayUpdateEvent());
                        finish();
                    }
                });
    }

    private void delete() {
        DialogUtils.show2BtnDialog(this, "??????????????????", "????????????????????????????????????????????????",
                v -> HttpRequest.getInstance()
                        .getApiService()
                        .deleteTheDay(info.getId())
                        .compose(RxComposer.commonProgress(TheDayEditActivity.this))
                        .subscribe(new SimpleObserver<String>() {
                            @Override
                            public void onNext(String s) {
                                showTip("????????????");
                                EventBus.getDefault().post(new TheDayUpdateEvent());
                                finish();
                            }
                        }));
    }

}