package com.boredream.baseapplication.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MockUtils {

    public static String getImgUrl() {
        List<String> imgs = new ArrayList<>();
        imgs.add("http://img4.duitang.com/uploads/item/201408/19/20140819172216_zPzZT.jpeg");
        imgs.add("http://img4.duitang.com/uploads/item/201404/16/20140416232354_VFTyY.jpeg");
        imgs.add("http://img3.duitang.com/uploads/item/201506/20/20150620224057_Ua4uL.jpeg");
        imgs.add("http://s.image.hnol.net/x/900x0/auto/http://image.hnol.net/c/2011-11/08/16/201111081649429421-1988454.jpg");
        imgs.add("http://img4.duitang.com/uploads/item/201502/25/20150225134028_XW3WC.thumb.700_0.jpeg");
        imgs.add("http://imgsrc.baidu.com/forum/pic/item/d1160924ab18972bb10a4e83e6cd7b899e510a42.jpg");
        imgs.add("http://img5q.duitang.com/uploads/item/201501/23/20150123231841_wCENF.jpeg");
        imgs.add("http://img4.duitang.com/uploads/item/201511/06/20151106141943_4tPrv.jpeg");
        return imgs.get(new Random().nextInt(imgs.size()));
    }

    public static String getVideoUrl() {
        List<String> imgs = new ArrayList<>();
        imgs.add("http://baobab.wdjcdn.com/1455782903700jy.mp4");
        imgs.add("http://baobab.wdjcdn.com/145076769089714.mp4");
        imgs.add("http://baobab.wdjcdn.com/14564977406580.mp4");
        return imgs.get(new Random().nextInt(imgs.size()));
    }

    public static String getMusicUrl() {
        List<String> imgs = new ArrayList<>();
        imgs.add("http://om5.alicdn.com/450/10450/56300/1073345_73731_l.mp3?auth_key=1d4f10afb733a4165686066a7f0852f8-1498446000-0-null");
        imgs.add("http://audio.xmcdn.com/group11/M08/3A/55/wKgDbVWbJuzz1oTfACgSj0ux7aQ198.m4a");
        imgs.add("http://116.224.86.38/m10.music.126.net/20170619145949/6cca39eb432ce464b14a3cf38569a135/ymusic/f171/0330/08ff/fbec8c9301dcaa80924033ea229d600c.mp3");
        imgs.add("http://116.224.86.36/m10.music.126.net/20170619151520/c4282deb88ece1d69c2c282b25ab1e64/ymusic/0b23/006a/51bf/0d8255acc1634702e1a184d446f91b88.mp3");
        imgs.add("http://116.224.86.21/m10.music.126.net/20170619151622/6d07119b03a7beb784e4f9f00e34ac2b/ymusic/ed04/efbf/0bef/e9c8fd8c545072fd0ddcf377582c6f8f.mp3");
        return imgs.get(new Random().nextInt(imgs.size()));
    }

    public static <T> T mockData(Class<T> clazz) {
        T t = null;
        try {
            t = clazz.newInstance();
            setValue(t, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> List<T> mockList(Class<T> clazz) {
        return mockList(clazz, 10);
    }

    public static <T> List<T> mockList(Class<T> clazz, int count) {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            T t = mockData(clazz);
            if (t != null) {
                list.add(t);
            }
        }
        return list;
    }

    private static <T> void setValue(Object object, Class<T> clazz) throws IllegalAccessException {
        Field[] superFields = clazz.getSuperclass().getDeclaredFields();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : superFields) {
            setFieldData(object, field);
        }
        for (Field field : fields) {
            setFieldData(object, field);
        }
    }

    private static void setFieldData(Object object, Field field) throws IllegalAccessException {
        if (Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
            return;
        }

        Object value = getFieldMockValue(field);
        if (value != null) {
            field.setAccessible(true);
            field.set(object, value);
        }
    }

    /**
     * 模拟基础类型数据
     */
    private static Object getFieldMockValue(Field field) {
        Class clazzType = field.getType();
        Object value = null;
        if (clazzType == int.class || clazzType == Integer.class) {
            value = new Random().nextInt(100);
        } else if (clazzType == long.class || clazzType == Long.class) {
            value = Math.abs(new Random().nextLong() % 10000);
        } else if (clazzType == float.class || clazzType == Float.class) {
            value = new Random().nextFloat();
        } else if (clazzType == double.class || clazzType == Double.class) {
            value = new Random().nextDouble();
        } else if (clazzType == String.class) {
            String name = field.getName();
            // 可添加修改更多规则
            if (name.toLowerCase().contains("video")) {
                value = getVideoUrl();
            } else if (name.toLowerCase().contains("music") || name.toLowerCase().contains("audio")) {
                value = getMusicUrl();
            } else if (name.toLowerCase().contains("img") || name.toLowerCase().contains("url")) {
                value = getImgUrl();
            } else {
                value = name + "_" + new Random().nextInt(100);
            }
        } else if (clazzType == boolean.class || clazzType == Boolean.class) {
            value = new Random().nextBoolean();
        }
        return value;
    }
}
