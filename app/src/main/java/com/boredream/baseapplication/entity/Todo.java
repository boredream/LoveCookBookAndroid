package com.boredream.baseapplication.entity;

/**
 * <p>
 * 清单
 * </p>
 *
 * @author boredream
 */
public class Todo extends Belong2UserEntity {


	// 所属清单组名称
    private String todoGroupName;

	// 所属清单组id
    private Long todoGroupId;

	// 已完成
    private boolean done;

	// 名称
    private String name;

	// 完成日期
    private String doneDate;

	// 描述
    private String detail;

	// 图片
    private String images;

    public String getTodoGroupName() {
        return todoGroupName;
    }

    public void setTodoGroupName(String todoGroupName) {
        this.todoGroupName = todoGroupName;
    }

    public Long getTodoGroupId() {
        return todoGroupId;
    }

    public void setTodoGroupId(Long todoGroupId) {
        this.todoGroupId = todoGroupId;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDoneDate() {
        return doneDate;
    }

    public void setDoneDate(String doneDate) {
        this.doneDate = doneDate;
    }

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
