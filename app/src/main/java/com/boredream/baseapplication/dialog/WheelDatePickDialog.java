package com.boredream.baseapplication.dialog;

import android.content.Context;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.ScreenUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.listener.OnDatePickListener;
import com.boredream.baseapplication.utils.DateUtils;
import com.boredream.baseapplication.view.DialogTitle;
import com.boredream.baseapplication.view.WheelView;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * 滚轮时间选择对话框
 */
public class WheelDatePickDialog extends BottomDialog {

    DialogTitle dialog_title;
    LinearLayout ll_container;
    WheelView<String> wheel_year;
    WheelView<String> wheel_month;
    WheelView<String> wheel_day;
    WheelView<String> wheel_hour;
    WheelView<String> wheel_minute;
    Button btn_ok;

    private String title = "选择日期";
    private boolean showYear = true;
    private boolean showMonth = true;
    private boolean showDay = true;
    private boolean showHour;
    private boolean showMinute;
    private int wheelWidth;
    private Calendar calendar;
    private OnDatePickListener listener;

    public WheelDatePickDialog(@NonNull Context context, String oldCalendar) {
        super(context);

        setContentView(R.layout.dialog_wheel_date_pick);

        this.calendar = DateUtils.str2calendar(oldCalendar);
        if (this.calendar == null) {
            this.calendar = Calendar.getInstance();
        }

        initView();
        initData();
    }

    public void setListener(OnDatePickListener listener) {
        this.listener = listener;
    }

    private void initView() {
        int count = 0;
        if (showYear) count++;
        if (showMonth) count++;
        if (showDay) count++;
        if (showHour) count++;
        if (showMinute) count++;
        if (count == 0) throw new RuntimeException("年月日至少得显示一个");
        wheelWidth = ScreenUtils.getScreenWidth() / (count);
        dialog_title = findViewById(R.id.dialog_title);
        ll_container = findViewById(R.id.ll_container);
        btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(v -> done());

        dialog_title.setTitle(title);
        dialog_title.setOnClickListener(v -> dismiss());
        ll_container.removeAllViews();
        if (showYear) {
            wheel_year = new WheelView<>(getContext());
            wheel_year.setOnWheelSelectedListener(this::onYearSelected);
            initWheel(wheel_year);
        }
        if (showMonth) {
            wheel_month = new WheelView<>(getContext());
            wheel_month.setOnWheelSelectedListener(this::onMonthSelected);
            initWheel(wheel_month);
        }
        if (showDay) {
            wheel_day = new WheelView<>(getContext());
            wheel_day.setOnWheelSelectedListener(this::onDaySelected);
            initWheel(wheel_day);
        }
        if (showHour) {
            wheel_hour = new WheelView<>(getContext());
            wheel_hour.setOnWheelSelectedListener(this::onHourSelected);
            initWheel(wheel_hour);
        }
        if (showMinute) {
            wheel_minute = new WheelView<>(getContext());
            wheel_minute.setOnWheelSelectedListener(this::onMinuteSelected);
            initWheel(wheel_minute);
        }
    }

    private void initWheel(WheelView<String> wheel) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                wheelWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        wheel.setLayoutParams(params);
        ll_container.addView(wheel);
    }

    private void initData() {
        if (showYear) {
            // 初始化年
            int curYear = calendar.get(Calendar.YEAR);
            ArrayList<String> years = new ArrayList<>();
            for (int i = curYear - 1000; i < curYear + 1000; i++) {
                years.add(String.valueOf(i));
            }
            wheel_year.setItems(years);
            wheel_year.getRv().scrollToPosition(years.indexOf(String.valueOf(curYear)) - 2);
        }

        if (showMonth) {
            // 初始化月，不会变1~12
            ArrayList<String> months = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                months.add(String.valueOf(i));
            }
            wheel_month.setItems(months);
            int curMonth = calendar.get(Calendar.MONTH) + 1;
            wheel_month.getRv().scrollToPosition(curMonth - 1);
        }
        if (showDay) {
            // 初始化日
            updateDayCount();
            int curDay = calendar.get(Calendar.DAY_OF_MONTH);
            wheel_day.getRv().scrollToPosition(curDay - 1);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        }
        if (showHour) {
            ArrayList<String> hours = new ArrayList<>();
            for (int i = 0; i <= 23; i++) {
                hours.add(String.valueOf(i));
            }
            wheel_hour.setItems(hours);
            wheel_hour.getRv().scrollToPosition(hours.indexOf(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))) - 2);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
        }
        if (showMinute) {
            ArrayList<String> minutes = new ArrayList<>();
            for (int i = 0; i <= 59; i++) {
                minutes.add(String.valueOf(i));
            }
            wheel_minute.setItems(minutes);
            wheel_minute.getRv().scrollToPosition(minutes.indexOf(String.valueOf(calendar.get(Calendar.MINUTE))) - 2);
        } else {
            calendar.set(Calendar.MINUTE, 0);
        }
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private void onYearSelected(String year) {
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        updateDayCount();
    }

    private void onMonthSelected(String month) {
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        updateDayCount();
    }

    private void onDaySelected(String day) {
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
    }

    private void onHourSelected(String hour) {
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
    }

    private void onMinuteSelected(String minute) {
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
    }

    // 更新不同年月的 月总天数
    private void updateDayCount() {
        if (!showDay) return;
        int monthMaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.i("DDD", "updateDayCount: " + DateUtils.calendar2str(calendar) + " : " + monthMaxDay);

        ArrayList<String> days = new ArrayList<>();
        for (int i = 1; i <= monthMaxDay; i++) {
            days.add(String.valueOf(i));
        }

        wheel_day.setItems(days);
    }

    private void done() {
        if (listener != null) {
            listener.onDatePick(calendar);
        }
        dismiss();
    }


}
