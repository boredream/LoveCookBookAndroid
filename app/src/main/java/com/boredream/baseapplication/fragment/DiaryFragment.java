package com.boredream.baseapplication.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.boredream.baseapplication.R;
import com.boredream.baseapplication.activity.DiaryEditActivity;
import com.boredream.baseapplication.adapter.DiaryAdapter;
import com.boredream.baseapplication.adapter.DiaryCalendarAdapter;
import com.boredream.baseapplication.base.BaseFragment;
import com.boredream.baseapplication.entity.Diary;
import com.boredream.baseapplication.entity.dto.PageResultDTO;
import com.boredream.baseapplication.entity.event.DiaryUpdateEvent;
import com.boredream.baseapplication.entity.event.UserUpdateEvent;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.utils.DateUtils;
import com.boredream.baseapplication.utils.DialogUtils;
import com.boredream.baseapplication.view.TitleBar;
import com.boredream.baseapplication.view.decoration.LastMarginItemDecoration;
import com.boredream.baseapplication.view.decoration.LeftPaddingItemDecoration;
import com.boredream.baseapplication.view.loading.RefreshListLayout;
import com.haibin.calendarview.CalendarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DiaryFragment extends BaseFragment {

    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.rll_list)
    RefreshListLayout rllList;
    @BindView(R.id.cl_calendar)
    CoordinatorLayout clCalendar;
    @BindView(R.id.calendarView)
    CalendarView calendarView;
    @BindView(R.id.rll_calendar)
    RefreshListLayout rllCalendar;
    @BindView(R.id.iv_pre_month)
    ImageView ivPreMonth;
    @BindView(R.id.tv_year_month)
    TextView tvYearMonth;
    @BindView(R.id.iv_next_month)
    ImageView ivNextMonth;

    private boolean showCalendar;

    private int listCurPage;
    private ArrayList<Diary> listInfoList = new ArrayList<>();

    private Calendar selectedDay;
    private HashMap<String, ArrayList<Diary>> allMonthCalendarInfoList = new HashMap<>();
    private ArrayList<Diary> calendarInfoList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(activity, R.layout.frag_diary, null);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initView();
        initData();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DiaryUpdateEvent event) {
        refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserUpdateEvent(UserUpdateEvent event) {
        refresh();
    }

    private void initView() {
        titleBar.setTitle("日记")
                .setLeftMode()
                .setRight("", v -> changeShowCalendar(!showCalendar));

        rllList.setEnableRefresh(true);
        rllList.setEnableLoadmore(false);
        rllList.setOnRefreshListener(refresh -> loadListData(false));
        rllList.setOnLoadmoreListener(refresh -> loadListData(true));
        rllList.getRv().setLayoutManager(new LinearLayoutManager(activity));
        rllList.getRv().setAdapter(new DiaryAdapter(listInfoList));
        rllList.getRv().addItemDecoration(new LastMarginItemDecoration());
        rllList.setOnDefaultAction(new RefreshListLayout.OnDefaultActionListener() {
            @Override
            public void onRefresh() {
                refresh();
            }

            @Override
            public void onCreate() {
                onAdd();
            }
        });

        rllCalendar.setEnableRefresh(false);
        rllCalendar.setEnableLoadmore(false);
        rllCalendar.getRv().setOverScrollMode(View.OVER_SCROLL_NEVER);
        rllCalendar.getRv().setLayoutManager(new LinearLayoutManager(activity));
        rllCalendar.getRv().setAdapter(new DiaryCalendarAdapter(calendarInfoList));
        rllCalendar.getRv().addItemDecoration(new LeftPaddingItemDecoration(activity));
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(com.haibin.calendarview.Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(com.haibin.calendarview.Calendar calendar, boolean isClick) {
                selectedDay.setTimeInMillis(calendar.getTimeInMillis());
                tvYearMonth.setText(getCurYearMonth());
                loadCalendarData(false);
            }
        });
        ivPreMonth.setOnClickListener(v -> calendarView.scrollToPre());
        ivNextMonth.setOnClickListener(v -> calendarView.scrollToNext());
        tvYearMonth.setOnClickListener(v -> {
            String oldData = DateUtils.calendar2str(selectedDay);
            DialogUtils.showCalendarPickDialog(activity, oldData, calendar ->
                    calendarView.scrollToCalendar(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH) + 1,
                            calendar.get(Calendar.DAY_OF_MONTH)));
        });

        selectedDay = Calendar.getInstance();
        tvYearMonth.setText(getCurYearMonth());
    }

    private void changeShowCalendar(boolean showCalendar) {
        this.showCalendar = showCalendar;
        titleBar.getTvRight().setText(showCalendar ? "列表模式" : "日历模式");
        Drawable drawable = getResources().getDrawable(showCalendar
                ? R.drawable.ic_diary_list
                : R.drawable.ic_diary_calendar);
        titleBar.getTvRight().setDrawables(new Drawable[]{drawable, null, null, null});
        clCalendar.setVisibility(showCalendar ? View.VISIBLE : View.GONE);
        rllList.setVisibility(showCalendar ? View.GONE : View.VISIBLE);

        // 切换样式的时候重新拉取数据
        refresh();
    }

    private void refresh() {
        if (showCalendar) {
            loadCalendarData(true);
        } else {
            loadListData(false);
        }
    }

    private void initData() {
        changeShowCalendar(false);
    }

    private void loadCalendarData(boolean skipLocal) {
        boolean success = false;
        String curYearMonth = getCurYearMonth();

        if (!skipLocal) {
            // 先从缓存取当月数据
            success = loadDayDateFromLocal(curYearMonth);
        }

        if (!success) {
            // 无数据，从服务器拉取
            loadMonthDataFromRemote(curYearMonth);
        }
    }

    private String getCurYearMonth() {
        return DateUtils.calendar2str(selectedDay, "yyyy-MM");
    }

    private boolean loadDayDateFromLocal(String curYearMonth) {
        // 先从缓存取当月数据
        ArrayList<Diary> list = allMonthCalendarInfoList.get(curYearMonth);
        if (list == null) {
            return false;
        }

        // 有数据，匹配到当天
        calendarInfoList.clear();
        for (Diary diary : list) {
            if (DateUtils.isSameDay(selectedDay, DateUtils.str2calendar(diary.getDiaryDate()))) {
                calendarInfoList.add(diary);
            }
        }
        rllCalendar.checkEmpty();
        return true;
    }

    private void loadMonthDataFromRemote(String curYearMonth) {
        HttpRequest.getInstance()
                .getApiService()
                .getDiaryMonth(curYearMonth, 1, 999)
                .compose(RxComposer.commonRefresh(this, rllCalendar, false))
                .subscribe(new SimpleObserver<PageResultDTO<Diary>>() {
                    @Override
                    public void onNext(PageResultDTO<Diary> response) {
                        // 获取成功后，存入月信息
                        allMonthCalendarInfoList.put(curYearMonth, new ArrayList<>(response.getRecords()));

                        // 更新日历红点提示
                        updateCalendarHint();

                        // 刷新列表，再次从本地获取显示
                        loadDayDateFromLocal(curYearMonth);
                    }
                });
    }

    private void updateCalendarHint() {
        Map<String, com.haibin.calendarview.Calendar> schemaList = new HashMap<>();
        for (Map.Entry<String, ArrayList<Diary>> entry : allMonthCalendarInfoList.entrySet()) {
            ArrayList<Diary> list = entry.getValue();
            if (list != null) {
                for (Diary diary : list) {
                    Calendar calendar = DateUtils.str2calendar(diary.getDiaryDate());
                    com.haibin.calendarview.Calendar schema = new com.haibin.calendarview.Calendar();
                    schema.setYear(calendar.get(Calendar.YEAR));
                    schema.setMonth(calendar.get(Calendar.MONTH) + 1);
                    schema.setDay(calendar.get(Calendar.DAY_OF_MONTH));
                    schemaList.put(schema.toString(), schema);
                }
            }
        }
        calendarView.setSchemeDate(schemaList);
    }

    private void loadListData(boolean loadMore) {
        int page;
        if (!loadMore) {
            page = listCurPage = 1;
        } else {
            page = listCurPage + 1;
        }

        HttpRequest.getInstance()
                .getApiService()
                .getDiaryPage(page, 20)
                .compose(RxComposer.commonRefresh(this, rllList, loadMore))
                .subscribe(new SimpleObserver<PageResultDTO<Diary>>() {
                    @Override
                    public void onNext(PageResultDTO<Diary> response) {
                        listCurPage = page;
                        if (listCurPage == 1) {
                            listInfoList.clear();
                        }

                        listInfoList.addAll(response.getRecords());
                        rllList.setEnableLoadmore(response.getCurrent() < response.getPages());
                        rllList.checkEmpty();
                    }
                });
    }

    @OnClick(R.id.fab)
    public void onAdd() {
        DiaryEditActivity.start(activity, null);
    }
}