package com.boredream.baseapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.boredream.baseapplication.R;
import com.boredream.baseapplication.activity.AboutActivity;
import com.boredream.baseapplication.activity.FeedBackActivity;
import com.boredream.baseapplication.activity.InviteCpActivity;
import com.boredream.baseapplication.activity.SettingActivity;
import com.boredream.baseapplication.activity.UserInfoActivity;
import com.boredream.baseapplication.adapter.SettingItemAdapter;
import com.boredream.baseapplication.base.BaseFragment;
import com.boredream.baseapplication.entity.SettingItem;
import com.boredream.baseapplication.entity.User;
import com.boredream.baseapplication.entity.event.UserUpdateEvent;
import com.boredream.baseapplication.listener.OnSelectedListener;
import com.boredream.baseapplication.net.GlideHelper;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.utils.DateUtils;
import com.boredream.baseapplication.utils.DialogUtils;
import com.boredream.baseapplication.utils.UserKeeper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MineFragment extends BaseFragment implements OnSelectedListener<SettingItem> {

    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_id)
    TextView tvId;
    @BindView(R.id.rv_items)
    RecyclerView rvItems;
    @BindView(R.id.btn_logout)
    Button btnLogout;

    private SettingItemAdapter adapter;
    private SettingItem cpSettingItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(activity, R.layout.frag_mine, null);
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
    public void onUserUpdateEvent(UserUpdateEvent event) {
        setUserInfo();
    }

    private void initView() {
        rvItems.setLayoutManager(new LinearLayoutManager(activity));
        ivAvatar.setOnClickListener(v -> UserInfoActivity.start(activity));
    }

    private void initData() {
        setSettingItems();
        setUserInfo();
    }

    private void setSettingItems() {
        cpSettingItem = new SettingItem(R.drawable.ic_setting_cp, "?????????", null, false);
        List<SettingItem> settingList = Arrays.asList(cpSettingItem,
                new SettingItem(R.drawable.ic_setting_love, "?????????", null, false),
                new SettingItem(R.drawable.ic_setting_recommend, "???????????????", null, false),
                new SettingItem(R.drawable.ic_setting_about, "????????????", null, false),
                new SettingItem(R.drawable.ic_setting_feed, "??????", null, false)
        );
        adapter = new SettingItemAdapter(settingList);
        rvItems.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    private void setUserInfo() {
        User user = UserKeeper.getSingleton().getUser();
        GlideHelper.loadAvatar(ivAvatar, user);
        tvName.setText(user.getNickname());
        tvId.setText(String.format(Locale.getDefault(), "ID: %s", user.getShowId()));

        // cp
        User cpUser = user.getCpUser();
        if (cpUser != null) {
            cpSettingItem.setRightText("??????");
            cpSettingItem.setShowRightImage(true);
            cpSettingItem.setRightImage(cpUser.getAvatar());
            cpSettingItem.setRightImageDefault(cpUser.getAvatarDefaultImg());
        } else {
            cpSettingItem.setRightText("??????");
            cpSettingItem.setShowRightImage(false);
            cpSettingItem.setRightImage(null);
            cpSettingItem.setRightImageDefault(null);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSelected(SettingItem data) {
        User user = UserKeeper.getSingleton().getUser();
        User cpUser = user.getCpUser();
        switch (data.getName()) {
            case "?????????":
                if (cpUser != null) {
                    // ??????
                    DialogUtils.show2BtnDialog(activity, "??????", "???????????????????????????",
                            (view) -> unbindCp(cpUser.getId()));
                } else {
                    // ??????
                    InviteCpActivity.start(activity);
                }
                break;
            case "?????????":
                if (cpUser != null) {
                    String str = "???????????????" + cpUser.getNickname() + "??????????????????????????????";
                    String bothTogetherDate = user.getBothTogetherDate();
                    if (bothTogetherDate != null) {
                        int days = DateUtils.calculateDayDiff(Calendar.getInstance(), DateUtils.str2calendar(bothTogetherDate));
                        str += "??????????????? " + days + " ?????????";
                    }
                    share(str);
                } else {
                    showTip("?????????????????????");
                }
                break;
            case "???????????????":
                share("??????????????????????????????????????????????????????");
                break;
            case "????????????":
                AboutActivity.start(activity);
                break;
            case "??????":
                SettingActivity.start(activity);
                break;
        }
    }

    private void share(String str) {
        str += "??????????????? https://www.pgyer.com/lovecookbook";

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, str);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void unbindCp(Long cpUserId) {
        HttpRequest.getInstance()
                .getApiService()
                .unbindUserCp(cpUserId)
                .compose(RxComposer.commonProgress(this))
                .subscribe(new SimpleObserver<Boolean>() {
                    @Override
                    public void onNext(Boolean response) {
                        // ???????????????????????????????????????
                        User user = UserKeeper.getSingleton().getUser();
                        user.setCpUserId(null);
                        user.setCpUser(null);
                        UserKeeper.getSingleton().setUser(user);

                        EventBus.getDefault().post(new UserUpdateEvent());
                        showTip("?????????????????????");
                    }
                });
    }

    @OnClick(R.id.btn_logout)
    public void onClick() {
        DialogUtils.show2BtnDialog(activity, "??????", "???????????????????????????",
                (view) -> UserKeeper.getSingleton().logout(activity));
    }
}