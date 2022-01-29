package com.boredream.baseapplication.entity;

import com.boredream.baseapplication.base.BaseEntity;

/**
 * 教材
 */
public class Book extends BaseEntity {

    public static final String DE = "德式";
    public static final String MEI = "美式";
    public static final String MAO = "毛线";
    public static final String PAO = "跑团";

    private String name;
    private String type;
    private double score;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
