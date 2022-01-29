package com.boredream.baseapplication.entity;

public class SettingItem {

    private Integer icon;
    private String name;
    private String rightText;
    private boolean showRightImage;
    private String rightImage;
    private Integer rightImageDefault;
    private boolean showRightArrow;

    public SettingItem(Integer icon, String name, String rightText, boolean showRightArrow) {
        this.icon = icon;
        this.name = name;
        this.rightText = rightText;
        this.showRightArrow = showRightArrow;
    }

    public Integer getIcon() {
        return icon;
    }

    public void setIcon(Integer icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public boolean isShowRightImage() {
        return showRightImage;
    }

    public void setShowRightImage(boolean showRightImage) {
        this.showRightImage = showRightImage;
    }

    public String getRightImage() {
        return rightImage;
    }

    public void setRightImage(String rightImage) {
        this.rightImage = rightImage;
    }

    public Integer getRightImageDefault() {
        return rightImageDefault;
    }

    public void setRightImageDefault(Integer rightImageDefault) {
        this.rightImageDefault = rightImageDefault;
    }

    public boolean isShowRightArrow() {
        return showRightArrow;
    }

    public void setShowRightArrow(boolean showRightArrow) {
        this.showRightArrow = showRightArrow;
    }
}
