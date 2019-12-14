package com.hykc.cityfreight.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/27.
 */

public class GoodsEntity implements Serializable {
    public static final int wjd = 0;//未接单
    public static final int dzh = 1;//待装货
    public static final int ysz = 2;//运输中
    public static final int ywc = 3;//已完成
    public static final int yqx = 4;//已取消
    private String startAddress;//起点
    private String endAddress;//终点
    private String price;//价格
    private String time;//时间
    private String carType;//车辆类型
    private String goodsName;//货物名称
    private int state;//状态
    private String rowid;
    private String endTime;
    private String startTime;

    private String fhrName;//发货人
    private String fhrPhone;//发货人电话
    private String shrName;//收货人
    private String shrPhone;//收货人电话
    private boolean isGuiding = false;
    private String payType;
    private String pickDown;
    private String xhjg;
    private String lon_to;//经度
    private String lat_to;//维度
    private String lon_from;//经度
    private String lat_from;//维度
    private String shd;//收货地
    private String fhd;//发货地
    private String jdTime;
    private String psTime;
    private String wcTime;
    private Driverpj driverpj;
    private String alctCode;
    private String  alctId ;

    public String getAlctCode() {
        return alctCode;
    }

    public void setAlctCode(String alctCode) {
        this.alctCode = alctCode;
    }

    public String getAlctId() {
        return alctId;
    }

    public void setAlctId(String alctId) {
        this.alctId = alctId;
    }

    public String getAlctKey() {
        return alctKey;
    }

    public void setAlctKey(String alctKey) {
        this.alctKey = alctKey;
    }

    private String alctKey;
    public Driverpj getDriverpj() {
        return driverpj;
    }

    public void setDriverpj(Driverpj driverpj) {
        this.driverpj = driverpj;
    }



    public String getJdTime() {
        return jdTime;
    }

    public void setJdTime(String jdTime) {
        this.jdTime = jdTime;
    }

    public String getPsTime() {
        return psTime;
    }

    public void setPsTime(String psTime) {
        this.psTime = psTime;
    }

    public String getWcTime() {
        return wcTime;
    }

    public void setWcTime(String wcTime) {
        this.wcTime = wcTime;
    }

    public String getQxTime() {
        return qxTime;
    }

    public void setQxTime(String qxTime) {
        this.qxTime = qxTime;
    }

    private String qxTime;


    public String getLon_to() {
        return lon_to;
    }

    public void setLon_to(String lon_to) {
        this.lon_to = lon_to;
    }

    public String getLat_to() {
        return lat_to;
    }

    public void setLat_to(String lat_to) {
        this.lat_to = lat_to;
    }

    public String getLon_from() {
        return lon_from;
    }

    public void setLon_from(String lon_from) {
        this.lon_from = lon_from;
    }

    public String getLat_from() {
        return lat_from;
    }

    public void setLat_from(String lat_from) {
        this.lat_from = lat_from;
    }

    public String getShd() {
        return shd;
    }

    public void setShd(String shd) {
        this.shd = shd;
    }

    public String getFhd() {
        return fhd;
    }

    public void setFhd(String fhd) {
        this.fhd = fhd;
    }


    public String getShrName() {
        return shrName;
    }

    public void setShrName(String shrName) {
        this.shrName = shrName;
    }

    public String getShrPhone() {
        return shrPhone;
    }

    public void setShrPhone(String shrPhone) {
        this.shrPhone = shrPhone;
    }


    public boolean isGuiding() {
        return isGuiding;
    }

    public void setGuiding(boolean guiding) {
        isGuiding = guiding;
    }


    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }


    public String getPickDown() {
        return pickDown;
    }

    public void setPickDown(String pickDown) {
        this.pickDown = pickDown;
    }


    public String getXhjg() {
        return xhjg;
    }

    public void setXhjg(String xhjg) {
        this.xhjg = xhjg;
    }


    public String getRowid() {
        return rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }


    public String getFhrName() {
        return fhrName;
    }

    public void setFhrName(String fhrName) {
        this.fhrName = fhrName;
    }

    public String getFhrPhone() {
        return fhrPhone;
    }

    public void setFhrPhone(String fhrPhone) {
        this.fhrPhone = fhrPhone;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCarType() {
        return carType;
    }

    public void setCarType(String carType) {
        this.carType = carType;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

   public static class Driverpj {
        private String val;
        private String createTime;
        private String msg;
        private String rowid;

        public String getVal() {
            return val;
        }

        public void setVal(String val) {
            this.val = val;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getRowid() {
            return rowid;
        }

        public void setRowid(String rowid) {
            this.rowid = rowid;
        }


    }

}
