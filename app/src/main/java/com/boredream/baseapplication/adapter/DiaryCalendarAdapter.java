package com.boredream.baseapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.boredream.baseapplication.R;
import com.boredream.baseapplication.activity.DiaryEditActivity;
import com.boredream.baseapplication.entity.Diary;
import com.boredream.baseapplication.net.GlideHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiaryCalendarAdapter extends RecyclerView.Adapter<DiaryCalendarAdapter.ViewHolder> {

    private List<Diary> infoList;

    public DiaryCalendarAdapter(List<Diary> infoList) {
        this.infoList = infoList;
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary_calendar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Diary data = infoList.get(position);
        holder.tvContent.setText(data.getContent().replace("\n", " "));
        GlideHelper.loadAvatar(holder.ivAvatar, data.getUser());
        holder.itemView.setOnClickListener(v -> DiaryEditActivity.start(holder.itemView.getContext(), data));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
