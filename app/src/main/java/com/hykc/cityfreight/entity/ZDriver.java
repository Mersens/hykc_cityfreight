package com.hykc.cityfreight.entity;

import java.io.Serializable;

/**
 * Created by niuniu on 2019/8/16.
 */
public class ZDriver implements Serializable {
    private int id;
    private int state;
    private String createTime;
    private int type;
    private Double price;
    private Double oldprice;
    private Double newprice;
    private String remarks;
    private String target;
    private int targetId;
    private String waybillId;
    private int driverId;
    private Double surplusPrice;
    private String jindieCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getOldprice() {
        return oldprice;
    }

    public void setOldprice(Double oldprice) {
        this.oldprice = oldprice;
    }

    public Double getNewprice() {
        return newprice;
    }

    public void setNewprice(Double newprice) {
        this.newprice = newprice;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public int getTargetId() {
        return targetId;
    }

    public void setTargetId(int targetId) {
        this.targetId = targetId;
    }

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public Double getSurplusPrice() {
        return surplusPrice;
    }

    public void setSurplusPrice(Double surplusPrice) {
        this.surplusPrice = surplusPrice;
    }

    public String getJindieCode() {
        return jindieCode;
    }

    public void setJindieCode(String jindieCode) {
        this.jindieCode = jindieCode;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"state\":")
                .append(state);
        sb.append(",\"createTime\":\"")
                .append(createTime).append('\"');
        sb.append(",\"type\":")
                .append(type);
        sb.append(",\"price\":")
                .append(price);
        sb.append(",\"oldprice\":")
                .append(oldprice);
        sb.append(",\"newprice\":")
                .append(newprice);
        sb.append(",\"remarks\":\"")
                .append(remarks).append('\"');
        sb.append(",\"target\":\"")
                .append(target).append('\"');
        sb.append(",\"targetId\":")
                .append(targetId);
        sb.append(",\"waybillId\":\"")
                .append(waybillId).append('\"');
        sb.append(",\"driverId\":")
                .append(driverId);
        sb.append(",\"surplusPrice\":")
                .append(surplusPrice);
        sb.append(",\"jindieCode\":\"")
                .append(jindieCode).append('\"');
        sb.append('}');
        return sb.toString();
    }
}
