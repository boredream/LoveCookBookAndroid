package com.boredream.baseapplication.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.activity.InviteCpActivity;
import com.boredream.baseapplication.activity.TheDayEditActivity;
import com.boredream.baseapplication.activity.UserInfoActivity;
import com.boredream.baseapplication.adapter.TheDayAdapter;
import com.boredream.baseapplication.base.BaseFragment;
import com.boredream.baseapplication.entity.TheDay;
import com.boredream.baseapplication.entity.User;
import com.boredream.baseapplication.entity.dto.PageResultDTO;
import com.boredream.baseapplication.entity.event.TheDayUpdateEvent;
import com.boredream.baseapplication.entity.event.UserUpdateEvent;
import com.boredream.baseapplication.listener.OnSelectedListener;
import com.boredream.baseapplication.net.GlideHelper;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.utils.DateUtils;
import com.boredream.baseapplication.utils.DialogUtils;
import com.boredream.baseapplication.utils.UserKeeper;
import com.boredream.baseapplication.view.decoration.LastMarginItemDecoration;
import com.boredream.baseapplication.view.loading.RefreshListLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TheDayFragment extends BaseFragment implements OnSelectedListener<TheDay> {

    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.tv_together_days)
    TextView tvTogetherDays;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.iv_left)
    ImageView ivLeft;
    @BindView(R.id.rll)
    RefreshListLayout rll;
    @BindView(R.id.iv_right_add)
    ImageView ivRightAdd;

    private int curPage;
    private ArrayList<TheDay> infoList = new ArrayList<>();
    private TheDayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(activity, R.layout.frag_the_day, null);
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
    public void onTheDayUpdateEvent(TheDayUpdateEvent event) {
        loadData(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserUpdateEvent(UserUpdateEvent event) {
        initData();
    }

    private void initView() {
        tvTogetherDays.setOnClickListener(v -> updateTogetherDate());
        tv.setOnClickListener(v -> updateTogetherDate());

        rll.setEnableRefresh(true);
        rll.setEnableLoadmore(false);
        rll.setOnRefreshListener(refresh -> loadData(false));
        rll.setOnLoadmoreListener(refresh -> loadData(true));
        rll.getRv().setLayoutManager(new LinearLayoutManager(activity));
        rll.getRv().setAdapter(adapter = new TheDayAdapter(infoList));
        rll.getRv().addItemDecoration(new LastMarginItemDecoration());
        adapter.setOnItemClickListener(this);
        rll.setOnDefaultAction(new RefreshListLayout.OnDefaultActionListener() {
            @Override
            public void onRefresh() {
                loadData(false);
            }

            @Override
            public void onCreate() {
                onAdd();
            }
        });
    }

    private void initData() {
        setHeadInfo();
        loadData(false);
    }

    private void setHeadInfo() {
        setTogetherDays();

        User user = UserKeeper.getSingleton().getUser();
        GlideHelper.loadAvatar(ivLeft, user);
        ivLeft.setOnClickListener(v -> UserInfoActivity.start(activity));

        User cpUser = user.getCpUser();
        if (cpUser != null) {
            GlideHelper.loadAvatar(ivRight, cpUser);
            ivRightAdd.setVisibility(View.GONE);
            ivRight.setOnClickListener(null);
        } else {
            ivRight.setImageDrawable(null);
            ivRightAdd.setVisibility(View.VISIBLE);
            ivRight.setOnClickListener(v -> InviteCpActivity.start(activity));
        }
    }

    private void setTogetherDays() {
        User user = UserKeeper.getSingleton().getUser();
        String bothTogetherDate = user.getBothTogetherDate();
        if (bothTogetherDate != null) {
            tv.setText("我们已恋爱");
            int days = DateUtils.calculateDayDiff(Calendar.getInstance(), DateUtils.str2calendar(bothTogetherDate));
            tvTogetherDays.setText(String.valueOf(days));
        } else {
            tv.setText("点我设置时间");
            tvTogetherDays.setText("1");
        }
    }

    private void updateTogetherDate() {
        User user = UserKeeper.getSingleton().getUser();
        String oldData = user.getBothTogetherDate();

        DialogUtils.showCalendarPickDialog(activity, oldData, calendar -> {
            String date = DateUtils.calendar2str(calendar);
            User newUser = new User();
            newUser.setCpTogetherDate(date);

            HttpRequest.getInstance()
                    .getApiService()
                    .putUser(user.getId(), newUser)
                    .compose(RxComposer.commonProgress(this))
                    .subscribe(new SimpleObserver<String>() {
                        @Override
                        public void onNext(String s) {
                            user.setCpTogetherDate(date);
                            setTogetherDays();
                            showTip("设置成功");
                        }
                    });
        });
    }

    private void loadData(boolean loadMore) {
        int page;
        if (!loadMore) {
            page = curPage = 1;
        } else {
            page = curPage + 1;
        }

        HttpRequest.getInstance()
                .getApiService()
                .getTheDayPage(page, 20)
                .compose(RxComposer.commonRefresh(this, rll, loadMore))
                .subscribe(new SimpleObserver<PageResultDTO<TheDay>>() {
                    @Override
                    public void onNext(PageResultDTO<TheDay> response) {
                        curPage = page;
                        if (curPage == 1) {
                            infoList.clear();
                        }

                        infoList.addAll(response.getRecords());
                        rll.setEnableLoadmore(response.getCurrent() < response.getPages());
                        rll.checkEmpty();
                    }
                });
    }

    @Override
    public void onSelected(TheDay data) {
        TheDayEditActivity.start(activity, data);
    }

    @OnClick(R.id.fab)
    public void onAdd() {
        TheDayEditActivity.start(activity, null);
    }
}