package com.boredream.baseapplication.base;


import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.boredream.baseapplication.utils.UMengUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.umeng.commonsdk.UMConfigure;


public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        initRefresh();
        UMConfigure.preInit(this, UMengUtils.APP_KEY, UMengUtils.CHANNEL);
    }

    private void initRefresh() {
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater((context, layout) ->
                new ClassicsHeader(context));

        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater((context, layout) ->
                new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate));
    }
}
