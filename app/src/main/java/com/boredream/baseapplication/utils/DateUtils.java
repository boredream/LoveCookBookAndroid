package com.boredream.baseapplication.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期时间工具类
 */
public class DateUtils {

    public static final long ONE_SECOND_MILLIONS = 1000;
    public static final long ONE_MINUTE_MILLIONS = 60 * ONE_SECOND_MILLIONS;
    public static final long ONE_HOUR_MILLIONS = 60 * ONE_MINUTE_MILLIONS;
    public static final long ONE_DAY_MILLIONS = 24 * ONE_HOUR_MILLIONS;

    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_TIME = "HH:mm";
    public static final String PATTERN_SPLIT = " ";
    public static final String PATTERN_DEF = PATTERN_DATE;

    /**
     * 计算天数差
     */
    public static int calculateDayDiff(Calendar targetTime, Calendar compareTime) {
        // 重置时分秒
        targetTime.set(Calendar.HOUR_OF_DAY, 0);
        targetTime.set(Calendar.MINUTE, 0);
        targetTime.set(Calendar.SECOND, 0);

        compareTime.set(Calendar.HOUR_OF_DAY, 0);
        compareTime.set(Calendar.MINUTE, 0);
        compareTime.set(Calendar.SECOND, 0);

        long targetTimeInMillis = targetTime.getTimeInMillis();
        long compareTimeInMillis = compareTime.getTimeInMillis();

        if (targetTimeInMillis > compareTimeInMillis) {
            return (int) (1f * (targetTimeInMillis - compareTimeInMillis) / ONE_DAY_MILLIONS + 0.5f);
        } else {
            return -(int) (1f * (compareTimeInMillis - targetTimeInMillis) / ONE_DAY_MILLIONS + 0.5f);
        }
    }

    /**
     * 计算天数差
     */
    public static float calculateDayDiffDetail(Calendar targetTime, Calendar compareTime) {
        // 重置时分秒
        targetTime.set(Calendar.HOUR_OF_DAY, 0);
        targetTime.set(Calendar.MINUTE, 0);
        targetTime.set(Calendar.SECOND, 0);

        compareTime.set(Calendar.HOUR_OF_DAY, 0);
        compareTime.set(Calendar.MINUTE, 0);
        compareTime.set(Calendar.SECOND, 0);

        long targetTimeInMillis = targetTime.getTimeInMillis();
        long compareTimeInMillis = compareTime.getTimeInMillis();

        return 1f * (targetTimeInMillis - compareTimeInMillis) / ONE_DAY_MILLIONS;
    }

    /**
     * 是否是今天
     */
    public static boolean isToday(Calendar targetTime) {
        return isSameDay(Calendar.getInstance(), targetTime);
    }

    /**
     * 是否是今天
     */
    public static boolean isToday(long targetTime) {
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeInMillis(targetTime);
        return isToday(targetCalendar);
    }

    /**
     * 是否为同一天
     */
    public static boolean isSameDay(long targetTime, long compareTime) {
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTimeInMillis(targetTime);

        Calendar compareCalendar = Calendar.getInstance();
        compareCalendar.setTimeInMillis(compareTime);

        return isSameDay(targetCalendar, compareCalendar);
    }

    /**
     * 是否为同一天
     */
    public static boolean isSameDay(Calendar targetTime, Calendar compareTime) {
        return targetTime.get(Calendar.YEAR) == compareTime.get(Calendar.YEAR) &&
                targetTime.get(Calendar.DAY_OF_YEAR) == compareTime.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isSameMonth(Calendar targetTime, Calendar compareTime) {
        return targetTime.get(Calendar.YEAR) == compareTime.get(Calendar.YEAR) &&
                targetTime.get(Calendar.MONTH) == compareTime.get(Calendar.MONTH);
    }

    public static Date str2date(String str, String format) {
        Date date = null;
        try {
            if (str != null) {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                date = sdf.parse(str);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date str2date(String str) {
        return str2date(str, PATTERN_DEF);
    }

    public static String date2str(Date date) {
        return date2str(date, PATTERN_DEF);
    }

    public static String date2str(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CHINA);
        return sdf.format(date);
    }

    public static Calendar str2calendar(String str) {
        Calendar calendar = null;
        Date date = str2date(str);
        if (date != null) {
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
        return calendar;
    }


    public static Calendar str2calendar(String str, String format) {
        Calendar calendar = null;
        Date date = str2date(str, format);
        if (date != null) {
            calendar = Calendar.getInstance();
            calendar.setTime(date);
        }
        return calendar;
    }

    public static String calendar2str(Calendar calendar) {
        return date2str(calendar.getTime());
    }

    public static String timeInMillsToDayStr(String timeInMills) {
        Date date = new Date(Long.parseLong(timeInMills));
        return date2str(date, PATTERN_DATE);
    }

    public static String timeInMillsToDayStr(Long timeInMills) {
        if (timeInMills == null) return null;
        return date2str(new Date(timeInMills), PATTERN_DATE);
    }

    public static String timeInMillsToDayMinuteStr(String timeInMills) {
        try {
            Date date = new Date(Long.parseLong(timeInMills));
            return date2str(date);
        } catch (Exception e) {
            return "";
        }
    }

    public static String timeInMillsToStr(String timeInMills, boolean showDay) {
        return timeInMillsToStr(timeInMills, showDay ? PATTERN_DEF : PATTERN_TIME);
    }

    public static String timeInMillsToStr(String timeInMills, String pattern) {
        try {
            Date date = new Date(Long.parseLong(timeInMills));
            return date2str(date, pattern);
        } catch (Exception e) {
            return "";
        }

    }

    public static String timeInMillsToDayMinuteStr(Long timeInMills) {
        if (timeInMills == null) return null;
        return date2str(new Date(timeInMills));
    }

    public static String calendar2str(Calendar calendar, String format) {
        if (calendar == null) return "";
        return date2str(calendar.getTime(), format);
    }

    public static String periodCalendar2Str(Long startTimeInMills, Long endTimeInMills, String pattern) {
        if (startTimeInMills == null || endTimeInMills == null) return "";

        SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return String.format("%s - %s", df.format(new Date(startTimeInMills)),
                df.format(new Date(endTimeInMills)));
    }

    public static String dateStr2Str(String fromPattern, String toPattern, String fromString) {
        try {
            Date date = new SimpleDateFormat(fromPattern, Locale.getDefault())
                    .parse(fromString);
            return new SimpleDateFormat(toPattern, Locale.getDefault()).format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    //获取今天0点0分0秒的信息
    public static Calendar getDayCalendar() {
        return clearToDayCalendar(Calendar.getInstance());
    }

    //清除时分秒毫秒信息
    public static Calendar clearToDayCalendar(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    //获取到本月1日0点0分0秒0毫秒的信息
    public static Calendar getMonthCalendar() {
        return clearToMonthCalendar(Calendar.getInstance());
    }

    public static Calendar getCalendar(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    public static Calendar getCalendar(String millis) {
        try {
            return getCalendar(Long.parseLong(millis));
        } catch (Exception e) {
            e.printStackTrace();
            return Calendar.getInstance();
        }
    }

    //获取到某月1日0点0分0秒0毫秒的信息
    public static Calendar clearToMonthCalendar(Calendar calendar) {
        clearToDayCalendar(calendar).set(Calendar.DAY_OF_MONTH, 1);
        return calendar;
    }

}
