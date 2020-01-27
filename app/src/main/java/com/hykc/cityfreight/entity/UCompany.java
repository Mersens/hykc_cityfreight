package com.hykc.cityfreight.entity;

import java.io.Serializable;

public class UCompany extends BaseObject implements Serializable {
    private long id;//id
    private long pid;//父级id 注册用户添加id
    private String corporateName;//公司名称
    private String creditId;//社会信用代码
    private String companyNature;//公司性质
    private String qualificationsURL;//营业执照图片路径
    private String alctKey;
    private String alctSecret;
    private String alctCode;
    private String etcCode;
    private int status;//0：在用 1：停用
    private double tax;//公司比例
    private int isBond;//是否缴纳运费 0：不 1代缴运费
    private double fee;//公司金额
    private int isPayment;//是否代付款
    private double obtainPrice;//收益金额
    private String legalName; //法人姓名
    private String legalPhone; //法人电话
    private String companyAddress;//公司地址
    private String openAccount;//开户行名称
    private String bankAccount;//银行账户
    private String legalId; // 法人身份证号
    private String jindieCode;//金蝶Code
    private String startShow;
    private String endShow;
    private String createTime;//创建时间
    //起始时间
    private String startTime;
    //结束时间
    private String endTime;
    //审核 0未审核 1已审核 2审核失败
    private int examine;

    private String sfzzURL;
    private String sfzfURL;

    //负责人姓名
    private String fuZeName;
    //负责人电话
    private String fuZePhone;
    //负责人身份证号
    private String fuZeId;
    private int billManage;
    //负者人生份证正面
    private String fuZeZM;
    //负责人身份证反面
    private String fuZeFM;

    public String getFuZeFM() {
        return fuZeFM;
    }

    public void setFuZeFM(String fuZeFM) {
        this.fuZeFM = fuZeFM;
    }

    public int getBillManage() {
        return billManage;
    }

    public void setBillManage(int billManage) {
        this.billManage = billManage;
    }

    public String getFuZeName() {
        return fuZeName;
    }

    public void setFuZeName(String fuZeName) {
        this.fuZeName = fuZeName;
    }

    public String getFuZePhone() {
        return fuZePhone;
    }

    public void setFuZePhone(String fuZePhone) {
        this.fuZePhone = fuZePhone;
    }

    public String getFuZeId() {
        return fuZeId;
    }

    public void setFuZeId(String fuZeId) {
        this.fuZeId = fuZeId;
    }

    public String getStartShow() {
        return startShow;
    }

    public String getOpenAccount() {
        return openAccount;
    }

