package com.hykc.cityfreight.service;

import com.hykc.cityfreight.app.Constants;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Mersens on 2016/9/28.
 */

public interface ServiceStore {
    /*新版接口*/
    @FormUrlEncoded
    @POST("app/getSms")
    Observable<ResponseBody> getSms(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/register")
    Observable<ResponseBody> register(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/login")
    Observable<ResponseBody> login(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/updateLoginPsd")
    Observable<ResponseBody> updateLoginPsd(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/updatePayPsd")
    Observable<ResponseBody> updatePayPsd(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/resetPayPsd")
    Observable<ResponseBody> resetPayPsd(@FieldMap Map<String, String> params);

    @POST("app/checkVersion")
    Observable<ResponseBody> checkVerson();

    @FormUrlEncoded
    @POST("app/getDirverInfo")
    Observable<ResponseBody> getDirverInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/submitAuthenticationInfo")
    Observable<ResponseBody> submitAuthenticationInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/checkDriverAuthentication")
    Observable<ResponseBody> checkDriverAuthentication(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/addCarInfo")
    Observable<ResponseBody> addCarInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/selectCarInfoByDriverId")
    Observable<ResponseBody> selectCarInfoByDriverId(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/selectAllCarInfo")
    Observable<ResponseBody> selectAllCarInfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("app/delCarInfoById")
    Observable<ResponseBody> delCarInfoById(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/updateCarInfo")
    Observable<ResponseBody> updateCarInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/getWaybillList")
    Observable<ResponseBody> getWaybillList(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/updateUWaybillStatuById")
    Observable<ResponseBody> updateUWaybillStatuById(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/upLoadPickUpImg")
    Observable<ResponseBody> upLoadPickUpImg(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/upLoadReImg")
    Observable<ResponseBody> upLoadReImg(@FieldMap(encoded = true) Map<String, String> params);

    @FormUrlEncoded
    @POST("app/updateRZImg")
    Observable<ResponseBody> updateRZImg(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/upLoadOrderImg")
    Observable<ResponseBody> upLoadOrderImg(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/selectUwaybillStrive")
    Observable<ResponseBody> selectUwaybillStrive(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/driverStrive")
    Observable<ResponseBody> driverStrive(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/checkTokenTimeout")
    Observable<ResponseBody> checkTokenTimeout(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/upLoadUWaybillLocation")
    Observable<ResponseBody> upLoadUWaybillLocation(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/submitOrderEctInfo")
    Observable<ResponseBody> submitOrderEctInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/updateAlctStatuById")
    Observable<ResponseBody> updateOrderAlctMsg(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/statisticsAppInLineCount")
    Observable<ResponseBody> statisticsAppInLineCount(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/selectUWaybillById")
    Observable<ResponseBody> selectUWaybillById(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/getDriverMoneyInfo")
    Observable<ResponseBody> getDriverMoneyInfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("app/getDriverOrderInfo")
    Observable<ResponseBody> getDriverOrderInfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("app/selectCardInfo")
    Observable<ResponseBody> selectCardInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/addCardInfo")
    Observable<ResponseBody> addCardInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/delCardInfoBtId")
    Observable<ResponseBody> delCardInfoById(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/uploadBankUserImg")
    Observable<ResponseBody> uploadBankUserImg(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("app/applyCashWithdrawal")
    Observable<ResponseBody> applyCashWithdrawal(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/selectCashWithdrawal")
    Observable<ResponseBody> selectCashWithdrawal(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/updateDriverImg")
    Observable<ResponseBody> updateDriverImg(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/selectUWaybillByWaybill")
    Observable<ResponseBody> selectUWaybillByWaybill(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("app/evaluate_huozhu")
    Observable<ResponseBody> evaluate_huozhu(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("etc/start/")
    Call<ResponseBody> submitOrderEctInfoStart(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("etc/end/")
    Call<ResponseBody> submitOrderEctInfoEnd(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/upAlctImgMsg")
    Observable<ResponseBody> upAlctImgMsg(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("bestsign/selectUDriverIsFaceTest/")
    Call<ResponseBody> selectUDriverIsFaceTest(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("bestsign/idcardFaceVerify/")
    Call<ResponseBody> idcardFaceVerify(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("bestsign/addDriverSignInfo/")
    Call<ResponseBody> addDriverSignInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("oil/queryDriverMoney/")
    Call<ResponseBody> queryDriverMoney(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("news/getNewsInfo/")
    Call<ResponseBody> getNewsInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("oil/getPayCode/")
    Call<ResponseBody> getPayCode(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("oil/getStationsByCity/")
    Call<ResponseBody> getStationAndFuels(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("oil/getFuelsByPid/")
    Call<ResponseBody> getFuelsByPid(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("bestsign/autoSignAgre/")
    Call<ResponseBody> autoSignAgre(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("bestsign/checkAgreByRowid/")
    Call<ResponseBody> checkAgreByRowid(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("sugg/addSuggestionInfo/")
    Call<ResponseBody> addSuggestionInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("sugg/uploadSuggImg/")
    Call<ResponseBody> uploadSuggImg(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("bestsign/selectAllArgeByAccount/")
    Call<ResponseBody> selectAllArgeByAccount(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("bestsign/selectAgreInfoByRowid/")
    Call<ResponseBody> selectAgreInfoByRowid(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("sugg/selectSuggByMobile/")
    Call<ResponseBody> selectSuggByMobile(@FieldMap Map<String, String> params);







    /*早期版本接口*/
    @FormUrlEncoded
    @POST("ds/opers.execLogin")
    Observable<ResponseBody> login(@Field("mobile") String mobile, @Field("pwd") String pwd, @Field("app") String app);

    @GET("ds/opers.regMobileUser")
    Observable<ResponseBody> reg(@Query("mobile") String mobile, @Query("newpwd") String newpwd, @Query("sms") String sms, @Query("app") String app, @Query("payPwd") String payPwd);

    @GET("showdata/cityDistribution/updatapwd.jsp")
    Observable<ResponseBody> find(@Query("mobile") String mobile, @Query("newpwd") String newpwd, @Query("sms") String sms, @Query("app") String app);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/changepwd.jsp")
    Observable<ResponseBody> changepwd(@Field("mobile") String mobile, @Field("newpwd") String newpwd, @Field("pwd") String pwd, @Field("token") String token, @Field("app") String app);

    @FormUrlEncoded
    @POST("ds/opers.getSms")
    Observable<ResponseBody> getCode(@Field("mobile") String mobile, @Field("cat") String cat);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/findUser.jsp")
    Observable<ResponseBody> getMoneyInfo(@Field("rowid") String rowid);
    @FormUrlEncoded
    @POST("showdata/cityDistribution/findOrderList.jsp")
    Observable<ResponseBody> getGoodsInfo(@Field("status") String status, @Field("token") String token, @Field("mobile") String mobile, @Field("app") String app);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/findMyOrder.jsp")
    Observable<ResponseBody> getGoodsInfo(@Field("rowid") String rowid, @Field("status") String status, @Field("token") String token, @Field("mobile") String mobile, @Field("app") String app);


    @FormUrlEncoded
    @POST("showdata/cityDistribution/submitOrder.jsp")
    Observable<ResponseBody> orders(@Field("rowid") String rowid, @Field("payer") String driverId, @Field("token") String token, @Field("app") String app, @Field("mobile") String mobile, @Field("type") String type, @Field("payPwd") String pwd);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/updataOrder.jsp")
    Observable<ResponseBody> updataOrder(@Field("rowid") String rowid, @Field("status") String status, @Field("mobile") String mobile, @Field("app") String app, @Field("token") String token, @Field("zl") String zl);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/updataOrder.jsp")
    Observable<ResponseBody> updataOrderDetail(@Field("rowid") String rowid, @Field("mobile") String mobile, @Field("app") String app, @Field("token") String token);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/submitOrder.jsp")
    Observable<ResponseBody> cancelOrder(@Field("rowid") String rowid, @Field("token") String token, @Field("mobile") String mobile, @Field("app") String app, @Field("type") String type, @Field("msg") String msg, @Field("payer") String payer);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/upload.jsp")
    Observable<ResponseBody> uoload(@Field("rowid") String rowid, @Field("token") String token, @Field("mobile") String mobile, @Field("app") String app, @Field("type") String type, @Field("base64") String base64);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/smrz.jsp")
    Observable<ResponseBody> smrz(@Field("identityNo") String identityNo, @Field("mobile") String mobile, @Field("app") String app, @Field("name") String driverName, @Field("cph") String cph, @Field("brand") String brand, @Field("carType") String carType);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/findMyRz.jsp")
    Observable<ResponseBody> findMyRz(@Field("rowid") String rowid, @Field("token") String token, @Field("mobile") String mobile, @Field("app") String app);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/findAroundGEO.jsp")
    Observable<ResponseBody> findAroundGEO(@Field("token") String token, @Field("mobile") String mobile, @Field("app") String app, @Field("lat") String lat, @Field("lon") String lon);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/createPayOrder.jsp")
    Observable<ResponseBody> createPayOrder(@Field("token") String token, @Field("mobile") String mobile, @Field("app") String app, @Field("paytype") String paytype, @Field("amount") String amount, @Field("zfbid") String zfbid);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/dealUserCars.jsp")
    Observable<ResponseBody> getMyCardInfo(@Field("token") String token, @Field("mobile") String mobile, @Field("app") String app, @Field("reqType") String search);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/dealUserCars.jsp")
    Observable<ResponseBody> uploadCardInfo(@Field("token") String token, @Field("mobile") String mobile, @Field("app") String app, @Field("carInfo") String carInfo, @Field("reqType") String search);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/inserCashrequ.jsp")
    Observable<ResponseBody> uploadTXInfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST("showdata/cityDistribution/updataOrderForPj.jsp")
    Observable<ResponseBody> updataOrderForPj(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/createPayOrder.jsp")//钱包微信充值
    Observable<ResponseBody> createWXPayOrder(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/changePayPwd.jsp")
    Observable<ResponseBody> changePayPwd(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/cityDistribution/updataPayPwd.jsp")
    Observable<ResponseBody> updataPayPwd(@FieldMap Map<String, String> params);



    @FormUrlEncoded
    @POST("showdata/cityDistribution/findMyTxsq.jsp")
    Observable<ResponseBody> findMyTxsq(@Field("rowid") String rowid, @Field("token") String token, @Field("mobile") String mobile, @Field("app") String app);

    @FormUrlEncoded
    @POST("showdata/mox/pickup.jsp")
    Observable<ResponseBody> pickup(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/mox/sendlocation.jsp")
    Observable<ResponseBody> getLocation(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/alct/cityErrorLog.jsp")
    Observable<ResponseBody> uplaodErrorInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/loadrzrequest_new.jsp")
    Observable<ResponseBody> loadRzInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(Constants.UPLOADE_URL)
    Observable<ResponseBody> upLoadImg(@FieldMap(encoded = true) Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/image_upload_new.jsp")
    Observable<ResponseBody> image_upload_new(@FieldMap Map<String, String> params);

    @GET
    Observable<ResponseBody> download(@Url String fileUrl);

}
