package com.hykc.cityfreight.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/23.
 */

public class OverlayInfo implements Serializable {
    private double latitude;//维度
    private double longitude;//经度
    private String name;
    private String distance;
    private String psjs;
    private String xhjg;
    private String carType;
    private String payType;
    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }


    public String getPsjs() {
        return psjs;
    }

    public void setPsjs(String psjs) {
        this.psjs = psjs;
    }

    public String getXhjg() {
        return xhjg;
    }

    public void setXhjg(String xhjg) {
        this.xhjg = xhjg;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }



}
