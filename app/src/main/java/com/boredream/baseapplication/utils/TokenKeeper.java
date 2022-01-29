package com.boredream.baseapplication.utils;

import com.blankj.utilcode.util.SPUtils;

public class TokenKeeper {

    private static volatile TokenKeeper singleton = null;
    private final SPUtils spUtils;

    public static TokenKeeper getSingleton() {
        if (singleton == null) {
            synchronized (TokenKeeper.class) {
                if (singleton == null) {
                    singleton = new TokenKeeper();
                }
            }
        }
        return singleton;
    }

    private TokenKeeper() {
        spUtils = SPUtils.getInstance();
    }

    private String token;

    public void setToken(String token) {
        this.token = token;
        spUtils.put("token", token);
    }

    public String getToken() {
        if (this.token == null) {
            this.token = spUtils.getString("token");
        }
        return this.token;
    }

    public void clear() {
        this.token = null;
        spUtils.remove("token");
    }
}
