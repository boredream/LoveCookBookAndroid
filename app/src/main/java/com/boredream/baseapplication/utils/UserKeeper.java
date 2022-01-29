package com.boredream.baseapplication.utils;

import com.blankj.utilcode.util.SPUtils;
import com.boredream.baseapplication.activity.LoginActivity;
import com.boredream.baseapplication.base.BaseActivity;
import com.boredream.baseapplication.entity.User;
import com.google.gson.Gson;

public class UserKeeper {

    private static volatile UserKeeper singleton = null;
    private final SPUtils spUtils;

    public static UserKeeper getSingleton() {
        if (singleton == null) {
            synchronized (UserKeeper.class) {
                if (singleton == null) {
                    singleton = new UserKeeper();
                }
            }
        }
        return singleton;
    }

    private UserKeeper() {
        spUtils = SPUtils.getInstance();
    }

    private User user;

    public void setUser(User user) {
        this.user = user;
        spUtils.put("user", new Gson().toJson(user));
    }

    public User getUser() {
        if (this.user == null) {
            try {
                this.user = new Gson().fromJson(spUtils.getString("user"), User.class);
            } catch (Exception e) {
                //
            }
        }
        return this.user;
    }

    public void clear() {
        this.user = null;
        spUtils.remove("user");
    }

    public void logout(BaseActivity activity) {
        TokenKeeper.getSingleton().clear();
        clear();
        LoginActivity.start(activity);
    }
}
