package com.boredream.baseapplication.entity;

import com.boredream.baseapplication.base.BaseEntity;

/**
 * 教室
 */
public class Classroom extends BaseEntity {

    private String name;
    private UserProfile teacher;
    private String province;
    private String city;
    private String district;
    private String address;
    private double longitude;
    private double latitude;
    private int seatCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserProfile getTeacher() {
        return teacher;
    }

    public void setTeacher(UserProfile teacher) {
        this.teacher = teacher;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }
}
