package com.hykc.cityfreight.entity;

import java.io.Serializable;

public class UCardEntity implements Serializable {
    private int id;
    private String driverId;//司机id
    private int cardType;//卡片类型 1支付宝,2微信,3银行卡
    private String name;//卡片名称 如：支付宝，微信，银行卡
    private String account;//账号
    //银行卡需要字段
    private String address;//银行卡开户银行
    private String bank;//银行名称
    private String creatTime;//创建时间
    private String updateTime;//修改时间
    private String useridentity_z_img;//身份证正面
    private String useridentity_f_img;//身份证反面

    public String getUseridentity_z_img() {
        return useridentity_z_img;
    }

    public void setUseridentity_z_img(String useridentity_z_img) {
        this.useridentity_z_img = useridentity_z_img;
    }

    public String getUseridentity_f_img() {
        return useridentity_f_img;
    }

    public void setUseridentity_f_img(String useridentity_f_img) {
        this.useridentity_f_img = useridentity_f_img;
    }



    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }



}
