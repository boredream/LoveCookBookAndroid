package com.boredream.baseapplication.entity;


import com.boredream.baseapplication.base.BaseEntity;

public class UserProfile extends BaseEntity {

    // fire base
    protected String localId;

    // customer
    protected String nickname;
    protected String avatar;
    protected int gender;

    public String getLocalId() {
        return localId;
    }

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
