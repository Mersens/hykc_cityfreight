package com.hykc.cityfreight.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.baidu.ocr.ui.camera.CameraActivity;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.UCarEntity;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.utils.CarInfoUtils;
import com.hykc.cityfreight.utils.FileUtil;
import com.hykc.cityfreight.utils.RecognizeService;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;


public class AddCarInfoActivity extends BaseActivity {
    private static final int REQUEST_CODE_CAMERA = 102;
    private static final int REQUEST_CODE_VEHICLE_LICENSE = 120;
    private static final int REQUEST_CODE_DRIVING_LICENSE = 121;
    final LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
    private ImageView mImgBack;
    private int type=-1;
    private Button mBtnNext;
    private EditText mEditTel;
    private EditText mEditCph;
    private EditText mEditZz;
    private EditText mEditDlysz;
    private Spinner mCDSpinner;
    private Spinner mCPLXSpinner;
    private Spinner mCLFLSpinner;
    private String cdItems[] = null;
    private String cplxItems[] = null;
    private String clflItems[] = null;
    private EditText editClsbm;
    private EditText editFdjh;
    private EditText editSYR;
    private TextView mTextSB_XSZ;
    private UCarEntity uCarEntity = new UCarEntity();
    private String id;
    private boolean hasGotToken = false;
    private TextView mTextDLStartTime;
    private TextView mTextDLEndTime;
    private RelativeLayout mLayoutDLStartTime;
    private RelativeLayout mLayoutDLEndTime;
    private EditText mEditJdcdjh;
    private EditText mEditNFCid;
    private TimePickerView dLStartPicker;
    private TimePickerView dLEndPicker;
    private String strDLStartTime;
    private String strDLEndTime;
    private EditText mEditpp;
    private EditText mEditcllx;