    public void setOpenAccount(String openAccount) {
        this.openAccount = openAccount;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public void setStartShow(String startShow) {
        this.startShow = startShow;
    }

    public String getEndShow() {
        return endShow;
    }

    public void setEndShow(String endShow) {
        this.endShow = endShow;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getExamine() {
        return examine;
    }

    public void setExamine(int examine) {
        this.examine = examine;
    }

    public String getJindieCode() {
        return jindieCode;
    }

    public void setJindieCode(String jindieCode) {
        this.jindieCode = jindieCode;
    }

    public String getLegalName(){return legalName;}
    public void setLegalName(String s){this.legalName = s;}

    public String getLegalPhone(){return legalPhone;}
    public void setLegalPhone(String s){this.legalPhone = s;}

    public String getLegalId(){return legalId;}
    public void setLegalId(String s){this.legalId = s;}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getCorporateName() {
        return corporateName;
    }

    public void setCorporateName(String corporateName) {
        this.corporateName = corporateName;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }

    public String getCompanyNature() {
        return companyNature;
    }

    public void setCompanyNature(String companyNature) {
        this.companyNature = companyNature;
    }

    public String getQualificationsURL() {
        return qualificationsURL;
    }

    public void setQualificationsURL(String qualificationsURL) {
        this.qualificationsURL = qualificationsURL;
    }

    public String getAlctKey() {
        return alctKey;
    }

    public void setAlctKey(String alctKey) {
        this.alctKey = alctKey;
    }

    public String getAlctSecret() {
        return alctSecret;
    }

    public void setAlctSecret(String alctSecret) {
        this.alctSecret = alctSecret;
    }

    public String getAlctCode() {
        return alctCode;
    }

    public void setAlctCode(String alctCode) {
        this.alctCode = alctCode;
    }

    public String getEtcCode() {
        return etcCode;
    }

    public void setEtcCode(String etcCode) {
        this.etcCode = etcCode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public int getIsBond() {
        return isBond;
    }

    public void setIsBond(int isBond) {
        this.isBond = isBond;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getIsPayment() {
        return isPayment;
    }

    public void setIsPayment(int isPayment) {
        this.isPayment = isPayment;
    }

    public double getObtainPrice() {
        return obtainPrice;
    }

    public void setObtainPrice(double obtainPrice) {
        this.obtainPrice = obtainPrice;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getSfzzURL() {
        return sfzzURL;
    }

    public void setSfzzURL(String sfzzURL) {
        this.sfzzURL = sfzzURL;
    }

    public String getSfzfURL() {
        return sfzfURL;
    }

    public void setSfzfURL(String sfzfURL) {
        this.sfzfURL = sfzfURL;
    }

    public String getFuZeZM() {
        return fuZeZM;
    }

    public void setFuZeZM(String fuZeZM) {
        this.fuZeZM = fuZeZM;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"pid\":")
                .append(pid);
        sb.append(",\"corporateName\":\"")
                .append(corporateName).append('\"');
        sb.append(",\"creditId\":\"")
                .append(creditId).append('\"');
        sb.append(",\"companyNature\":\"")
                .append(companyNature).append('\"');
        sb.append(",\"qualificationsURL\":\"")
                .append(qualificationsURL).append('\"');
        sb.append(",\"alctKey\":\"")
                .append(alctKey).append('\"');
        sb.append(",\"alctSecret\":\"")
                .append(alctSecret).append('\"');
        sb.append(",\"alctCode\":\"")
                .append(alctCode).append('\"');
        sb.append(",\"etcCode\":\"")
                .append(etcCode).append('\"');
        sb.append(",\"status\":")
                .append(status);
        sb.append(",\"tax\":")
                .append(tax);
        sb.append(",\"isBond\":")
                .append(isBond);
        sb.append(",\"fee\":")
                .append(fee);
        sb.append(",\"isPayment\":")
                .append(isPayment);
        sb.append(",\"obtainPrice\":")
                .append(obtainPrice);
        sb.append(",\"legalName\":\"")
                .append(legalName).append('\"');
        sb.append(",\"legalPhone\":\"")
                .append(legalPhone).append('\"');
        sb.append(",\"companyAddress\":\"")
                .append(companyAddress).append('\"');
        sb.append(",\"openAccount\":\"")
                .append(openAccount).append('\"');
        sb.append(",\"bankAccount\":\"")
                .append(bankAccount).append('\"');
        sb.append(",\"legalId\":\"")
                .append(legalId).append('\"');
        sb.append(",\"jindieCode\":\"")
                .append(jindieCode).append('\"');
        sb.append(",\"startShow\":\"")
                .append(startShow).append('\"');
        sb.append(",\"endShow\":\"")
                .append(endShow).append('\"');
        sb.append(",\"createTime\":\"")
                .append(createTime).append('\"');
        sb.append(",\"startTime\":\"")
                .append(startTime).append('\"');
        sb.append(",\"endTime\":\"")
                .append(endTime).append('\"');
        sb.append(",\"examine\":")
                .append(examine);
        sb.append(",\"sfzzURL\":\"")
                .append(sfzzURL).append('\"');
        sb.append(",\"sfzfURL\":\"")
                .append(sfzfURL).append('\"');
        sb.append(",\"fuZeName\":\"")
                .append(fuZeName).append('\"');
        sb.append(",\"fuZePhone\":\"")
                .append(fuZePhone).append('\"');
        sb.append(",\"fuZeId\":\"")
                .append(fuZeId).append('\"');
        sb.append(",\"billManage\":")
                .append(billManage);
        sb.append(",\"fuZeZM\":\"")
                .append(fuZeZM).append('\"');
        sb.append(",\"fuZeFM\":\"")
                .append(fuZeFM).append('\"');
        sb.append('}');
        return sb.toString();
    }

}
