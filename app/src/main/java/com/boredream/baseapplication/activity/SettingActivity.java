package com.boredream.baseapplication.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boredream.baseapplication.R;
import com.boredream.baseapplication.adapter.SettingItemAdapter;
import com.boredream.baseapplication.base.BaseActivity;
import com.boredream.baseapplication.entity.SettingItem;
import com.boredream.baseapplication.listener.OnSelectedListener;
import com.boredream.baseapplication.view.TitleBar;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends BaseActivity implements OnSelectedListener<SettingItem> {

    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.rv_items)
    RecyclerView rvItems;

    public static void start(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    private SettingItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar(true);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        titleBar.setTitle("设置").setLeftBack();

        List<SettingItem> settingList = Arrays.asList(
                new SettingItem(null, "设置登录密码", null, false),
                new SettingItem(null, "意见反馈", null, false),
                new SettingItem(null, "用户协议", null, false),
                new SettingItem(null, "隐私政策", null, false)
        );
        adapter = new SettingItemAdapter(settingList);
        rvItems.setLayoutManager(new LinearLayoutManager(this));
        rvItems.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onSelected(SettingItem data) {
        switch (data.getName()) {
            case "设置登录密码":
                SetPasswordActivity.start(this);
                break;
            case "意见反馈":
                FeedBackActivity.start(this);
                break;
            case "用户协议":
                WebViewActivity.start(this, "http://www.papikoala.cn/lovebook/userprotocol.html", "用户协议");
                break;
            case "隐私政策":
                WebViewActivity.start(this, "http://www.papikoala.cn/lovebook/privacy.html", "隐私政策");
                break;
        }
    }
}
