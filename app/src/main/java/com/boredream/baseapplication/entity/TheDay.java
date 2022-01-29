package com.boredream.baseapplication.entity;

/**
 * <p>
 * 纪念日
 * </p>
 *
 * @author boredream
 */
public class TheDay extends Belong2UserEntity {


    /**
     * 提醒方式 累计天数
     */
    public static final int NOTIFY_TYPE_TOTAL_COUNT = 1;

    /**
     * 提醒方式 按年倒计天数
     */
    public static final int NOTIFY_TYPE_YEAR_COUNT_DOWN = 2;

    // 名称
    private String name;

    // 纪念日期
    private String theDayDate;

    // 提醒方式
    private int notifyType;

    public void setNotifyTypeStr(String data) {
        if ("每年倒数".equals(data)) {
            notifyType = NOTIFY_TYPE_YEAR_COUNT_DOWN;
        } else if ("累计天数".equals(data)) {
            notifyType = NOTIFY_TYPE_TOTAL_COUNT;
        } else {
            notifyType = 0;
        }
    }

    public String getNotifyTypeStr() {
        if (NOTIFY_TYPE_YEAR_COUNT_DOWN == notifyType) {
            return "每年倒数";
        } else if (NOTIFY_TYPE_TOTAL_COUNT == notifyType) {
            return "累计天数";
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTheDayDate() {
        return theDayDate;
    }

    public void setTheDayDate(String theDayDate) {
        this.theDayDate = theDayDate;
    }

    public int getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(int notifyType) {
        this.notifyType = notifyType;
    }

}
