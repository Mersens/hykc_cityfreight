package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.UCarEntity;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class CheckRzTextActivity extends BaseActivity {
    private ImageView mImgBack;
    private TextView mTextResetRz;
    private Button btn;
    private TextView mTextName;
    private TextView mTextCardNum;
    private TextView mTextCardStartTime;
    private TextView mTextCardStartEnd;

    private TextView mTextJSZ;
    private TextView mTextJSZStartTime;
    private TextView mTextJSZEndTime;
    private TextView mTextSCLZTime;
    private TextView mTextTEL;

    private LinearLayout mLayoutCarInfo1;
    private LinearLayout mLayoutCarInfo2;
    private LinearLayout mLayoutCarInfo3;

    //车辆信息1
    private TextView mTextSYR;
    private TextView mTextCPH;
    private TextView mTextCLSBM;
    private TextView mTextFDJH;
    private TextView mTextZZ;
    private TextView mTextDLYSZ;
    private TextView mTextJZLX;
    private TextView mTextCLLX;
    private TextView mTextCX;
    private TextView mTextCC;
    private TextView mTextCPLX;
    private TextView mTextCLFL;
    //车辆信息2
    private TextView mTextSYR2;
    private TextView mTextCPH2;
    private TextView mTextCLSBM2;
    private TextView mTextFDJH2;
    private TextView mTextZZ2;
    private TextView mTextDLYSZ2;
    private TextView mTextJZLX2;
    private TextView mTextCLLX2;
    private TextView mTextCX2;
    private TextView mTextCC2;
    private TextView mTextCPLX2;
    private TextView mTextCLFL2;
    //车辆信息3
    private TextView mTextSYR3;
    private TextView mTextCPH3;
    private TextView mTextCLSBM3;
    private TextView mTextFDJH3;
    private TextView mTextZZ3;
    private TextView mTextDLYSZ3;
    private TextView mTextJZLX3;
    private TextView mTextCLLX3;
    private TextView mTextCX3;
    private TextView mTextCC3;
    private TextView mTextCPLX3;
    private TextView mTextCLFL3;

    private String cxItems[] = null;
    private String cplxItems[] = null;
    private String clflItems[] = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_check_rz_text);
        init();
    }

    @Override
    public void init() {
        cplxItems = getResources().getStringArray(R.array.cplx);
        clflItems = getResources().getStringArray(R.array.clfl);

        initViews();
        initEvent();
        initDatas();
    }



    private void initDatas() {
        final String userid = SharePreferenceUtil.getInstance(this).getUserId();
        if (TextUtils.isEmpty(userid)) {
            Toast.makeText(this, "请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        String userinfo = SharePreferenceUtil.getInstance(CheckRzTextActivity.this).getUserinfo();
        try {
            JSONObject object = new JSONObject(userinfo);
            final String token = object.getString("token");
            if (TextUtils.isEmpty(token)) {
                Toast.makeText(this, "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, String> map = new HashMap<>();
            map.put("mobile", userid);
            map.put("token", token);
            RequestManager.getInstance()
                    .mServiceStore
                    .getDirverInfo(map)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                        @Override
                        public void onSuccess(String msg) {
                            Log.e("getDirverInfo onSuccess", msg);
                            analysisJson(msg, userid, token);
                        }

                        @Override
                        public void onError(String msg) {
                            Log.e("getDirverInfo onError", msg);
                        }
                    }));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void analysisJson(final String msg, final String userid, final String token) {
        try {
            JSONObject object = new JSONObject(msg);
            if (object.getBoolean("success")) {
                String str = object.getString("entity");
                Gson gson = new Gson();
                UDriver uDriver = gson.fromJson(str, UDriver.class);
                if (null != uDriver) {
                    setUDriverInfo(uDriver);
                    getCarInfo(uDriver, userid, token);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCarInfo(UDriver uDriver, String userid, final String token) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", userid);
        map.put("token", token);
        map.put("driverId1", uDriver.getId() + "");
        map.put("driverId2", uDriver.getId() + "");
        map.put("driverId3", uDriver.getId() + "");
        RequestManager.getInstance()
                .mServiceStore
                .selectCarInfoByDriverId(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("getCarInfo onSuccess", msg);
                        analysisJson(msg);
                    }

                    @Override
                    public void onError(String msg) {
                        mLayoutCarInfo1.setVisibility(View.GONE);
                        Log.e("getCarInfo onError", msg);
                    }
                }));

    }

    private void analysisJson(String msg) {
        try {
            JSONObject object=new JSONObject(msg);
            if(object.getBoolean("success")){
                String str=object.getString("entity");
                if(!TextUtils.isEmpty(str)){
                    JSONArray array=new JSONArray(str);
                    for (int i = 0; i <array.length() ; i++) {
                        String arr= array.getString(i);
                        Gson gson=new Gson();
                        UCarEntity uCarEntity=gson.fromJson(arr,UCarEntity.class);
                        if(uCarEntity!=null){
                            setDatas(i,uCarEntity);

                        }
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void  setDatas(int index,UCarEntity uCarEntity){
        switch (index){
            case 0:
                mLayoutCarInfo1.setVisibility(View.VISIBLE);
                setCarInfo1(uCarEntity);
                break;
            case 1:
                mLayoutCarInfo2.setVisibility(View.VISIBLE);
                setCarInfo2(uCarEntity);
                break;
            case 2:
                mLayoutCarInfo3.setVisibility(View.VISIBLE);
                setCarInfo3(uCarEntity);
                break;
        }

    }

    private void setCarInfo3(UCarEntity uCarEntity){
        String owner_p=uCarEntity.getOwner_p();
        if(!TextUtils.isEmpty(owner_p)){
            mTextSYR3.setText(owner_p);
        }
        String licensePlateNo=uCarEntity.getLicensePlateNo();
        if(!TextUtils.isEmpty(licensePlateNo)){
            mTextCPH3.setText(licensePlateNo);
        }
        String vehicleIdentityCode=uCarEntity.getVehicleIdentityCode();
        if(!TextUtils.isEmpty(vehicleIdentityCode)){
            mTextCLSBM3.setText(vehicleIdentityCode);
        }
        String engineNumber=uCarEntity.getEngineNumber();
        if(!TextUtils.isEmpty(engineNumber)){
            mTextFDJH3.setText(engineNumber);
        }

        String load_p=uCarEntity.getLoad_p();
        if(!TextUtils.isEmpty(load_p)){
            mTextZZ3.setText(load_p+" 吨");
        }
        String transportLicenseNo=uCarEntity.getTransportLicenseNo();
        if(!TextUtils.isEmpty(transportLicenseNo)){
            mTextDLYSZ3.setText(transportLicenseNo);
        }

        String standardVehicleType=uCarEntity.getStandardVehicleType();
        if(!TextUtils.isEmpty(standardVehicleType)){
            mTextCX3.setText(standardVehicleType);
        }
        String car_len=uCarEntity.getCar_len();
        if(!TextUtils.isEmpty(car_len)){
            mTextCC3.setText(car_len);
        }
        String cplx=uCarEntity.getCplx();
        if(!TextUtils.isEmpty(cplx)){
            int index=getlicenseCPLXPos(cplx);
            mTextCPLX3.setText(cplxItems[index]);
        }
        String clfl=uCarEntity.getClfl();
        if(!TextUtils.isEmpty(clfl)){
            int index=getlicenseCLFLPos(clfl);
            mTextCLFL3.setText(clflItems[index]);
        }
        String brand=uCarEntity.getBrand();
        if(!TextUtils.isEmpty(brand)){
            mTextCLLX3.setText(brand);
        }

    }
    private void setCarInfo2(UCarEntity uCarEntity){
        String owner_p=uCarEntity.getOwner_p();
        if(!TextUtils.isEmpty(owner_p)){
            mTextSYR2.setText(owner_p);
        }
        String licensePlateNo=uCarEntity.getLicensePlateNo();
        if(!TextUtils.isEmpty(licensePlateNo)){
            mTextCPH2.setText(licensePlateNo);
        }
        String vehicleIdentityCode=uCarEntity.getVehicleIdentityCode();
        if(!TextUtils.isEmpty(vehicleIdentityCode)){
            mTextCLSBM2.setText(vehicleIdentityCode);
        }
        String engineNumber=uCarEntity.getEngineNumber();
        if(!TextUtils.isEmpty(engineNumber)){
            mTextFDJH2.setText(engineNumber);
        }

        String load_p=uCarEntity.getLoad_p();
        if(!TextUtils.isEmpty(load_p)){
            mTextZZ2.setText(load_p+" 吨");
        }
        String transportLicenseNo=uCarEntity.getTransportLicenseNo();
        if(!TextUtils.isEmpty(transportLicenseNo)){
            mTextDLYSZ2.setText(transportLicenseNo);
        }

        String standardVehicleType=uCarEntity.getStandardVehicleType();
        if(!TextUtils.isEmpty(standardVehicleType)){
            mTextCX2.setText(standardVehicleType);
        }
        String car_len=uCarEntity.getCar_len();
        if(!TextUtils.isEmpty(car_len)){
            mTextCC2.setText(car_len);
        }
        String cplx=uCarEntity.getCplx();
        if(!TextUtils.isEmpty(cplx)){
            int index=getlicenseCPLXPos(cplx);
            mTextCPLX2.setText(cplxItems[index]);
        }
        String clfl=uCarEntity.getClfl();
        if(!TextUtils.isEmpty(clfl)){
            int index=getlicenseCLFLPos(clfl);
            mTextCLFL2.setText(clflItems[index]);
        }
        String brand=uCarEntity.getBrand();
        if(!TextUtils.isEmpty(brand)){
            mTextCLLX2.setText(brand);
        }

    }

    private void setCarInfo1(UCarEntity uCarEntity){
        String owner_p=uCarEntity.getOwner_p();
        if(!TextUtils.isEmpty(owner_p)){
            mTextSYR.setText(owner_p);
        }
        String licensePlateNo=uCarEntity.getLicensePlateNo();
        if(!TextUtils.isEmpty(licensePlateNo)){
            mTextCPH.setText(licensePlateNo);
        }
        String vehicleIdentityCode=uCarEntity.getVehicleIdentityCode();
        if(!TextUtils.isEmpty(vehicleIdentityCode)){
            mTextCLSBM.setText(vehicleIdentityCode);
        }
        String engineNumber=uCarEntity.getEngineNumber();
        if(!TextUtils.isEmpty(engineNumber)){
            mTextFDJH.setText(engineNumber);
        }
        String mobile= uCarEntity.getMobile();
        if(!TextUtils.isEmpty(mobile)){
            mTextTEL.setText(mobile);
        }
        String load_p=uCarEntity.getLoad_p();
        if(!TextUtils.isEmpty(load_p)){
            mTextZZ.setText(load_p+" 吨");
        }
        String transportLicenseNo=uCarEntity.getTransportLicenseNo();
        if(!TextUtils.isEmpty(transportLicenseNo)){
            mTextDLYSZ.setText(transportLicenseNo);
        }

        String standardVehicleType=uCarEntity.getStandardVehicleType();
        if(!TextUtils.isEmpty(standardVehicleType)){
            mTextCX.setText(standardVehicleType);
        }
        String car_len=uCarEntity.getCar_len();
        if(!TextUtils.isEmpty(car_len)){
            mTextCC.setText(car_len);
        }
        String cplx=uCarEntity.getCplx();
        if(!TextUtils.isEmpty(cplx)){
            int index=getlicenseCPLXPos(cplx);
            mTextCPLX.setText(cplxItems[index]);
        }
        String clfl=uCarEntity.getClfl();
        if(!TextUtils.isEmpty(clfl)){
            int index=getlicenseCLFLPos(clfl);
            mTextCLFL.setText(clflItems[index]);
        }
        String brand=uCarEntity.getBrand();
        if(!TextUtils.isEmpty(brand)){
            mTextCLLX.setText(brand);
        }

    }
    private int getlicenseCPLXPos(String name){
        int pos=0;
        for(int i=0;i<cplxItems.length;i++){
            if(cplxItems[i].equals(name)){
                pos=i;
                break;
            }
        }
        return pos;

    }
    private int getlicenseCXPos(String name){
        int pos=0;
        for(int i=0;i<cxItems.length;i++){
            if(cxItems[i].equals(name)){
                pos=i;
                break;
            }
        }
        return pos;

    }
    private int getlicenseCLFLPos(String name){
        int pos=0;
        for(int i=0;i<clflItems.length;i++){
            if(clflItems[i].equals(name)){
                pos=i;
                break;
            }
        }
        return pos;

    }

    private void setUDriverInfo(UDriver uDriver) {
        String driverName = uDriver.getDriverName();
        if (!TextUtils.isEmpty(driverName)) {
            mTextName.setText(driverName);
        }
        String mobile=uDriver.getMobile();
        if(!TextUtils.isEmpty(mobile)){
            mTextTEL.setText(mobile);
        }
        String identityNo = uDriver.getIdentityNo();
        if (!TextUtils.isEmpty(identityNo)) {
            mTextCardNum.setText(identityNo);
        }
        String identity_effectiveStartDate = uDriver.getIdentity_effectiveStartDate();
        if (!TextUtils.isEmpty(identity_effectiveStartDate)) {
            mTextCardStartTime.setText(identity_effectiveStartDate);
        }
        String identity_effectiveEndDate = uDriver.getIdentity_effectiveEndDate();
        if (!TextUtils.isEmpty(identity_effectiveEndDate)) {
            mTextCardStartEnd.setText(identity_effectiveEndDate);

        }
        String licenseNo = uDriver.getLicenseNo();
        if (!TextUtils.isEmpty(licenseNo)) {
            mTextJSZ.setText(licenseNo);
        }

        String drivingLicense_effectiveStartDate = uDriver.getDrivingLicense_effectiveStartDate();
        if (!TextUtils.isEmpty(drivingLicense_effectiveStartDate)) {
            mTextJSZStartTime.setText(drivingLicense_effectiveStartDate);
        }
        String drivingLicense_effectiveEndDate = uDriver.getDrivingLicense_effectiveEndDate();
        if (!TextUtils.isEmpty(drivingLicense_effectiveEndDate)) {
            mTextJSZEndTime.setText(drivingLicense_effectiveEndDate);
        }
        String licenseFirstGetDate = uDriver.getLicenseFirstGetDate();
        if (!TextUtils.isEmpty(licenseFirstGetDate)) {
            mTextSCLZTime.setText(licenseFirstGetDate);
        }
        String licenseType=uDriver.getLicenseType();
        if(!TextUtils.isEmpty(licenseType)){
            mTextJZLX.setText(licenseType);
        }
    }


    private void initViews() {
        mImgBack=findViewById(R.id.img_back);
        mTextResetRz = findViewById(R.id.tv_add);
        mTextName = findViewById(R.id.tv_name);
        mTextCardNum = findViewById(R.id.tv_num);
        mTextCardStartTime = findViewById(R.id.num_start_Time);
        mTextCardStartEnd = findViewById(R.id.num_start_end);
        mTextJSZ = findViewById(R.id.tv_jsz);
        mTextJSZStartTime = findViewById(R.id.jsz_start_name);
        mTextJSZEndTime = findViewById(R.id.jsz_start_end);
        mTextSCLZTime = findViewById(R.id.tv_jsz_time);
        mTextTEL = findViewById(R.id.tv_tel);
        mLayoutCarInfo1=findViewById(R.id.layout_carinfo1);
        mLayoutCarInfo2=findViewById(R.id.layout_carinfo2);
        mLayoutCarInfo3=findViewById(R.id.layout_carinfo3);

        //车辆信息2
        mTextSYR2 = findViewById(R.id.tv_syr2);
        mTextCPH2 = findViewById(R.id.tv_cph2);
        mTextCLSBM2 = findViewById(R.id.tv_clsbm2);
        mTextFDJH2 = findViewById(R.id.tv_fdjh2);
        mTextZZ2 = findViewById(R.id.tv_zz2);
        mTextDLYSZ2 = findViewById(R.id.tv_dlysz2);
        mTextJZLX2 = findViewById(R.id.tv_jszlx2);
        mTextCLLX2 = findViewById(R.id.tv_cllx2);
        mTextCX2 = findViewById(R.id.tv_cx2);
        mTextCC2 = findViewById(R.id.tv_cc2);
        mTextCPLX2 = findViewById(R.id.tv_cplx2);
        mTextCLFL2 = findViewById(R.id.tv_clfl2);
        //车辆信息1
        mTextSYR = findViewById(R.id.tv_syr);
        mTextCPH = findViewById(R.id.tv_cph);
        mTextCLSBM = findViewById(R.id.tv_clsbm);
        mTextFDJH = findViewById(R.id.tv_fdjh);
        mTextZZ = findViewById(R.id.tv_zz);
        mTextDLYSZ = findViewById(R.id.tv_dlysz);
        mTextJZLX = findViewById(R.id.tv_jszlx);
        mTextCLLX = findViewById(R.id.tv_cllx);
        mTextCX = findViewById(R.id.tv_cx);
        mTextCC = findViewById(R.id.tv_cc);
        mTextCPLX = findViewById(R.id.tv_cplx);
        mTextCLFL = findViewById(R.id.tv_clfl);
        //车辆信息3
        mTextSYR3 = findViewById(R.id.tv_syr3);
        mTextCPH3 = findViewById(R.id.tv_cph3);
        mTextCLSBM3 = findViewById(R.id.tv_clsbm3);
        mTextFDJH3 = findViewById(R.id.tv_fdjh3);
        mTextZZ3 = findViewById(R.id.tv_zz3);
        mTextDLYSZ3 = findViewById(R.id.tv_dlysz3);
        mTextJZLX3 = findViewById(R.id.tv_jszlx3);
        mTextCLLX3 = findViewById(R.id.tv_cllx3);
        mTextCX3 = findViewById(R.id.tv_cx3);
        mTextCC3 = findViewById(R.id.tv_cc3);
        mTextCPLX3 = findViewById(R.id.tv_cplx3);
        mTextCLFL3 = findViewById(R.id.tv_clfl3);

        btn=findViewById(R.id.btn_next);
    }

    private void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        mTextResetRz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckRzTextActivity.this, RzTextActivity.class);
                intent.putExtra("rzType", 1);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckRzTextActivity.this, CheckRzImgActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }


}
