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
import com.boredream.baseapplication.entity.FeedBack;
import com.boredream.baseapplication.image.picker.PickImageActivity;
import com.boredream.baseapplication.image.upload.ImageRequestUtils;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.view.ImageGridView;
import com.boredream.baseapplication.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class FeedBackActivity extends PickImageActivity {

    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.et_content)
    EditText etContent;
    @BindView(R.id.igv)
    ImageGridView igv;
    @BindView(R.id.btn_commit)
    Button btnCommit;

    public static void start(Context context) {
        Intent intent = new Intent(context, FeedBackActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        titleBar.setTitle("意见反馈").setLeftBack();
        btnCommit.setOnClickListener(v -> commit());
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
        FeedBack info = new FeedBack();
        info.setDetail(content);
        info.setImages(igv.getImages());

        ImageRequestUtils.checkImage4update(info)
                .flatMap((Function<FeedBack, ObservableSource<BaseResponse<String>>>) diary ->
                        HttpRequest.getInstance()
                                .getApiService()
                                .postFeedBack(info))
                .compose(RxComposer.commonProgress(this))
                .subscribe(new SimpleObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        showTip("提交成功，感谢您的反馈！");
                        finish();
                    }
                });
    }

}