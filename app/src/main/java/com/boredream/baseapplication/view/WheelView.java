package com.boredream.baseapplication.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.boredream.baseapplication.R;

import java.util.List;

/**
 * 滚轮控件
 */
public class WheelView<T> extends RelativeLayout {
    // 自定义RV + SnapHelper实现滚轮
    private RecyclerView rv;
    private LinearSnapHelper snapHelper;
    private WheelView.ItemAdapter adapter;
    private List<T> items;

    public RecyclerView getRv() {
        return rv;
    }

    public WheelView(Context context) {
        super(context);
        initView(context, null);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        int height = SizeUtils.dp2px(340);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));

        rv = new RecyclerView(context);
        rv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rv);
        addView(rv);

        View view = new View(context);
        view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        view.setBackgroundResource(R.drawable.bg_gradient_vertical_wheel);
        addView(view);

        View over = new View(context);
        LayoutParams overParams = new LayoutParams(LayoutParams.MATCH_PARENT, height / 5);
        overParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        over.setLayoutParams(overParams);
        over.setBackgroundResource(R.color.divider_gray);
        addView(over);
    }

    public void setItems(List<T> items) {
        setItems(items, null);
    }

    public void setItems(List<T> items, T selected) {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }

        if (adapter != null && this.items != null) {
            this.items.clear();
            this.items.addAll(items);
            addEmptyItem();
            adapter.notifyDataSetChanged();
            return;
        }

//        this.items = CollectionUtils.filterEmpty(items);
        this.items = items;
        addEmptyItem();

        adapter = new WheelView.ItemAdapter();
        rv.setAdapter(adapter);
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    T selectItem = getSelectItem();
                    if (selectItem != null && onWheelSelectedListener != null) {
                        onWheelSelectedListener.onWheelSelected(selectItem);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        if (selected != null) {
            for (int i = 0; i < this.items.size(); i++) {
                if (selected.equals(this.items.get(i))) {
                    rv.scrollToPosition(i - 2);
                    break;
                }
            }
        }
    }

    public void scrollToText(String text) {
        if (TextUtils.isEmpty(text)) return;
        if (CollectionUtils.isEmpty(items)) return;
        for (int i = 0; i < this.items.size(); i++) {
            T t = this.items.get(i);
            if (t != null && text.equals(t.toString())) {
                LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
                layoutManager.scrollToPositionWithOffset(i, SizeUtils.dp2px(68 * 2));
                break;
            }
        }
    }

    public void scrollToPosition(int position) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
        layoutManager.scrollToPositionWithOffset(position + 2, SizeUtils.dp2px(68 * 2));
    }

    private void addEmptyItem() {
        this.items.add(0, null);
        this.items.add(0, null);
        this.items.add(null);
        this.items.add(null);
    }

    public T getSelectItem() {
        View snapView = snapHelper.findSnapView(rv.getLayoutManager());
        int position = rv.getChildAdapterPosition(snapView);
        if (position == RecyclerView.NO_POSITION) {
            return null;
        } else {
            return items.get(position);
        }
    }

    private class ItemAdapter extends RecyclerView.Adapter<WheelView.ViewHolder> {

        @Override
        public WheelView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_bottom_list_item, parent, false);
            return new WheelView.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(WheelView.ViewHolder holder, int position) {
            T data = items.get(position % items.size());
            if (data == null) {
                holder.tv_item.setText("");
            } else {
                holder.tv_item.setTag(data);
                holder.tv_item.setText(data.toString());
            }
        }

        @Override
        public int getItemCount() {
            return items == null ? 0 : items.size();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_item;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_item = itemView.findViewById(R.id.tv_item);
        }
    }

    private OnWheelSelectedListener<T> onWheelSelectedListener;

    public void setOnWheelSelectedListener(OnWheelSelectedListener<T> onWheelSelectedListener) {
        this.onWheelSelectedListener = onWheelSelectedListener;
    }

    public static interface OnWheelSelectedListener<T> {
        void onWheelSelected(@NonNull T t);
    }
}
