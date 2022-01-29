package com.boredream.baseapplication.entity;

import com.boredream.baseapplication.base.BaseEntity;

/**
 * 课程
 */
public class Course extends BaseEntity {

    private UserProfile assistant;
    private Classroom classroom;
    private String date;
    private long startTime;

    /**
     * 0-萌新 1-进阶
     */
    private int grade;

    public UserProfile getAssistant() {
        return assistant;
    }

    public void setAssistant(UserProfile assistant) {
        this.assistant = assistant;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
