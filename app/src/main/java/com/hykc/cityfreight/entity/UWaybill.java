package com.hykc.cityfreight.entity;

import java.io.Serializable;

public class UWaybill extends BaseObject implements Serializable {
    /**
     * 运单信息
     */
    private long id;//订单id 自增
    private String waybillId;//moxid//订单生成id
    private long companyId;//发布公司id
    private String companyName;//所在公司名称
    private long publisherId;//发布订单人员id
    private String contractNum;//合同号
    private String customerName;//客户名
    private String goodsName;//货物名称
    private String fromProvince;//出发省
    private String fromCity;//出发市
    private String fromArea;//出发区
    private int fromAreaId;//出发地区域编码
    private String shipperName;//发货人姓名
    private String shipperPhone;//发货人电话
    private String fromLocation;//发货地址
    private String fromlat;//发货地纬度
    private String fromlong;//发货地经度
    private String toProvince;//送达省
    private String toCity;//送达市
    private String toArea;//送达区
    private int toAreaId;//目的地区域编码
    private String toLocation;//到货地址
    private String tolat;//到货地纬度
    private String tolong;//到货地经度
    private String consigneeName;//收货人
    private String consigneePhone;//收货人电话
    private int goodId;//货物编码id
    private int goodCode;//货物代码
    private String goodName;//货物代码名称
    private String taskUnit;//任务单位
    private String goodsDescribe;//货物描述
    private String remarks;//备注
    private String createTime;//创建时间
    private double distance;//距离
    private String carLength;//车长

    private String carLoad;//载重
    //----订单自带信息
    private int orderType;//1任务 0个人
    private long taskId;//模版id
    private int isOil;//是否使用有卡 0未使用 1使用比例 2自定义输入油卡数
    private double oilRatio;//油卡比列 如果isOil 如果是油卡比例则显示比例数   不是显示0
    private double oilPrice;//油卡金额
    private double orderPrice;//订单金额
    private double etcPrice;//etc金额
    private String estimatedTime;//预计送达时间
    private String receiveTime;//接单时间
    private String pickupTime;//提货时间
    private String unloadTime;//卸货时间
    private String signTime;//签收时间
    private double tax;//运单手续费比列
    private String clearingTime;//结款时间
    private String payTime;//支付时间 保证金
    private double driverPrice;//司机获得
    private int status;//0 创建运单 1取消 2拒绝 3已接单 4开始配送 5已送达 6已签收 7已结算 8 已完成
    private int revocationState;//0 未撤销 1司机撤销 2货主撤销
    private int orderTimes;//承运次数
    //-------司机信息
    private long driverId;//司机id
    private String driverMobile;
    private String driverName;//司机姓名
    private String driverCardId;//司机身份证
    private long carId;//车辆id
    private String carNumber;//车牌号
    private String creditId;//社会信用代码
    private int isDriverShowPrice;//是否展示运费
    //--货物信息
    private double ucreditId;// 订单运输单位数 xx吨 xx立方米
    private double taskPrice;//订单单价
    private String syncAlctMsg;//同步安联返回结果
    private String pickupMsg;//提货返回信息
    private String unloadMsg;//卸货安联返回信息
    private String pickupURL;//提货照片地址
    private String unloadURL;//卸货照片地址
    private String receiptURL;//回单照地址
    private String podURL;
    private int isPickup;//是否上传提货照 0未上传 1已上传
    private int isUnload;//是否上传卸货照
    private String alctCode;
    private String  alctId ;
    private String alctKey;
    private String alctUnloadMsg;//卸货照上传信息

    private int userType=1;//用户类型 1表示司机本人(默认) 2表示货主人,3车队长
    private String bank_user_name;//银行卡持卡人姓名
    private String bank_user_account;//银行卡账号
    private int isSelf;//是否是本人
    public int getIsSelf() {
        return isSelf;
    }

    public void setIsSelf(int isSelf) {
        this.isSelf = isSelf;
    }

