package com.boredream.baseapplication.entity;

/**
 * <p>
 * 已经反馈
 * </p>
 *
 * @author boredream
 */
public class FeedBack extends Belong2UserEntity {

	// 描述
    private String detail;

	// 图片
    private String images;

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
