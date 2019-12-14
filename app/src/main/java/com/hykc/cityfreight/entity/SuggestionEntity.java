package com.hykc.cityfreight.entity;

import java.io.Serializable;

public class SuggestionEntity extends BaseObject implements Serializable {
    private int id;
    private String mobile;//联系电话
    private String questionType;//问题类型
    private String questionContent;//问题内容
    private String imgUrl;//照片地址
    private String creatTime;//提交时间
    private int source;//来源 0 全部 1干线，2城配，3服务端
    private int statu;//状态 0全部，1已处理，2未处理
    private String dealTime;//处理时间
    private String dealUser;//处理人
    private String dealMsg;//处理内容
    
    public String getDealMsg() {
        return dealMsg;
    }

    public void setDealMsg(String dealMsg) {
        this.dealMsg = dealMsg;
    }




    public String getDealUser() {
        return dealUser;
    }

    public void setDealUser(String dealUser) {
        this.dealUser = dealUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getStatu() {
        return statu;
    }

    public void setStatu(int statu) {
        this.statu = statu;
    }

    public String getDealTime() {
        return dealTime;
    }

    public void setDealTime(String dealTime) {
        this.dealTime = dealTime;
    }


}
