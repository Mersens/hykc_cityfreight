package com.hykc.cityfreight.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.entity.EventEntity;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.service.ServiceStore;
import com.hykc.cityfreight.service.WbCoudFaceManager;
import com.hykc.cityfreight.utils.FileUtil;
import com.hykc.cityfreight.utils.RecognizeService;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.RxBus;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.hykc.cityfreight.view.FactTestDialog;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RzTextActivity extends BaseActivity {
    public static final String SFZ_ZNAME = "personal1.jpg";//身份证正面
    public static final String SFZ_FNAME = "personal2.jpg";//身份证反面
    public static final String JSZNAME = "personal3.jpg";//驾驶证
    public static final String XSZNAME = "vehicle0.jpg";//行驶证
    public static final int SFZ_Z = 1;//身份证正面
    public static final int SFZ_F = 2;//身份证反面
    public static final int JSZ = 3;//驾驶证
    private static final int REQUEST_CODE_CAMERA = 102;
    private static final int REQUEST_CODE_VEHICLE_LICENSE = 120;
    private static final int REQUEST_CODE_DRIVING_LICENSE = 121;
    final LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
    private ImageView mImgBack;
    private int type=0;
    private Button mBtnNext;
    private EditText mEditName;
    private EditText mEditCardNum;
    private RelativeLayout layout_idCard_start;
    private RelativeLayout layout_idCard_end;
    private TextView num_start_name;
    private TextView num_start_end;
    private EditText editJSZ;
    private RelativeLayout layout_jsz_start;
    private RelativeLayout layout_jsz_end;
    private TextView jsz_start_name;
    private TextView jsz_start_end;
    private RelativeLayout layout_jsz_time;
    private TextView tv_jsz_time;
    private Spinner jszlxSpinner;
    private String jszlxItems[]=null;
    private TextView mTextSB_SFZ;
    private TextView mTextSB_SFZ_F;
    private TextView mTextSB_JSZ;
    private TimePickerView idCardStartPicker;
    private TimePickerView idCardEndPicker;
    private TimePickerView jszStartPicker;
    private TimePickerView jszEndPicker;
    private TimePickerView jszTimePicker;
    private UDriver driver = new UDriver();
    private UDriver mUDriver=null;
    private String id;
    private boolean hasGotToken = false;

    private static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private static String formatTime(String time){
        String t="";
        SimpleDateFormat d1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat d2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = d1.parse(time);
            t=d2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t;

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rz);
        init();
    }

    @Override
    public void init() {
        id= SharePreferenceUtil.getInstance(this).getUserId();
        type=getIntent().getIntExtra("rzType",0);
        initViews();
        initDatas();
        initTimePicker();
        initAccessToken();
        initEvent();
        getUserRzInfo();
    }

    private void getUserRzInfo() {
        if(TextUtils.isEmpty(id)){
            Toast.makeText(this, "id为空，请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        String userinfo=SharePreferenceUtil.getInstance(RzTextActivity.this).getUserinfo();
        Gson gson=new Gson();
        mUDriver=gson.fromJson(userinfo,UDriver.class);
        try {
            JSONObject object = new JSONObject(userinfo);
            String token=object.getString("token");
            if(TextUtils.isEmpty(token)){
                Toast.makeText(this, "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String,String> map=new HashMap<>();
            map.put("mobile",id);
            map.put("token",token);
            RequestManager.getInstance()
                    .mServiceStore
                    .getDirverInfo(map)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                        @Override
                        public void onSuccess(String msg) {
                            Log.e("getDirverInfo onSuccess", msg);
                            analysisJson(msg);
                        }

                        @Override
                        public void onError(String msg) {
                            Log.e("getDirverInfo onError", msg);
                        }
                    }));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
    private void analysisJson(String msg) {
        try {
            JSONObject object=new JSONObject(msg);
            if(object.getBoolean("success")){
                String str=object.getString("entity");
                Gson gson=new Gson();
                UDriver uDriver= gson.fromJson(str,UDriver.class);
                if(null!=uDriver){
                    mUDriver=uDriver;
                    setUDriverInfo(uDriver);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setUDriverInfo(UDriver uDriver){
        driver.setCar1Id(uDriver.getCar1Id());
        driver.setCar2Id(uDriver.getCar2Id());
        driver.setCar3Id(uDriver.getCar3Id());
        String driverName=uDriver.getDriverName();
        if(!TextUtils.isEmpty(driverName)){
            mEditName.setText(driverName);
            driver.setDriverName(driverName);
        }
        String identityNo=uDriver.getIdentityNo();
        if(!TextUtils.isEmpty(identityNo)){
            mEditCardNum.setText(identityNo);
            driver.setIdentityNo(identityNo);
        }
        String identity_effectiveStartDate=uDriver.getIdentity_effectiveStartDate();
        if(!TextUtils.isEmpty(identity_effectiveStartDate)){
            num_start_name.setText(identity_effectiveStartDate);
            driver.setIdentity_effectiveStartDate(identity_effectiveStartDate);
        }
        String identity_effectiveEndDate=uDriver.getIdentity_effectiveEndDate();
        if(!TextUtils.isEmpty(identity_effectiveEndDate)){
            num_start_end.setText(identity_effectiveEndDate);
            driver.setIdentity_effectiveEndDate(identity_effectiveEndDate);

        }
        String licenseNo=uDriver.getLicenseNo();
        if(!TextUtils.isEmpty(licenseNo)){
            editJSZ.setText(licenseNo);
            driver.setLicenseNo(licenseNo);
        }

        String drivingLicense_effectiveStartDate=uDriver.getDrivingLicense_effectiveStartDate();
        if(!TextUtils.isEmpty(drivingLicense_effectiveStartDate)){
            jsz_start_name.setText(drivingLicense_effectiveStartDate);
            driver.setDrivingLicense_effectiveStartDate(drivingLicense_effectiveStartDate);
        }
        String drivingLicense_effectiveEndDate=uDriver.getDrivingLicense_effectiveEndDate();
        if(!TextUtils.isEmpty(drivingLicense_effectiveEndDate)){
            jsz_start_end.setText(drivingLicense_effectiveEndDate);
            driver.setDrivingLicense_effectiveEndDate(drivingLicense_effectiveEndDate);
        }
        String licenseFirstGetDate=uDriver.getLicenseFirstGetDate();
        if(!TextUtils.isEmpty(licenseFirstGetDate)){
            tv_jsz_time.setText(licenseFirstGetDate);
            driver.setLicenseFirstGetDate(licenseFirstGetDate);
        }
        String licenseType=uDriver.getLicenseType();
        if(!TextUtils.isEmpty(licenseType)){
            int index=getlicenseTypePos(licenseType);
            jszlxSpinner.setSelection(index);
            driver.setLicenseType(licenseType);
        }
    }
    private int getlicenseTypePos(String name){
        int pos=1;
        for(int i=0;i<jszlxItems.length;i++){
            if(jszlxItems[i].equals(name)){
                pos=i;
                break;
            }
        }
        return pos;
    }


    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                Log.e("initAccessToken",error.getMessage());
            }
        }, getApplicationContext());
    }

    private void initTimePicker() {
        idCardStartPicker = new TimePickerBuilder(RzTextActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                num_start_name.setText(getTime(date));
                num_start_name.setTextColor(getResources().getColor(R.color.text_color_black));
                driver.setIdentity_effectiveStartDate(getTime(date));


            }
        }).build();
        idCardEndPicker = new TimePickerBuilder(RzTextActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                num_start_end.setText(getTime(date));
                num_start_end.setTextColor(getResources().getColor(R.color.text_color_black));
                driver.setIdentity_effectiveEndDate(getTime(date));

        }
        }).build();
        jszStartPicker = new TimePickerBuilder(RzTextActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                jsz_start_name.setText(getTime(date));
                jsz_start_name.setTextColor(getResources().getColor(R.color.text_color_black));
                driver.setDrivingLicense_effectiveStartDate(getTime(date));
            }
        }).build();
        jszEndPicker = new TimePickerBuilder(RzTextActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                jsz_start_end.setText(getTime(date));
                jsz_start_end.setTextColor(getResources().getColor(R.color.text_color_black));
                driver.setDrivingLicense_effectiveEndDate(getTime(date));
            }
        }).build();
        jszTimePicker = new TimePickerBuilder(RzTextActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_jsz_time.setText(getTime(date));
                tv_jsz_time.setTextColor(getResources().getColor(R.color.text_color_black));
                driver.setLicenseFirstGetDate(getTime(date));

            }
        }).build();

    }

    private String getTime(Date date){
        SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd");
        String time = format0.format(date);
        return time;
    }

    private void initViews() {
        mBtnNext = findViewById(R.id.btn_next);

        mEditName = findViewById(R.id.editname);
        mEditCardNum = findViewById(R.id.editnum);


        layout_idCard_start=findViewById(R.id.layout_idCard_start);

        layout_idCard_start=findViewById(R.id.layout_idCard_start);
        layout_idCard_end=findViewById(R.id.layout_idCard_end);
        num_start_name=findViewById(R.id.num_start_name);
        num_start_end=findViewById(R.id.num_start_end);
        editJSZ=findViewById(R.id.editJSZ);
        layout_jsz_start=findViewById(R.id.layout_jsz_start);
        layout_jsz_end=findViewById(R.id.layout_jsz_end);
        jsz_start_name=findViewById(R.id.jsz_start_name);
        jsz_start_end=findViewById(R.id.jsz_start_end);
        layout_jsz_time=findViewById(R.id.layout_jsz_time);
        tv_jsz_time=findViewById(R.id.tv_jsz_time);
        jszlxSpinner=findViewById(R.id.jszlxSpinner);
        mTextSB_SFZ=findViewById(R.id.tv_sfz_sb);
        mTextSB_JSZ=findViewById(R.id.tv_jsz_sb);
        mTextSB_SFZ_F=findViewById(R.id.tv_sfz_f_sb);
        mImgBack=findViewById(R.id.img_back);
    }

    private void initDatas() {

        jszlxItems= getResources().getStringArray(R.array.jszlx);
        driver.setLicenseType(jszlxItems[0]);
        ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(RzTextActivity.this, android.R.layout.simple_spinner_item, jszlxItems);
        adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jszlxSpinner.setAdapter(adapter6);
        jszlxSpinner.setSelection(0,true);
        jszlxSpinner.setDropDownVerticalOffset(dp2px(RzTextActivity.this, 36));
    }

    private void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        layout_idCard_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCardStartPicker.show();
            }
        });

        layout_idCard_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCardEndPicker.show();
            }
        });
        layout_jsz_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jszStartPicker.show();
            }
        });

        layout_jsz_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jszEndPicker.show();
            }
        });

        layout_jsz_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jszTimePicker.show();
            }
        });
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntentDatas();

            }
        });
        jszlxSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                driver.setLicenseType(jszlxItems[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTextSB_SFZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(RzTextActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        });
        mTextSB_SFZ_F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(RzTextActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_BACK);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        });
        mTextSB_JSZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(RzTextActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_DRIVING_LICENSE);

            }
        });

    }

    private void recIDCard(String idCardSide, final String filePath) {
        if(loadingDialogFragment!=null){
            loadingDialogFragment.show(getSupportFragmentManager(),"rzLoadingDialog");

        }
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);
        OCR.getInstance(this).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if(loadingDialogFragment!=null){
                    loadingDialogFragment.dismiss();
                }
                if (result != null) {
                    //IDCardResult front{direction=0, wordsResultNumber=6, address=河南省孟津县常袋乡马岭村, idNumber=410322199304228314, birthday=19930422, name=马新新, gender=男, ethnic=汉}
                    Log.e("recognizeIDCard","==="+result.toString());
                    mEditName.setText(result.getName().toString());
                    mEditCardNum.setText(result.getIdNumber().toString());
                    setImg(SFZ_Z,filePath);
                }
            }

            @Override
            public void onError(OCRError error) {
                if(loadingDialogFragment!=null){
                    loadingDialogFragment.dismiss();
                }
                Toast.makeText(RzTextActivity.this, error.getErrorCode(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void recIDCardBack(String idCardSide, final String filePath) {
        if(loadingDialogFragment!=null){
            loadingDialogFragment.show(getSupportFragmentManager(),"idcardLoadingDialog");
        }
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(50);
        OCR.getInstance(this).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if(loadingDialogFragment!=null){
                    loadingDialogFragment.dismiss();
                }
                if (result != null) {

                    Log.e("recognizeIDCardBack","==="+result.toString());
                    String startTime=result.getSignDate().toString();
                    if(!TextUtils.isEmpty(startTime)){
                        String time= formatTime(startTime);
                        num_start_name.setText(time);
                        num_start_name.setTextColor(getResources().getColor(R.color.text_color_black));
                        driver.setIdentity_effectiveStartDate(time);

                    }
                    String endTime=result.getExpiryDate().toString();
                    if(!TextUtils.isEmpty(endTime)){
                        String time= formatTime(endTime);
                        num_start_end.setText(time);
                        num_start_end.setTextColor(getResources().getColor(R.color.text_color_black));
                        driver.setIdentity_effectiveEndDate(time);

                    }
                    setImg(SFZ_F,filePath);
                }
            }

            @Override
            public void onError(OCRError error) {
                if(loadingDialogFragment!=null){
                    loadingDialogFragment.dismiss();
                }
                Toast.makeText(RzTextActivity.this, error.getErrorCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        recIDCardBack(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                    }
                }
            }
        }
        // 识别成功回调，行驶证识别
        else if (requestCode == REQUEST_CODE_VEHICLE_LICENSE && resultCode == Activity.RESULT_OK) {
            if(loadingDialogFragment!=null){
                loadingDialogFragment.show(getSupportFragmentManager(),"rzLoadingDialog");

            }

            RecognizeService.recVehicleLicense(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            if(loadingDialogFragment!=null){
                                loadingDialogFragment.dismiss();
                            }
                          Log.e("recVehicleLicense","==="+result);
                            if(!TextUtils.isEmpty(result)){
                                try {
                                    JSONObject jsonObject=new JSONObject(result);
                                    JSONObject object=new JSONObject(jsonObject.getString("words_result"));
                                    if(object.has("号牌号码")){
                                        JSONObject object1=new JSONObject(object.getString("号牌号码"));
                                        String cph=object1.getString("words");
                                        if(!TextUtils.isEmpty(cph)){

                                        }
                                    }
                                    if(object.has("车辆识别代号")){
                                        JSONObject object2=new JSONObject(object.getString("车辆识别代号"));
                                        String clsbm=object2.getString("words");
                                        if(!TextUtils.isEmpty(clsbm)){

                                        }
                                    }
                                    if(object.has("发动机号码")){
                                        JSONObject object2=new JSONObject(object.getString("车辆识别代号"));
                                        String clsbm=object2.getString("words");
                                        JSONObject object3=new JSONObject(object.getString("发动机号码"));
                                        String fsjh=object3.getString("words");
                                        if(!TextUtils.isEmpty(fsjh) && !TextUtils.isEmpty(clsbm)){

                                        }
                                    }
                                    if(object.has("所有人")){
                                        JSONObject object2=new JSONObject(object.getString("所有人"));
                                        String syr=object2.getString("words");
                                        if(!TextUtils.isEmpty(syr)){
                                        }

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
        }

        // 识别成功回调，驾驶证识别
       else if (requestCode == REQUEST_CODE_DRIVING_LICENSE && resultCode == Activity.RESULT_OK) {
            if(loadingDialogFragment!=null){
                loadingDialogFragment.showF(getSupportFragmentManager(),"rzLoadingDialog");
            }
             final String filePath=FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
            RecognizeService.recDrivingLicense(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            if(loadingDialogFragment!=null){
                                loadingDialogFragment.dismissAllowingStateLoss();
                            }
                            if(!TextUtils.isEmpty(result)){
                                try {
                                    JSONObject jsonObject=new JSONObject(result);
                                    JSONObject object=new JSONObject(jsonObject.getString("words_result"));
                                    if(object.has("证号")){
                                        JSONObject object1=new JSONObject(object.getString("证号"));
                                        String zh=object1.getString("words");
                                        if(!TextUtils.isEmpty(zh)){
                                            editJSZ.setText(zh);
                                            driver.setLicenseNo(zh);
                                        }
                                    }
                                    if(object.has("初次领证日期")){
                                        JSONObject object2=new JSONObject(object.getString("初次领证日期"));
                                        String cclzrq=object2.getString("words");
                                        if(!TextUtils.isEmpty(cclzrq)){
                                            String time= formatTime(cclzrq);
                                            tv_jsz_time.setText(time);
                                            tv_jsz_time.setTextColor(getResources().getColor(R.color.text_color_black));
                                            driver.setLicenseFirstGetDate(time);
                                        }
                                    }
                                    if(object.has("有效期限")){
                                        JSONObject object3=new JSONObject(object.getString("有效期限"));
                                        String jszStart=object3.getString("words");
                                        if(!TextUtils.isEmpty(jszStart)){
                                            String time= formatTime(jszStart);
                                            jsz_start_name.setText(time);
                                            jsz_start_name.setTextColor(getResources().getColor(R.color.text_color_black));
                                           driver.setDrivingLicense_effectiveStartDate(time);

                                        }
                                    }
                                    if(object.has("至")){
                                        JSONObject object3=new JSONObject(object.getString("至"));
                                        String jszEnd=object3.getString("words");
                                        if(!TextUtils.isEmpty(jszEnd)){
                                            String time= formatTime(jszEnd);
                                            jsz_start_end.setText(time);
                                            jsz_start_end.setTextColor(getResources().getColor(R.color.text_color_black));
                                            driver.setDrivingLicense_effectiveEndDate(time);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                setImg(JSZ,filePath);
                            }
                            Log.e("recDrivingLicense","==="+result);

                        }
                    });
        }
    }

    private void setIntentDatas() {
        final String name = mEditName.getText().toString().trim();
        final String cardNum = mEditCardNum.getText().toString().trim();
        final String jszh = editJSZ.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "姓名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        driver.setDriverName(name);
        if (TextUtils.isEmpty(cardNum)) {
            Toast.makeText(this, "身份证号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isLegalId(cardNum)) {
            Toast.makeText(this, "请输入正确身份证号", Toast.LENGTH_SHORT).show();
            return;
        }

        driver.setIdentityNo(cardNum);
        if (TextUtils.isEmpty(jszh)) {
            Toast.makeText(this, "驾驶证号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (jszh.length()!=18) {
            Toast.makeText(this, "请输入正确18位驾驶证号！", Toast.LENGTH_SHORT).show();
            return;
        }
        driver.setLicenseNo(jszh);

        if(TextUtils.isEmpty(driver.getIdentity_effectiveStartDate())){
            Toast.makeText(this, "身份证开始有效期不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(driver.getIdentity_effectiveEndDate())){
            Toast.makeText(this, "身份证结束有效期不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(driver.getLicenseFirstGetDate())){
            Toast.makeText(this, "首次驾驶证获得时间不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(driver.getDrivingLicense_effectiveStartDate())){
            Toast.makeText(this, "驾驶证开始有效日期不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(driver.getDrivingLicense_effectiveEndDate())){
            Toast.makeText(this, "驾驶证结束有效日期不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(driver.getLicenseType())){
            Toast.makeText(this, "驾驶证类型不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 身份证一致性校验
        //selectUDriverIsFaceTest(driver);
        // 跳转下一步
       doNext();

    }

    private void selectUDriverIsFaceTest(final UDriver driver) {
        final LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
        loadingDialogFragment.showF(getSupportFragmentManager(),"IsFaceTestView");

        Map<String,String> map=new HashMap<>();
        map.put("account",id);
        map.put("statu",1+"");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.selectUDriverIsFaceTest(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingDialogFragment.dismissAllowingStateLoss();
                ResponseBody body=response.body();
                String str= null;
                try {
                    if(null!=body){
                        str = body.string();
                        JSONObject object=new JSONObject(str);
                        boolean success=object.getBoolean("success");
                        if(success){
                            //已校验
                            doNext();
                        }else {
                            //未校验
                            showFaceView(driver);
                        }
                    }else {

                        Toast.makeText(RzTextActivity.this, "用户信息查询失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("selectUDriverIsFaceTest","selectUDriverIsFaceTest=="+str);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingDialogFragment.dismissAllowingStateLoss();
                Log.e("onFailure","onFailure=="+t.getMessage());
                Toast.makeText(RzTextActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFaceView(final UDriver driver) {
        final ExitDialogFragment dialogFragment=ExitDialogFragment.getInstance("请进行刷脸认证！");
        dialogFragment.show(getSupportFragmentManager(),"showFaceView");
        dialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialogFragment.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialogFragment.dismissAllowingStateLoss();
                idcardFaceVerify(driver);
            }
        });

    }

    private void idcardFaceVerify(final UDriver driver) {
        final LoadingDialogFragment faceLoading=LoadingDialogFragment.getInstance();
        faceLoading.showF(getSupportFragmentManager(),"faceLoading");
        Map<String,String> map=new HashMap<>();
        map.put("account",id);
        map.put("name",driver.getDriverName());
        map.put("identity",driver.getIdentityNo());
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.idcardFaceVerify(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                faceLoading.dismissAllowingStateLoss();
                ResponseBody body=response.body();
                String str= null;
                try {
                    if(null!=body){
                        str = body.string();
                        JSONObject object=new JSONObject(str);
                        boolean success=object.getBoolean("success");
                        if(success){
                            JSONObject jsonObject=new JSONObject(object.getString("msg"));
                            Log.d("WbCoudFaceManager","获取数字签名");
                            Log.d("WbCoudFaceManager","idcardFaceVerify=="+jsonObject.toString());
                            initFaceTest(jsonObject,driver);
                        }else {

                            String error=object.getString("msg");
                            Toast.makeText(RzTextActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(RzTextActivity.this, "用户信息查询失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("idcardFaceVerify","idcardFaceVerify=="+str);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                faceLoading.dismissAllowingStateLoss();
                Log.e("onFailure","onFailure=="+t.getMessage());
            }
        });

    }

    private void initFaceTest(JSONObject jsonObject, final UDriver driver) throws JSONException {
        String orderNo=jsonObject.getString("orderNo");
        String webankUserid=jsonObject.getString("webankUserId");
        String randomStr=jsonObject.getString("randomStr");
        String faceAuthSign=jsonObject.getString("faceAuthSign");
        final String name=driver.getDriverName();
        final String idNo=driver.getIdentityNo();
        WbCoudFaceManager wbCoudFaceManager=new WbCoudFaceManager(webankUserid,
                randomStr,
                orderNo,
                Constants.FACE_APPID,
                idNo,
                name,
                faceAuthSign,
                RzTextActivity.this
        );
        wbCoudFaceManager.setOnFaceListener(new WbCoudFaceManager.OnFaceListener() {
            @Override
            public void onSucccess() {
                Log.e("wbCoudFaceManager","initFaceTest==onSucccess");
                addDriverSignInfo(id,name,idNo,0,1);

            }
            @Override
            public void onFail(String msg) {
                Toast.makeText(RzTextActivity.this,
                        msg, Toast.LENGTH_SHORT).show();
                Log.e("wbCoudFaceManager","initFaceTest=="+msg);
                showFaceTestView(id,name,idNo,0);
            }
        });
        wbCoudFaceManager.execute();

    }
    private void showFaceTestView(final String id, final String name, final String idNo,final int statu) {
        final FactTestDialog factTestDialog=FactTestDialog.newInstance(id,name,idNo);
        factTestDialog.show(getSupportFragmentManager(),"showFaceTestView");
        factTestDialog.setOnCheckListener(new FactTestDialog.OnCheckListener() {
            @Override
            public void onCheck() {
                factTestDialog.dismiss();
                addDriverSignInfo(id,name,idNo,1,statu);
            }

            @Override
            public void onDismiss() {
                factTestDialog.dismiss();
            }
        });

    }
    private void addDriverSignInfo(String account,String name,String identity,int fromStatus ,final int statu) {
        Map<String, String> map = new HashMap<>();
        map.put("account", account);
        map.put("name", name);
        map.put("identity", identity);
        map.put("fromStatus",fromStatus+"");
        map.put("statu", statu+"");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call = serviceStore.addDriverSignInfo(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body = response.body();
                String str = null;
                try {
                    if (null != body) {
                        str = body.string();
                        JSONObject object = new JSONObject(str);
                        boolean success = object.getBoolean("success");
                        if(success){
                            doNext();
                        }else {
                            Toast.makeText(RzTextActivity.this, "认证失败！", Toast.LENGTH_SHORT).show();

                        }
                    }else {
                        Toast.makeText(RzTextActivity.this, "认证失败！", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("idcardFaceVerify", "idcardFaceVerify==" + str);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "onFailure==" + t.getMessage());
                Toast.makeText(RzTextActivity.this, "认证失败！", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void doNext() {
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(),"rztext");
        String userinfo=SharePreferenceUtil.getInstance(RzTextActivity.this).getUserinfo();
        try {
            JSONObject object = new JSONObject(userinfo);
            String token=object.getString("token");
            if(TextUtils.isEmpty(token)){
                Toast.makeText(this, "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String,String> map=new HashMap<>();
            map.put("mobile",id);
            map.put("token",token);
            map.put("driverName",driver.getDriverName());
            map.put("identityNo",driver.getIdentityNo());
            map.put("identity_effectiveStartDate",driver.getIdentity_effectiveStartDate());
            map.put("identity_effectiveEndDate",driver.getIdentity_effectiveEndDate());
            map.put("licenseNo",driver.getLicenseNo());
            map.put("drivingLicense_effectiveStartDate",driver.getDrivingLicense_effectiveStartDate());
            map.put("drivingLicense_effectiveEndDate",driver.getDrivingLicense_effectiveEndDate());
            map.put("licenseFirstGetDate",driver.getLicenseFirstGetDate());
            map.put("licenseType",driver.getLicenseType());
            map.put("status","3");
            map.put("car1Id",driver.getCar1Id()+"");
            map.put("car2Id",driver.getCar2Id()+"");
            map.put("car3Id",driver.getCar3Id()+"");
            RequestManager.getInstance()
                    .mServiceStore
                    .submitAuthenticationInfo(map)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                        @Override
                        public void onSuccess(String msg) {
                            Log.e(" save onSuccess", msg);
                            dialogFragment.dismissAllowingStateLoss();
                            try {
                                JSONObject jsonObject=new JSONObject(msg);
                                if(jsonObject.getBoolean("success")){
                                    Intent intent = new Intent(RzTextActivity.this, AddCarInfoActivity.class);
                                    intent.putExtra("type",0);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                    RxBus.getInstance().send(new EventEntity("1002","实名认证"));
                                }else {
                                    String error=jsonObject.getString("msg");
                                    Toast.makeText(RzTextActivity.this, "提交失败！"+error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(String msg) {
                            dialogFragment.dismissAllowingStateLoss();
                            Log.e("save onError", msg);
                            Toast.makeText(RzTextActivity.this, "提交失败！"+msg, Toast.LENGTH_SHORT).show();

                        }
                    }));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private boolean isLegalId(String id) {
        if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")) {
            return true;
        } else {
            return false;
        }
    }
    private String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        if (bitmap != null) {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] bitmapBytes = baos.toByteArray();
            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT).replaceAll(" ", "");
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private Bitmap compressImg(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(imagePath, options);
        int h = options.outHeight;
        int w = options.outWidth;
        float hh = 1280f;//这里设置高度为1280f
        float ww = 720f;//这里设置宽度为720f
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (options.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (options.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = be; // 设置为刚才计算的压缩比例
        return BitmapFactory.decodeFile(imagePath, options); // 解码文件
    }


    private void setImg(int type,String path){
        if(TextUtils.isEmpty(path)){
            return;
        }
        if(mUDriver==null){
            return;
        }
        String name =  getTime(new Date())+".jpg";
        Bitmap bitmap = compressImg(path);
        String ImgBuffer = bitmapToBase64(bitmap);
        String uploadBuffer=ImgBuffer.replaceAll("\\+","-");
        if(TextUtils.isEmpty(uploadBuffer)){
            Toast.makeText(this, "照片为空，请重新选择！", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (type){
            case SFZ_Z:
                upLoadImg(name,type,uploadBuffer);
                break;
            case SFZ_F:
                upLoadImg(name,type,uploadBuffer);
                break;
            case JSZ:
                upLoadImg(name,type,uploadBuffer);
                break;
        }
    }
    private void upLoadImg(final String fileName,final int imgType,String uploadBuffer){

        Map<String, String> m = new HashMap<>();
        m.put("mobile", id);
        m.put("fileName", fileName);
        m.put("base64", uploadBuffer);
        m.put("id",mUDriver.getId()+"");
        m.put("imgType",imgType+"");
        RequestManager.getInstance()
                .mServiceStore
                .upLoadReImg(m)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String s) {

                        try {
                            JSONObject jsonObject=new JSONObject(s);
                            if(jsonObject.getBoolean("success")){

                            }else {
                                String error=jsonObject.getString("message");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("upLoadImgToService", msg);

                    }
                }));
    }
}
