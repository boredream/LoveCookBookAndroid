package com.boredream.baseapplication.entity;

/**
 * <p>
 * 日记
 * </p>
 *
 * @author boredream
 */
public class Diary extends Belong2UserEntity {


	// 文字内容
    private String content;

	// 日记日期
    private String diaryDate;

	// 图片
    private String images;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDiaryDate() {
        return diaryDate;
    }

    public void setDiaryDate(String diaryDate) {
        this.diaryDate = diaryDate;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