    public String getCarLength() {
        return carLength;
    }

    public void setCarLength(String carLength) {
        this.carLength = carLength;
    }

    public String getCarLoad() {
        return carLoad;
    }

    public void setCarLoad(String carLoad) {
        this.carLoad = carLoad;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
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

    public double getAdminPrice() {
        return adminPrice;
    }

    public void setAdminPrice(double adminPrice) {
        this.adminPrice = adminPrice;
    }

    private double adminPrice;
    public void setUcreditId(double ucreditId) {
        this.ucreditId = ucreditId;
    }

    public String getHwjz() {
        return hwjz;
    }

    public void setHwjz(String hwjz) {
        this.hwjz = hwjz;
    }

    private String hwjz;
    public String getAlctUnloadMsg() {
        return alctUnloadMsg;
    }

    public void setAlctUnloadMsg(String alctUnloadMsg) {
        this.alctUnloadMsg = alctUnloadMsg;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


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

    public String getPodURL() {
        return podURL;
    }

    public void setPodURL(String podURL) {
        this.podURL = podURL;
    }

    public int getIsPickup() {
        return isPickup;
    }

    public void setIsPickup(int isPickup) {
        this.isPickup = isPickup;
    }

    public int getIsUnload() {
        return isUnload;
    }

    public void setIsUnload(int isUnload) {
        this.isUnload = isUnload;
    }

    public int getIsPod() {
        return isPod;
    }

    public void setIsPod(int isPod) {
        this.isPod = isPod;
    }

    private int isPod;//是否上传回单照


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getWaybillId() {
        return waybillId;
    }

    public void setWaybillId(String waybillId) {
        this.waybillId = waybillId;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(long publisherId) {
        this.publisherId = publisherId;
    }

    public String getContractNum() {
        return contractNum;
    }

    public void setContractNum(String contractNum) {
        this.contractNum = contractNum;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getFromProvince() {
        return fromProvince;
    }

    public void setFromProvince(String fromProvince) {
        this.fromProvince = fromProvince;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getFromArea() {
        return fromArea;
    }

    public void setFromArea(String fromArea) {
        this.fromArea = fromArea;
    }

    public int getFromAreaId() {
        return fromAreaId;
    }

    public void setFromAreaId(int fromAreaId) {
        this.fromAreaId = fromAreaId;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getShipperPhone() {
        return shipperPhone;
    }

    public void setShipperPhone(String shipperPhone) {
        this.shipperPhone = shipperPhone;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getFromlat() {
        return fromlat;
    }

    public void setFromlat(String fromlat) {
        this.fromlat = fromlat;
    }

    public String getFromlong() {
        return fromlong;
    }

    public void setFromlong(String fromlong) {
        this.fromlong = fromlong;
    }

    public String getToProvince() {
        return toProvince;
    }

    public void setToProvince(String toProvince) {
        this.toProvince = toProvince;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public String getToArea() {
        return toArea;
    }

    public void setToArea(String toArea) {
        this.toArea = toArea;
    }

    public int getToAreaId() {
        return toAreaId;
    }

    public void setToAreaId(int toAreaId) {
        this.toAreaId = toAreaId;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getTolat() {
        return tolat;
    }

    public void setTolat(String tolat) {
        this.tolat = tolat;
    }

    public String getTolong() {
        return tolong;
    }

    public void setTolong(String tolong) {
        this.tolong = tolong;
    }

    public String getConsigneeName() {
        return consigneeName;
    }

    public void setConsigneeName(String consigneeName) {
        this.consigneeName = consigneeName;
    }

    public String getConsigneePhone() {
        return consigneePhone;
    }

    public void setConsigneePhone(String consigneePhone) {
        this.consigneePhone = consigneePhone;
    }

    public int getGoodId() {
        return goodId;
    }

    public void setGoodId(int goodId) {
        this.goodId = goodId;
    }

    public int getGoodCode() {
        return goodCode;
    }

    public void setGoodCode(int goodCode) {
        this.goodCode = goodCode;
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public String getTaskUnit() {
        return taskUnit;
    }

    public void setTaskUnit(String taskUnit) {
        this.taskUnit = taskUnit;
    }

    public String getGoodsDescribe() {
        return goodsDescribe;
    }

    public void setGoodsDescribe(String goodsDescribe) {
        this.goodsDescribe = goodsDescribe;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public int getIsOil() {
        return isOil;
    }

    public void setIsOil(int isOil) {
        this.isOil = isOil;
    }

    public double getOilRatio() {
        return oilRatio;
    }

    public void setOilRatio(double oilRatio) {
        this.oilRatio = oilRatio;
    }

    public double getOilPrice() {
        return oilPrice;
    }

    public void setOilPrice(double oilPrice) {
        this.oilPrice = oilPrice;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(String receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getUnloadTime() {
        return unloadTime;
    }

    public void setUnloadTime(String unloadTime) {
        this.unloadTime = unloadTime;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public String getClearingTime() {
        return clearingTime;
    }

    public void setClearingTime(String clearingTime) {
        this.clearingTime = clearingTime;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRevocationState() {
        return revocationState;
    }

    public void setRevocationState(int revocationState) {
        this.revocationState = revocationState;
    }

    public long getDriverId() {
        return driverId;
    }

    public void setDriverId(long driverId) {
        this.driverId = driverId;
    }

    public long getCarId() {
        return carId;
    }

    public void setCarId(long carId) {
        this.carId = carId;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public double getDriverPrice() {
        return driverPrice;
    }

    public void setDriverPrice(double driverPrice) {
        this.driverPrice = driverPrice;
    }

    public double getEtcPrice() {
        return etcPrice;
    }

    public void setEtcPrice(double etcPrice) {
        this.etcPrice = etcPrice;
    }

    public int getOrderTimes() {
        return orderTimes;
    }

    public void setOrderTimes(int orderTimes) {
        this.orderTimes = orderTimes;
    }

    public int getIsDriverShowPrice() {
        return isDriverShowPrice;
    }

    public void setIsDriverShowPrice(int isDriverShowPrice) {
        this.isDriverShowPrice = isDriverShowPrice;
    }

    public String getDriverMobile() {
        return driverMobile;
    }

    public void setDriverMobile(String driverMobile) {
        this.driverMobile = driverMobile;
    }

    public double getUcreditId() {
        return ucreditId;
    }

    public void setUcreditId(int ucreditId) {
        this.ucreditId = ucreditId;
    }

    public double getTaskPrice() {
        return taskPrice;
    }

    public void setTaskPrice(double taskPrice) {
        this.taskPrice = taskPrice;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getSyncAlctMsg() {
        return syncAlctMsg;
    }

    public void setSyncAlctMsg(String syncAlctMsg) {
        this.syncAlctMsg = syncAlctMsg;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getDriverCardId() {
        return driverCardId;
    }

    public void setDriverCardId(String driverCardId) {
        this.driverCardId = driverCardId;
    }

    public String getPickupMsg() {
        return pickupMsg;
    }

    public void setPickupMsg(String pickupMsg) {
        this.pickupMsg = pickupMsg;
    }

    public String getUnloadMsg() {
        return unloadMsg;
    }

    public void setUnloadMsg(String unloadMsg) {
        this.unloadMsg = unloadMsg;
    }

    public String getPickupURL() {
        return pickupURL;
    }

    public void setPickupURL(String pickupURL) {
        this.pickupURL = pickupURL;
    }

    public String getUnloadURL() {
        return unloadURL;
    }

    public void setUnloadURL(String unloadURL) {
        this.unloadURL = unloadURL;
    }

    public String getReceiptURL() {
        return receiptURL;
    }

    public void setReceiptURL(String receiptURL) {
        this.receiptURL = receiptURL;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getCreditId() {
        return creditId;
    }

    public void setCreditId(String creditId) {
        this.creditId = creditId;
    }
}
