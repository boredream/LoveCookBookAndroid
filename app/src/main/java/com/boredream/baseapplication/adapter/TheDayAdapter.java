package com.boredream.baseapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.entity.TheDay;
import com.boredream.baseapplication.listener.OnSelectedListener;
import com.boredream.baseapplication.utils.DateUtils;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TheDayAdapter extends RecyclerView.Adapter<TheDayAdapter.ViewHolder> {

    private List<TheDay> infoList;
    private OnSelectedListener<TheDay> onSelectedListener;

    public void setOnItemClickListener(OnSelectedListener<TheDay> onSelectedListener) {
        this.onSelectedListener = onSelectedListener;
    }

    public TheDayAdapter(List<TheDay> infoList) {
        this.infoList = infoList;
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_the_day, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        TheDay data = infoList.get(position);
        holder.tvName.setText(data.getName());
        String date = data.getTheDayDate();
        if (StringUtils.isEmpty(date)) {
            holder.ivAdd.setVisibility(View.VISIBLE);
            holder.tvNotifyType.setVisibility(View.GONE);
            holder.tvDayCount.setVisibility(View.GONE);
            holder.tv.setVisibility(View.GONE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.tvName.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_VERTICAL);

            holder.tvDate.setVisibility(View.GONE);
        } else {
            holder.ivAdd.setVisibility(View.GONE);
            holder.tvNotifyType.setVisibility(View.VISIBLE);
            holder.tvDayCount.setVisibility(View.VISIBLE);
            holder.tv.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.tvName.getLayoutParams();
            params.removeRule(RelativeLayout.CENTER_VERTICAL);

            holder.tvDate.setVisibility(View.VISIBLE);
            try {
                date = DateUtils.calendar2str(DateUtils.str2calendar(data.getTheDayDate()), "yyyy/MM/dd（EEEE）");
            } catch (Exception e) {
                date = data.getTheDayDate();
            }
            holder.tvDate.setText(date);

            // 计算显示的天数
            if (data.getNotifyType() == TheDay.NOTIFY_TYPE_YEAR_COUNT_DOWN) {
                // 按年倒计天数
                Calendar theDayCalendar = DateUtils.str2calendar(data.getTheDayDate());

                // 年份取今年，月日取纪念日的
                Calendar curYearTheDayCalendar = Calendar.getInstance();
                curYearTheDayCalendar.set(Calendar.MONTH, theDayCalendar.get(Calendar.MONTH));
                curYearTheDayCalendar.set(Calendar.DAY_OF_MONTH, theDayCalendar.get(Calendar.DAY_OF_MONTH));

                int days = DateUtils.calculateDayDiff(curYearTheDayCalendar, Calendar.getInstance());
                if(days < 0) {
                    // 代表今年的今年日已经过了，取明年的
                    curYearTheDayCalendar.add(Calendar.YEAR, 1);
                    days = DateUtils.calculateDayDiff(curYearTheDayCalendar, Calendar.getInstance());
                }

                holder.tvNotifyType.setText("还有");
                holder.tvDayCount.setText(String.valueOf(days));
            } else {
                // 累计天数
                int days = DateUtils.calculateDayDiff(Calendar.getInstance(), DateUtils.str2calendar(data.getTheDayDate()));
                holder.tvNotifyType.setText("已经");
                holder.tvDayCount.setText(String.valueOf(days));
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (onSelectedListener != null) {
                onSelectedListener.onSelected(data);
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv)
        TextView tv;
        @BindView(R.id.tv_day_count)
        TextView tvDayCount;
        @BindView(R.id.tv_notify_type)
        TextView tvNotifyType;
        @BindView(R.id.iv_add)
        ImageView ivAdd;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
