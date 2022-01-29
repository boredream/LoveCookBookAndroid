package com.boredream.baseapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.boredream.baseapplication.R;
import com.boredream.baseapplication.base.BaseActivity;
import com.boredream.baseapplication.base.BaseFragment;
import com.boredream.baseapplication.fragment.DiaryFragment;
import com.boredream.baseapplication.fragment.FragmentController;
import com.boredream.baseapplication.fragment.MineFragment;
import com.boredream.baseapplication.fragment.TheDayFragment;
import com.boredream.baseapplication.fragment.TodoFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.fl_content)
    FrameLayout flContent;
    @BindView(R.id.rg_bottom_tab)
    RadioGroup rgBottomTab;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 如果是退出应用flag,则直接关闭当前页面,不加载UI
        boolean exit = getIntent().getBooleanExtra("exit", false);
        if (exit) {
            finish();
            return;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initData() {

    }

    private void initView() {
        ButterKnife.bind(this);

        ArrayList<BaseFragment> fragments = new ArrayList<>();
        fragments.add(new TheDayFragment());
        fragments.add(new DiaryFragment());
        fragments.add(new TodoFragment());
        fragments.add(new MineFragment());

        new FragmentController(this, rgBottomTab, R.id.fl_content, fragments);
        changeTab(0);
    }

    public void changeTab(int position) {
        ((RadioButton) rgBottomTab.getChildAt(position)).setChecked(true);
    }

    private long firstBackTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - firstBackTime > 2000) {
            showTip("再按一次退出程序");
            firstBackTime = System.currentTimeMillis();
        } else {
            MobclickAgent.onKillProcess(this);
            exit();
        }
    }

    /**
     * 退出程序
     */
    protected void exit() {
        // 退出程序方法有多种
        // 这里使用clear + new task的方式清空整个任务栈,只保留新打开的Main页面
        // 然后Main页面接收到退出的标志位exit=true,finish自己,这样就关闭了全部页面
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("exit", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
