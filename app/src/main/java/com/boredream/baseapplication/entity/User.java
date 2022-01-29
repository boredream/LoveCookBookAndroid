package com.boredream.baseapplication.entity;

import com.boredream.baseapplication.R;
import com.boredream.baseapplication.base.BaseEntity;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author boredream
 */
public class User extends BaseEntity {

    // 用户名
    private String username;

    // 密码
    private String password;

    // 角色
    private String role;

    // 第三方id
    private String openId;

    // 伴侣用户id
    private Long cpUserId;

    // 伴侣在一起时间
    private String cpTogetherDate;

    // 伴侣用户
    private User cpUser;

    // 昵称
    private String nickname;

    // 头像
    private String avatar;

    // 性别
    private String gender;

    // 生日
    private String birthday;

    public String getShowId() {
        Long id = getId();
        StringBuilder sb = new StringBuilder();
        int maxLength = 6;
        for (int i = 0; i < maxLength - String.valueOf(id).length(); i++) {
            sb.append("0");
        }
        sb.append(id);
        return sb.toString();
    }

    /**
     * 默认头像
     */
    public int getAvatarDefaultImg() {
        int defaultAvatar = R.drawable.avatar_girl;
        if("男".equals(gender)) {
            defaultAvatar = R.drawable.avatar_boy;
        }
        return defaultAvatar;
    }

    /**
     * 取自己或对方的在一起时间
     */
    public String getBothTogetherDate() {
        String date = cpTogetherDate;
        if (date == null && cpUser != null) {
            date = cpUser.getCpTogetherDate();
        }
        return date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getCpUserId() {
        return cpUserId;
    }

    public void setCpUserId(Long cpUserId) {
        this.cpUserId = cpUserId;
    }

    public User getCpUser() {
        return cpUser;
    }

    public void setCpUser(User cpUser) {
        this.cpUser = cpUser;
    }

    public String getCpTogetherDate() {
        return cpTogetherDate;
    }

    public void setCpTogetherDate(String cpTogetherDate) {
        this.cpTogetherDate = cpTogetherDate;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

}
