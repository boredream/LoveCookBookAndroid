package com.boredream.baseapplication.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.entity.SettingItem;
import com.boredream.baseapplication.listener.OnSelectedListener;
import com.boredream.baseapplication.view.SettingItemView;

import java.util.List;

public class SettingItemAdapter extends RecyclerView.Adapter<SettingItemAdapter.ViewHolder> {

    private List<SettingItem> infoList;
    private OnSelectedListener<SettingItem> onSelectedListener;

    public void setOnItemClickListener(OnSelectedListener<SettingItem> onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public SettingItemAdapter(List<SettingItem> infoList) {
        this.infoList = infoList;
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SettingItemView v = new SettingItemView(parent.getContext());
        v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, SizeUtils.dp2px(62)));
        // 阴影，y4 b8
        ImageView ivLeft = v.getIvLeft();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ivLeft.getLayoutParams();
        params.width += SizeUtils.dp2px(16);
        params.leftMargin -= SizeUtils.dp2px(8);
        params.rightMargin -= SizeUtils.dp2px(8);
        ivLeft.setPadding(0, SizeUtils.dp2px(8), 0, 0);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        SettingItem data = infoList.get(position);
        holder.content.setData(data);
        TextView tvRight = holder.content.getTvRight();
        tvRight.setTextSize("另一半".equals(data.getName()) ? 12 : 14);
        tvRight.setTextColor(holder.itemView.getResources().getColor(
                "另一半".equals(data.getName()) ? R.color.txt_light_gray : R.color.txt_gray));

        holder.itemView.setOnClickListener(v -> {
            if (onSelectedListener != null) {
                onSelectedListener.onSelected(data);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        SettingItemView content;

        public ViewHolder(View itemView) {
            super(itemView);
            content = (SettingItemView) itemView;
        }
    }
}
