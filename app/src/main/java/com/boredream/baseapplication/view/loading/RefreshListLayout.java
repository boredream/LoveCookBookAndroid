package com.boredream.baseapplication.view.loading;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.boredream.baseapplication.R;
import com.boredream.baseapplication.net.ErrorConstants;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RefreshListLayout extends SmartRefreshLayout implements ILoadingView {

    @BindView(R.id.refresh_list_rv)
    RecyclerView rv;
    @BindView(R.id.refresh_list_iv_empty)
    ImageView ivEmpty;
    @BindView(R.id.refresh_list_tv_empty)
    TextView tvEmpty;
    @BindView(R.id.refresh_list_ll_empty)
    LinearLayout llEmpty;
    @BindView(R.id.refresh_list_ll_loading)
    LinearLayout llLoading;
    @BindView(R.id.refresh_btn_empty)
    Button btnEmpty;

    public RecyclerView getRv() {
        return rv;
    }

    public ImageView getIvEmpty() {
        return ivEmpty;
    }

    public TextView getTvEmpty() {
        return tvEmpty;
    }

    public LinearLayout getLlEmpty() {
        return llEmpty;
    }

    public LinearLayout getLlLoading() {
        return llLoading;
    }

    public RefreshListLayout(Context context) {
        super(context);
        init();
    }

    public RefreshListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_refresh_list_layout, this);
        ButterKnife.bind(this);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    public void showError(Throwable e) {
        String errorInfo = ErrorConstants.parseHttpErrorInfo(e);
        if ("网络未连接".equals(errorInfo)) {
            ivEmpty.setImageResource(R.drawable.default_offline);
            tvEmpty.setText("网络当前没有信号哦~");
        } else {
            ivEmpty.setImageResource(R.drawable.default_empty);
            tvEmpty.setText("当前页面加载失败哦~");
        }
        btnEmpty.setText("立即刷新");
        btnEmpty.setVisibility(View.VISIBLE);
        btnEmpty.setOnClickListener(v -> {
            if (onDefaultAction != null) {
                onDefaultAction.onRefresh();
            }
        });
    }

    public void checkEmpty() {
        if (rv.getAdapter() == null) return;
        rv.getAdapter().notifyDataSetChanged();
        if (rv.getAdapter().getItemCount() == 0) {
            ivEmpty.setImageResource(R.drawable.default_empty);
            tvEmpty.setText("还没有创建任何信息哦~");
            btnEmpty.setVisibility(View.INVISIBLE);
            btnEmpty.setOnClickListener(v -> {
                if (onDefaultAction != null) {
                    onDefaultAction.onCreate();
                }
            });

            llEmpty.setVisibility(VISIBLE);
        } else {
            llEmpty.setVisibility(GONE);
        }
    }

    public void showRefresh(boolean loadMore) {
        // 加载更多时，一般都不是手动触发，无视
        if (loadMore) return;

        // 如果正在refreshing，不用处理
        if (isRefreshing()) return;

        // 显示布局loading
        llLoading.setVisibility(VISIBLE);
    }

    public void dismissRefresh(boolean loadMore) {
        // 不同loading状态，不同dismiss处理
        if (loadMore) {
            finishLoadmore();
        } else {
            if (isRefreshing()) {
                finishRefresh();
            }
            if (llLoading.getVisibility() == VISIBLE) {
                llLoading.setVisibility(GONE);
            }
        }
    }

    @Override
    public void doOnSubscribe(boolean loadMore) {
        showRefresh(loadMore);
    }

    public void doOnError(Throwable throwable, boolean loadMore) {
        dismissRefresh(loadMore);
        if(!loadMore) {
            showError(throwable);
        }
    }

    public <T> void doOnNext(T t, boolean loadMore) {
        dismissRefresh(loadMore);
    }

    private OnDefaultActionListener onDefaultAction;

    public void setOnDefaultAction(OnDefaultActionListener onDefaultAction) {
        this.onDefaultAction = onDefaultAction;
    }

    public static interface OnDefaultActionListener {
        void onRefresh();
        void onCreate();
    }
}
