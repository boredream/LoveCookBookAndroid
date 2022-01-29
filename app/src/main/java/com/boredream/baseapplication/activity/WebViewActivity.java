package com.boredream.baseapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.boredream.baseapplication.R;
import com.boredream.baseapplication.base.BaseActivity;
import com.boredream.baseapplication.view.MyWebView;
import com.boredream.baseapplication.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WebViewActivity extends BaseActivity {

    @BindView(R.id.web_view)
    MyWebView webView;
    @BindView(R.id.title_bar)
    TitleBar titleBar;

    private String url;
    private String fixTitle;

    /**
     * 打开内置浏览器页面
     *
     * @param url      地址
     * @param fixTitle 固定标题，为null时默认取链接的title
     */
    public static void start(Context context, @NonNull String url, String fixTitle) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("fixTitle", fixTitle);
        context.startActivity(intent);
    }

    @Override
    protected void setScreenOrientation() {
        //
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        // 解决TBS浏览器，避免闪屏和透明问题
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        initExtras();
        initData();
    }

    private void initExtras() {
        url = getIntent().getStringExtra("url");
        fixTitle = getIntent().getStringExtra("fixTitle");
    }

    private void initData() {
        titleBar.setTitle(fixTitle)
                .hideBg()
                .setLeftBack();
        webView.loadUrl(url);
    }

}
