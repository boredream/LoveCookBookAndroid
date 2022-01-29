package com.boredream.baseapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.base.BaseResponse;
import com.boredream.baseapplication.entity.Todo;
import com.boredream.baseapplication.entity.event.TodoUpdateEvent;
import com.boredream.baseapplication.image.picker.PickImageActivity;
import com.boredream.baseapplication.image.upload.ImageRequestUtils;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.utils.DialogUtils;
import com.boredream.baseapplication.view.EditTextWithClear;
import com.boredream.baseapplication.view.ImageGridView;
import com.boredream.baseapplication.view.SettingItemView;
import com.boredream.baseapplication.view.TitleBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class TodoEditActivity extends PickImageActivity {

    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.etwc_name)
    EditTextWithClear etwcName;
    @BindView(R.id.et_desc)
    EditText etDesc;
    @BindView(R.id.siv_date)
    SettingItemView sivDate;
    @BindView(R.id.igv)
    ImageGridView igv;
    @BindView(R.id.btn_done)
    Button btnDone;

    private Todo info;

    public static void start(Context context, Todo info) {
        Intent intent = new Intent(context, TodoEditActivity.class);
        intent.putExtra("info", info);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_edit);
        ButterKnife.bind(this);

        initExtras();
        initView();
        initData();
    }

    private void initExtras() {
        info = (Todo) getIntent().getSerializableExtra("info");
    }

    private void initView() {
        // 进来的一定是编辑的情况 
        titleBar.setTitle("清单详情")
                .setLeftBack()
                .setRight("删除", v -> delete());
        btnDone.setOnClickListener(v -> commit());
        sivDate.setDateAction(date -> info.setDoneDate(date));
    }

    private void initData() {
        etwcName.setText(info.getName());
        etDesc.setText(info.getDetail());
        sivDate.setText(info.getDoneDate());
        igv.setImages(info.getImages());
        btnDone.setText(info.isDone() ? "已完成" : "标记为已完成");
    }

    @Override
    protected void onSinglePickImageResult(@NonNull String path) {
        igv.addLocalImage(path);
    }

    private void commit() {
        String name = etwcName.getText().toString().trim();
        if (StringUtils.isEmpty(name)) {
            showTip("内容不能为空");
            return;
        }
        info.setName(name);
        info.setDetail(etDesc.getText().toString().trim());
        info.setDoneDate(sivDate.getText());
        info.setImages(igv.getImages());
        info.setDone(true);

        ImageRequestUtils.checkImage4update(info)
                .flatMap((Function<Todo, ObservableSource<BaseResponse<String>>>) diary ->
                        HttpRequest.getInstance()
                                .getApiService()
                                .putTodo(info.getId(), info))
                .compose(RxComposer.commonProgress(this))
                .subscribe(new SimpleObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        showTip("提交成功");
                        EventBus.getDefault().post(new TodoUpdateEvent());
                        finish();
                    }
                });
    }

    private void delete() {
        DialogUtils.show2BtnDialog(this, "删除此条记录", "删除后无法恢复，请问确认删除吗？",
                v -> HttpRequest.getInstance()
                        .getApiService()
                        .deleteTodo(info.getId())
                        .compose(RxComposer.commonProgress(TodoEditActivity.this))
                        .subscribe(new SimpleObserver<String>() {
                            @Override
                            public void onNext(String s) {
                                showTip("删除成功");
                                EventBus.getDefault().post(new TodoUpdateEvent());
                                finish();
                            }
                        }));
    }

}