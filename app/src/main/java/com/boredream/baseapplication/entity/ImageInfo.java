package com.boredream.baseapplication.entity;

import com.blankj.utilcode.util.StringUtils;

import java.io.Serializable;

public class ImageInfo implements Serializable {

    private String path; // 照相机拍摄的 本地图片地址
    private String url; // 网络地址

    /**
     * 获取图片显示内容，有限显示本地
     */
    public String getImageShowModel() {
        if (!StringUtils.isEmpty(path)) return path;
        return url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return getImageShowModel();
    }
}
