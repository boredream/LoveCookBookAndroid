package com.boredream.baseapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.boredream.baseapplication.R;
import com.boredream.baseapplication.activity.DiaryEditActivity;
import com.boredream.baseapplication.entity.Diary;
import com.boredream.baseapplication.net.GlideHelper;
import com.boredream.baseapplication.utils.DateUtils;
import com.boredream.baseapplication.view.RingLineView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> {

    private List<Diary> infoList;

    // TODO: chunyang 11/30/21 合并日期头
    private ArrayList<String> dateHeaderList = new ArrayList<>();

    public DiaryAdapter(List<Diary> infoList) {
        this.infoList = infoList;
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Diary data = infoList.get(position);
        Calendar calendar = DateUtils.str2calendar(data.getDiaryDate());
        holder.rlv.setOvalColor(position == 0 ? R.color.colorPrimary : R.color.gray50);
        holder.rlv.setHideTopLine(position == 0);
        holder.tvDay.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        holder.tvWeek.setText(DateUtils.calendar2str(calendar, "EE"));
        holder.tvYearMonth.setText(DateUtils.calendar2str(calendar, "yyyy年MM月"));
        holder.tvContent.setText(data.getContent());
        GlideHelper.loadAvatar(holder.ivUserAvatar, data.getUser());
        holder.tvUserName.setText(data.getUser().getNickname());

        holder.flCard.setOnClickListener(v -> DiaryEditActivity.start(holder.itemView.getContext(), data));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.rlv)
        RingLineView rlv;
        @BindView(R.id.tv_day)
        TextView tvDay;
        @BindView(R.id.tv_week)
        TextView tvWeek;
        @BindView(R.id.tv_year_month)
        TextView tvYearMonth;
        @BindView(R.id.rl_date_header)
        RelativeLayout rlDateHeader;
        @BindView(R.id.fl_card)
        FrameLayout flCard;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.iv_user_avatar)
        ImageView ivUserAvatar;
        @BindView(R.id.tv_user_name)
        TextView tvUserName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
