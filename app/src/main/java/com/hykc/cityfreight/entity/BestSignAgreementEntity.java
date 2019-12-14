package com.hykc.cityfreight.entity;

public class BestSignAgreementEntity extends BaseObject {

    private String id;
    private String account;//用户账号
    private String fmd5;//文件MD5值
    private String ftype;//文件类型
    private String fname;//文件名字
    private String fpages;//文件总页数
    private String fdata;//文件内容
    private String isCleanup;//是否强制清理pdf,枚举值： 0-不强制清理 1-强制清理
    private String agreType;//合同类型
    private String contractId;//合同id
    private String title;//合同标题
    private String description;//合同描述
    private String expireTime;//有效期
    private String rowid;//运单id
    private String creatTime;//合同生成时间
    private String signTime;//合同签署时间
    private int agreStatu;//合同状态。0未签署 1已签署
    private String signMsg;//签署返回信息
    //仅限银行卡信息比较
    private int isSelf;//是否是本人 0本人 1不是本人
    private String bank_user_name;//银行卡持卡人姓名
    private String bank_user_account;//银行卡账号

    public int getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(int isSelf) {
        this.isSelf = isSelf;
    }

    public String getBank_user_name() {
        return bank_user_name;
    }

    public void setBank_user_name(String bank_user_name) {
        this.bank_user_name = bank_user_name;
    }

    public String getBank_user_account() {
        return bank_user_account;
    }

    public void setBank_user_account(String bank_user_account) {
        this.bank_user_account = bank_user_account;
    }


    public String getSignMsg() {
        return signMsg;
    }

    public void setSignMsg(String signMsg) {
        this.signMsg = signMsg;
    }

    public String getRowid() {
        return rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
    }

    public String getCreatTime() {
        return creatTime;
    }

    public void setCreatTime(String creatTime) {
        this.creatTime = creatTime;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public int getAgreStatu() {
        return agreStatu;
    }

    public void setAgreStatu(int agreStatu) {
        this.agreStatu = agreStatu;
    }



    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }





    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getAgreType() {
        return agreType;
    }

    public void setAgreType(String agreType) {
        this.agreType = agreType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFmd5() {
        return fmd5;
    }

    public void setFmd5(String fmd5) {
        this.fmd5 = fmd5;
    }

    public String getFtype() {
        return ftype;
    }

    public void setFtype(String ftype) {
        this.ftype = ftype;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getFpages() {
        return fpages;
    }

    public void setFpages(String fpages) {
        this.fpages = fpages;
    }

    public String getFdata() {
        return fdata;
    }

    public void setFdata(String fdata) {
        this.fdata = fdata;
    }

    public String getIsCleanup() {
        return isCleanup;
    }

    public void setIsCleanup(String isCleanup) {
        this.isCleanup = isCleanup;
    }





}
