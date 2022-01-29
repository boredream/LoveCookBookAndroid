package com.boredream.baseapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.base.BaseResponse;
import com.boredream.baseapplication.entity.Diary;
import com.boredream.baseapplication.entity.event.DiaryUpdateEvent;
import com.boredream.baseapplication.image.picker.PickImageActivity;
import com.boredream.baseapplication.image.upload.ImageRequestUtils;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.utils.DateUtils;
import com.boredream.baseapplication.utils.DialogUtils;
import com.boredream.baseapplication.view.ImageGridView;
import com.boredream.baseapplication.view.SettingItemView;
import com.boredream.baseapplication.view.TitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class DiaryEditActivity extends PickImageActivity {

    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.siv_date)
    SettingItemView sivDate;
    @BindView(R.id.igv)
    ImageGridView igv;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    private Diary info;
    private boolean isEdit;

    public static void start(Context context, Diary info) {
        Intent intent = new Intent(context, DiaryEditActivity.class);
        intent.putExtra("info", info);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_edit);
        ButterKnife.bind(this);

        initExtras();
        initView();
        initData();
    }

    private void initExtras() {
        info = (Diary) getIntent().getSerializableExtra("info");
        isEdit = info != null;
        if (info == null) {
            info = new Diary();
        }
    }

    private void initView() {
        titleBar.setTitle(isEdit ? "修改日记" : "添加日记").setLeftBack();
        if (isEdit) {
            titleBar.setRight("删除", v -> delete());
        }
        sivDate.setDateAction(date -> info.setDiaryDate(date));
        btnCommit.setText(isEdit ? "修改" : "添加");
        btnCommit.setOnClickListener(v -> commit());
    }

    private void initData() {
        if (!isEdit) {
            // 新增默认设置今天日期
            sivDate.setText(DateUtils.calendar2str(Calendar.getInstance()));
            return;
        }
        etContent.setText(info.getContent());
        sivDate.setText(info.getDiaryDate());
        igv.setImages(info.getImages());
    }

    @Override
    protected void onSinglePickImageResult(@NonNull String path) {
        igv.addLocalImage(path);
    }

    private void commit() {
        String content = etContent.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            showTip("内容不能为空");
            return;
        }
        info.setContent(content);
        info.setDiaryDate(sivDate.getText());
        info.setImages(igv.getImages());

        Observable<BaseResponse<String>> observable;
        if (isEdit) {
            observable = HttpRequest.getInstance()
                    .getApiService()
                    .putDiary(info.getId(), info);
        } else {
            observable = HttpRequest.getInstance()
                    .getApiService()
                    .postDiary(info);
        }

        ImageRequestUtils.checkImage4update(info)
                .flatMap((Function<Diary, ObservableSource<BaseResponse<String>>>) diary -> observable)
                .compose(RxComposer.commonProgress(this))
                .subscribe(new SimpleObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        showTip("提交成功");
                        EventBus.getDefault().post(new DiaryUpdateEvent());
                        finish();
                    }
                });
    }

    private void delete() {
        DialogUtils.show2BtnDialog(this, "删除此条记录", "删除后无法恢复，请问确认删除吗？",
                v -> HttpRequest.getInstance()
                        .getApiService()
                        .deleteDiary(info.getId())
                        .compose(RxComposer.commonProgress(DiaryEditActivity.this))
                        .subscribe(new SimpleObserver<String>() {
                            @Override
                            public void onNext(String s) {
                                showTip("删除成功");
                                EventBus.getDefault().post(new DiaryUpdateEvent());
                                finish();
                            }
                        }));
    }

}