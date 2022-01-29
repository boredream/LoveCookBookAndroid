package com.boredream.baseapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.StringUtils;
import com.boredream.baseapplication.R;
import com.boredream.baseapplication.adapter.SettingItemAdapter;
import com.boredream.baseapplication.base.BaseResponse;
import com.boredream.baseapplication.dialog.BottomListSelectedDialog;
import com.boredream.baseapplication.dialog.WheelDatePickDialog;
import com.boredream.baseapplication.entity.SettingItem;
import com.boredream.baseapplication.entity.User;
import com.boredream.baseapplication.entity.event.UserUpdateEvent;
import com.boredream.baseapplication.image.picker.PickImageActivity;
import com.boredream.baseapplication.image.upload.ImageRequestUtils;
import com.boredream.baseapplication.listener.OnSelectedListener;
import com.boredream.baseapplication.net.HttpRequest;
import com.boredream.baseapplication.net.RxComposer;
import com.boredream.baseapplication.net.SimpleObserver;
import com.boredream.baseapplication.utils.DateUtils;
import com.boredream.baseapplication.utils.DialogUtils;
import com.boredream.baseapplication.utils.UserKeeper;
import com.boredream.baseapplication.view.TitleBar;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;

public class UserInfoActivity extends PickImageActivity implements OnSelectedListener<SettingItem> {

    @BindView(R.id.title_bar)
    TitleBar titleBar;
    @BindView(R.id.rv_items)
    RecyclerView rvItems;

    private List<SettingItem> settingList;
    private SettingItemAdapter adapter;
    private User newUser;

    public static void start(Context context) {
        Intent intent = new Intent(context, UserInfoActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        initView();
        loadData();
    }

    private void initView() {
        titleBar.setTitle("个人资料").setLeftBack().setRight("完成", v -> commit());
        rvItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void commit() {
        User user = UserKeeper.getSingleton().getUser();
        user.setAvatar(settingList.get(0).getRightImage());
        user.setNickname(settingList.get(1).getRightText());
        user.setGender(settingList.get(2).getRightText());
        user.setBirthday(settingList.get(3).getRightText());

        ImageRequestUtils.checkImage4update(user)
                .flatMap((Function<User, ObservableSource<BaseResponse<String>>>) newUser -> {
                    UserInfoActivity.this.newUser = newUser;
                    return HttpRequest.getInstance()
                            .getApiService()
                            .putUser(newUser.getId(), newUser);
                })
                .compose(RxComposer.commonProgress(this))
                .subscribe(new SimpleObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        UserKeeper.getSingleton().setUser(newUser);
                        EventBus.getDefault().post(new UserUpdateEvent());
                        showTip("修改成功");
                        finish();
                    }
                });
    }

    private void loadData() {
        User user = UserKeeper.getSingleton().getUser();
        SettingItem avatarSettingItem = new SettingItem(null, "头像", null, true);
        avatarSettingItem.setShowRightImage(true);
        avatarSettingItem.setRightImage(user.getAvatar());
        avatarSettingItem.setRightImageDefault(user.getAvatarDefaultImg());
        settingList = Arrays.asList(
                avatarSettingItem,
                new SettingItem(null, "昵称", user.getNickname(), true),
                new SettingItem(null, "性别", user.getGender(), true),
                new SettingItem(null, "生日", user.getBirthday(), true)
        );
        adapter = new SettingItemAdapter(settingList);
        rvItems.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    protected void onSinglePickImageResult(@NonNull String path) {
        settingList.get(0).setRightImage(path);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSelected(SettingItem data) {
        switch (data.getName()) {
            case "头像":
                DialogUtils.showImagePickDialog(this);
                break;
            case "昵称":
                EditTextActivity.start(this, data.getName(), data.getRightText());
                break;
            case "性别":
                onGenderClick(data);
                break;
            case "生日":
                onBirthdayClick(data);
                break;
        }
    }

    private void onGenderClick(SettingItem item) {
        // TODO: chunyang 11/29/21 代码重复
        ArrayList<String> list = new ArrayList<>();
        list.add("男");
        list.add("女");
        BottomListSelectedDialog<String> dialog = new BottomListSelectedDialog<>(
                this, item.getName(), list, item.getRightText());
        dialog.setOnListSelectedListener(data -> {
            item.setRightText(data);
            adapter.notifyDataSetChanged();
        });
        dialog.show();
    }

    private void onBirthdayClick(SettingItem item) {
        WheelDatePickDialog dialog = new WheelDatePickDialog(
                this, item.getRightText());
        dialog.setListener(calendar -> {
            String date = DateUtils.calendar2str(calendar);
            item.setRightText(date);
            adapter.notifyDataSetChanged();
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String nickname = EditTextActivity.getActivityResult(requestCode, resultCode, data);
        if (!StringUtils.isEmpty(nickname)) {
            settingList.get(1).setRightText(nickname);
            adapter.notifyDataSetChanged();
        }
    }

}