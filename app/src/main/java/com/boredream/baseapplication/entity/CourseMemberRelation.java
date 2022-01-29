package com.boredream.baseapplication.entity;

import com.boredream.baseapplication.base.BaseEntity;

/**
 * 课程参加人员 - 关系表
 */
public class CourseMemberRelation extends BaseEntity {

    private Course course;
    private User user;

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