    private static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_carinfo);
        init();
    }

    @Override
    public void init() {

        if(getIntent().hasExtra("type")){
            type=getIntent().getIntExtra("type",-1);
        }
        type=getIntent().getIntExtra("rzType",0);
        initViews();
        initDatas();
        initAccessToken();
        initEvent();
        downLoadRzMsg();
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
            }
        }, getApplicationContext());
    }

    private void initViews() {
        id=SharePreferenceUtil.getInstance(this).getUserId();
        mEditJdcdjh=findViewById(R.id.editJdcdjh);
        mEditNFCid=findViewById(R.id.editNFCid);
        mTextDLStartTime=findViewById(R.id.num_start_name);
        mTextDLEndTime=findViewById(R.id.num_start_end);
        mLayoutDLStartTime=findViewById(R.id.layout_dljyxkz_start);
        mLayoutDLEndTime=findViewById(R.id.layout_dljyxkz_end);
        mBtnNext = findViewById(R.id.btn_next);
        if(type==0){
            mBtnNext.setText("下一步");
        }else if(type==1) {
            mBtnNext.setText("完成");
        }
        mImgBack=findViewById(R.id.img_back);
        mEditTel = findViewById(R.id.editTel);

        if(!TextUtils.isEmpty(id)){
            mEditTel.setText(id);
        }
        mEditCph = findViewById(R.id.editCph);
        mEditZz = findViewById(R.id.editZz);
        mEditDlysz = findViewById(R.id.editDlysz);
        mCDSpinner = findViewById(R.id.ccSpinner);
        mCPLXSpinner = findViewById(R.id.cplxSpinner);
        mCLFLSpinner = findViewById(R.id.clflSpinner);
        editClsbm=findViewById(R.id.editClsbm);
        editFdjh=findViewById(R.id.editFdjh);
        mTextSB_XSZ=findViewById(R.id.tv_xsz_sb);
        editSYR=findViewById(R.id.editSYR);
        mEditpp=findViewById(R.id.editclpp);
        mEditcllx=findViewById(R.id.editcllx);
    }

    private void initDatas() {

        cdItems = getResources().getStringArray(R.array.cc);
        uCarEntity.setCar_len(cdItems[0]);
        cplxItems = getResources().getStringArray(R.array.cplx);
        String cplx = CarInfoUtils.getInstance().getLxIdByValue(cplxItems[0]);
        uCarEntity.setCplx(cplx);
        clflItems = getResources().getStringArray(R.array.clfl);
        String clfl = CarInfoUtils.getInstance().getFlIdByValue(clflItems[0]);
        uCarEntity.setClfl(clfl);

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(AddCarInfoActivity.this, android.R.layout.simple_spinner_item, cdItems);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCDSpinner.setAdapter(adapter3);
        mCDSpinner.setDropDownVerticalOffset(dp2px(AddCarInfoActivity.this, 36));

        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(AddCarInfoActivity.this, android.R.layout.simple_spinner_item, cplxItems);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCPLXSpinner.setAdapter(adapter4);
        mCPLXSpinner.setDropDownVerticalOffset(dp2px(AddCarInfoActivity.this, 36));

        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(AddCarInfoActivity.this, android.R.layout.simple_spinner_item, clflItems);
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCLFLSpinner.setAdapter(adapter5);
        mCLFLSpinner.setDropDownVerticalOffset(dp2px(AddCarInfoActivity.this, 36));

    }

    private void downLoadRzMsg() {
        getUserInfo();

    }

    private void getUserInfo(){
        final String userid = SharePreferenceUtil.getInstance(this).getUserId();
        if (TextUtils.isEmpty(userid)) {
            Toast.makeText(this, "请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        String userinfo = SharePreferenceUtil.getInstance(AddCarInfoActivity.this).getUserinfo();
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
                    if (array.length()==0){
                        confirmTips("暂无车辆信息");

                    }else if(array.length()>=1){{
                        confirmTips("默认显示一辆车辆信息\n"+"请到[我的车辆]中修改其他车辆");
                        String arr= array.getString(0);
                        Gson gson=new Gson();
                        uCarEntity=gson.fromJson(arr,UCarEntity.class);
                        setDatas();
                    }

                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setDatas(){
        String owner_p=uCarEntity.getOwner_p();
        if(!TextUtils.isEmpty(owner_p)){
            editSYR.setText(owner_p);
        }
        String licensePlateNo=uCarEntity.getLicensePlateNo();
        if(!TextUtils.isEmpty(licensePlateNo)){
            mEditCph.setText(licensePlateNo);
        }
        String vehicleIdentityCode=uCarEntity.getVehicleIdentityCode();
        if(!TextUtils.isEmpty(vehicleIdentityCode)){
            editClsbm.setText(vehicleIdentityCode);
        }
        String engineNumber=uCarEntity.getEngineNumber();
        if(!TextUtils.isEmpty(engineNumber)){
            editFdjh.setText(engineNumber);
        }

       /* String str=uCarEntity.getTransportLicenseExpireDate();
        if(!TextUtils.isEmpty(str) && str.contains("至")){
            String strs[]=str.split("至");
            mTextDLStartTime.setText(strs[0]);
            mTextDLStartTime.setTextColor(getResources().getColor(R.color.text_color_black));
            mTextDLEndTime.setText(strs[1]);
            mTextDLEndTime.setTextColor(getResources().getColor(R.color.text_color_black));
            strDLStartTime=strs[0];
            strDLEndTime=strs[1];
        }*/

        if(!TextUtils.isEmpty(uCarEntity.getTransportLicenseStartTime())){
            mTextDLStartTime.setTextColor(getResources().getColor(R.color.text_color_black));
            mTextDLStartTime.setText(uCarEntity.getTransportLicenseStartTime());
        }
        if(!TextUtils.isEmpty(uCarEntity.getTransportLicenseEndTime())){
            mTextDLEndTime.setTextColor(getResources().getColor(R.color.text_color_black));
            mTextDLEndTime.setText(uCarEntity.getTransportLicenseEndTime());
        }


        if(!TextUtils.isEmpty(uCarEntity.getVehicleRegistrationCertificateNo())){
            mEditJdcdjh.setText(uCarEntity.getVehicleRegistrationCertificateNo());
        }
        String nfcid=uCarEntity.getNfcId();
        if(!TextUtils.isEmpty(nfcid)){
            mEditNFCid.setText(nfcid);
        }

        String load_p=uCarEntity.getLoad_p();
        if(!TextUtils.isEmpty(load_p)){
            mEditZz.setText(load_p+" 吨");
        }
        String transportLicenseNo=uCarEntity.getTransportLicenseNo();
        if(!TextUtils.isEmpty(transportLicenseNo)){
            mEditDlysz.setText(transportLicenseNo);
        }

        String standardVehicleType=uCarEntity.getStandardVehicleType();
        if(!TextUtils.isEmpty(standardVehicleType)){
            mEditcllx.setText(standardVehicleType);
        }
        String car_len=uCarEntity.getCar_len();
        if(!TextUtils.isEmpty(car_len)){
            int index=getlicenseCDPos(car_len);
            mCDSpinner.setSelection(index);
        }else {
            uCarEntity.setCar_len(cdItems[0]);
        }
        String cplx=uCarEntity.getCplx();
        if(!TextUtils.isEmpty(cplx)){
            int index=getlicenseCPLXPos(cplx);
            mCPLXSpinner.setSelection(index);
        }
        String clfl=uCarEntity.getClfl();
        if(!TextUtils.isEmpty(clfl)){
            String val=CarInfoUtils.getInstance().getFlById(clfl);
            int index=getlicenseCLFLPos(val);
            mCLFLSpinner.setSelection(index,true);
        }
        String brand=uCarEntity.getBrand();
        if(!TextUtils.isEmpty(brand)){
            mEditpp.setText(brand);


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
    private int getlicenseCDPos(String name){
        int pos=0;
        for(int i=0;i<cdItems.length;i++){
            if(cdItems[i].equals(name)){
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
    private void confirmTips(String msg) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getSupportFragmentManager(), "confirmTips");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                dialog.dismiss();


            }
        });

    }

    private String getTime(Date date){
        SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd");
        String time = format0.format(date);
        return time;
    }


    private void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        dLStartPicker = new TimePickerBuilder(AddCarInfoActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mTextDLStartTime.setText(getTime(date));
                mTextDLStartTime.setTextColor(getResources().getColor(R.color.text_color_black));
                strDLStartTime=getTime(date);
                uCarEntity.setTransportLicenseStartTime(getTime(date));

            }
        }).build();
        dLEndPicker = new TimePickerBuilder(AddCarInfoActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                mTextDLEndTime.setText(getTime(date));
                mTextDLEndTime.setTextColor(getResources().getColor(R.color.text_color_black));
                strDLEndTime=getTime(date);
                uCarEntity.setTransportLicenseEndTime(getTime(date));


            }
        }).build();
        mLayoutDLStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dLStartPicker.show();
            }
        });
        mLayoutDLEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dLEndPicker.show();
            }
        });
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntentDatas();

            }
        });


        mCDSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("onItemSelected",cdItems[position]+";"+position);
                uCarEntity.setCar_len(cdItems[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mCPLXSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cplx = CarInfoUtils.getInstance().getLxIdByValue(cplxItems[position]);
                uCarEntity.setCplx(cplx);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mCLFLSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String clfl = CarInfoUtils.getInstance().getFlIdByValue(clflItems[position]);
                uCarEntity.setClfl(clfl);
                uCarEntity.setCartype(clfl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mTextSB_XSZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(AddCarInfoActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_VEHICLE_LICENSE);
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
                        //recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        //recIDCardBack(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                    }
                }
            }
        }
        // 识别成功回调，行驶证识别
        else if (requestCode == REQUEST_CODE_VEHICLE_LICENSE && resultCode == Activity.RESULT_OK) {

            RecognizeService.recVehicleLicense(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {

                            Log.e("recVehicleLicense","==="+result);
                            if(!TextUtils.isEmpty(result)){
                                try {
                                    JSONObject jsonObject=new JSONObject(result);
                                    JSONObject object=new JSONObject(jsonObject.getString("words_result"));
                                    if(object.has("号牌号码")){
                                        JSONObject object1=new JSONObject(object.getString("号牌号码"));
                                        String cph=object1.getString("words");
                                        if(!TextUtils.isEmpty(cph)){
                                            mEditCph.setText(cph);
                                        }
                                    }

                                    if(object.has("品牌型号")){
                                        JSONObject object1=new JSONObject(object.getString("品牌型号"));
                                        String pp=object1.getString("words");
                                        if(!TextUtils.isEmpty(pp)){
                                            mEditpp.setText(pp);
                                        }
                                    }

                                    if(object.has("车辆类型")){
                                        JSONObject object1=new JSONObject(object.getString("车辆类型"));
                                        String cllx=object1.getString("words");
                                        if(!TextUtils.isEmpty(cllx)){
                                            mEditcllx.setText(cllx);
                                        }
                                    }

                                    if(object.has("车辆识别代号")){
                                        JSONObject object2=new JSONObject(object.getString("车辆识别代号"));
                                        String clsbm=object2.getString("words");
                                        if(!TextUtils.isEmpty(clsbm)){
                                            editClsbm.setText(clsbm);
                                        }
                                    }
                                    if(object.has("发动机号码")){
                                        JSONObject object2=new JSONObject(object.getString("车辆识别代号"));
                                        String clsbm=object2.getString("words");
                                        JSONObject object3=new JSONObject(object.getString("发动机号码"));
                                        String fsjh=object3.getString("words");
                                        if(!TextUtils.isEmpty(fsjh) && !TextUtils.isEmpty(clsbm)){
                                            if(fsjh.length()==17 && clsbm.length()!=17){
                                                editClsbm.setText(fsjh);
                                                editFdjh.setText(clsbm);
                                            }else {
                                                editFdjh.setText(fsjh);
                                            }

                                        }
                                    }
                                    if(object.has("所有人")){
                                        JSONObject object2=new JSONObject(object.getString("所有人"));
                                        String syr=object2.getString("words");
                                        if(!TextUtils.isEmpty(syr)){
                                            editSYR.setText(syr);
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

        }
    }
    public static boolean checkPlateNumberFormat(String content) {
        String pattern = "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]{1}(([A-HJ-Z]{1}[A-HJ-NP-Z0-9]{5})|([A-HJ-Z]{1}(([DF]{1}[A-HJ-NP-Z0-9]{1}[0-9]{4})|([0-9]{5}[DF]{1})))|([A-HJ-Z]{1}[A-D0-9]{1}[0-9]{3}警)))|([0-9]{6}使)|((([沪粤川云桂鄂陕蒙藏黑辽渝]{1}A)|鲁B|闽D|蒙E|蒙H)[0-9]{4}领)|(WJ[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼·•]{1}[0-9]{4}[TDSHBXJ0-9]{1})|([VKHBSLJNGCE]{1}[A-DJ-PR-TVY]{1}[0-9]{5})";
        return Pattern.matches(pattern, content);
    }

    private void setIntentDatas() {

        final String tel = mEditTel.getText().toString().trim();
        final String cph = mEditCph.getText().toString().trim();
        final String zz = mEditZz.getText().toString().trim();
        final String dlysz = mEditDlysz.getText().toString().trim();
        final String clsbm = editClsbm.getText().toString().trim();
        final String fdjh = editFdjh.getText().toString().trim();
        final String syr=editSYR.getText().toString().trim();
        String jdcdjzsh=mEditJdcdjh.getText().toString();
        String nfcid=mEditNFCid.getText().toString().trim();
        String pp=mEditpp.getText().toString().trim();
        String cllx=mEditcllx.getText().toString();

        if (TextUtils.isEmpty(tel)) {
            Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;

        }
        if (tel.length()!=11) {
            Toast.makeText(this, "请输入正确手机号！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(cph)) {
            Toast.makeText(this, "车牌号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!checkPlateNumberFormat(cph)){
            Toast.makeText(this, "请输入正确车牌号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(zz)) {
            Toast.makeText(this, "车辆载重不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(dlysz)){
            Toast.makeText(this, "道路运输证不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(syr)) {
            Toast.makeText(this, "所有人不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(clsbm)) {
            Toast.makeText(this, "车辆识别码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (clsbm.length()!=17) {
            Toast.makeText(this, "请输入正确的17位车辆识别码！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(fdjh)){
            Toast.makeText(this, "请输入发动机号！", Toast.LENGTH_SHORT).show();
            return;

        }
        if(TextUtils.isEmpty(pp)){
            Toast.makeText(this, "车辆品牌不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(cllx)){
            Toast.makeText(this, "车型不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(uCarEntity.getCar_len())){
            Toast.makeText(this, "车长不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(uCarEntity.getCplx())){
            Toast.makeText(this, "车牌类型不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(uCarEntity.getClfl())){
            Toast.makeText(this, "车辆分类不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(uCarEntity.getTransportLicenseStartTime())){
            Toast.makeText(this, "道路运输证开始时间不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(uCarEntity.getTransportLicenseEndTime())){
            Toast.makeText(this, "道路运输证结束时间不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        uCarEntity.setLicensePlateNo(cph);
        uCarEntity.setVehicleIdentityCode(clsbm);
        uCarEntity.setEngineNumber(fdjh);
        uCarEntity.setOwner_p(syr);
        uCarEntity.setUsage_p("货运");
        uCarEntity.setLoad_p(zz);
        uCarEntity.setTransportLicenseNo(dlysz);
        uCarEntity.setVehicleRegistrationCertificateNo(jdcdjzsh);
        uCarEntity.setNfcId(nfcid);
        uCarEntity.setMobile(id);
        uCarEntity.setStatus(1);
        uCarEntity.setBrand(pp);
        uCarEntity.setStandardVehicleType(cllx);
        doSave();
        }

    private void doSave() {
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(),"addcarinfo");
        Map<String,String> map=new HashMap<>();
        String userinfo=SharePreferenceUtil.getInstance(AddCarInfoActivity.this).getUserinfo();
        try {
            JSONObject object = new JSONObject(userinfo);
            String token=object.getString("token");
            if(TextUtils.isEmpty(token)){
                Toast.makeText(this, "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            map.put("mobile",id);
            map.put("token",token);
            map.put("licensePlateNo",uCarEntity.getLicensePlateNo().toUpperCase());
            map.put("standardVehicleType",uCarEntity.getStandardVehicleType());
            map.put("vehicleIdentityCode",uCarEntity.getVehicleIdentityCode());
            map.put("brand",uCarEntity.getBrand());
            map.put("engineNumber",uCarEntity.getEngineNumber());
            map.put("owner_p",uCarEntity.getOwner_p());
            map.put("usage_p","货运");
            map.put("load_p",uCarEntity.getLoad_p());
            map.put("affiliatedEnterprise",uCarEntity.getAffiliatedEnterprise()==null?"":uCarEntity.getAffiliatedEnterprise());
            map.put("transportLicenseNo",uCarEntity.getTransportLicenseNo());
            map.put("transportLicenseExpireDate","");
            map.put("transportLicenseStartTime",uCarEntity.getTransportLicenseStartTime());
            map.put("transportLicenseEndTime",uCarEntity.getTransportLicenseEndTime());
            map.put("vehicleRegistrationCertificateNo",uCarEntity.getVehicleRegistrationCertificateNo());
            map.put("nfcId","");
            map.put("carmsg","");
            map.put("createtime","");
            map.put("isAddNewCar","0");
            map.put("cartype",uCarEntity.getClfl());
            map.put("status",uCarEntity.getStatus()+"");
            map.put("driverId1",uCarEntity.getDriverId1()+"");
            map.put("driverId2",uCarEntity.getDriverId2()+"");
            map.put("driverId3",uCarEntity.getDriverId3()+"");
            map.put("cplx",uCarEntity.getCplx());
            map.put("clfl",uCarEntity.getClfl());
            map.put("car_len",uCarEntity.getCar_len());
            RequestManager.getInstance()
                    .mServiceStore
                    .addCarInfo(map)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                        @Override
                        public void onSuccess(String msg) {
                            dialogFragment.dismissAllowingStateLoss();
                            Log.e(" save onSuccess", msg);
                            try {
                                JSONObject jsonObject=new JSONObject(msg);
                                if(jsonObject.getBoolean("success")){
                                    Toast.makeText(AddCarInfoActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                                    if(type==0){
                                        Intent intent = new Intent(AddCarInfoActivity.this, RzImgActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                    }else {
                                        finish();
                                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                                    }

                                }else {
                                    String error=jsonObject.getString("msg");
                                    Toast.makeText(AddCarInfoActivity.this, "提交失败！"+error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String msg) {
                            dialogFragment.dismissAllowingStateLoss();
                            Log.e("save onError", msg);
                            Toast.makeText(AddCarInfoActivity.this, "提交失败！"+msg, Toast.LENGTH_SHORT).show();
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
}
