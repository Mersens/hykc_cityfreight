package com.hykc.cityfreight.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.hdgq.locationlib.LocationOpenApi;
import com.hdgq.locationlib.listener.OnResultListener;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.MenuPageAdapter;
import com.hykc.cityfreight.app.AlctConstants;
import com.hykc.cityfreight.app.App;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.entity.AlctEntity;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.entity.UWaybill;
import com.hykc.cityfreight.fragment.DriverHomeFragment;
import com.hykc.cityfreight.fragment.MyInfoFragment;
import com.hykc.cityfreight.fragment.OrderFragment;
import com.hykc.cityfreight.fragment.QuestionDialogFragment;
import com.hykc.cityfreight.fragment.SourceFragment;
import com.hykc.cityfreight.processprotection.PlayerMusicService;
import com.hykc.cityfreight.service.ConnectService;
import com.hykc.cityfreight.service.MQTTService;
import com.hykc.cityfreight.utils.APKVersionCodeUtils;
import com.hykc.cityfreight.utils.AlctManager;
import com.hykc.cityfreight.utils.AppInLineHelper;
import com.hykc.cityfreight.utils.LocationOpenApiHelper;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.ServiceUtils;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.AcceptAgreDialog;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.hykc.cityfreight.view.NoScrollViewPager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private final int SDK_PERMISSION_REQUEST = 127;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> mTitles = new ArrayList<>(Arrays.asList("运单",
            "货源",
            "司机之家",
            "我的"));
    private NoScrollViewPager mViewPager;
    private RelativeLayout mLayoutYD;
    private RelativeLayout mLayoutHY;
    private RelativeLayout mLayoutSJ;
    private RelativeLayout mLayoutWD;

    private ImageView mImgYD;
    private ImageView mImgHY;
    private ImageView mImgSJ;
    private ImageView mImgWD;

    private TextView mTextYD;
    private TextView mTextHY;
    private TextView mTextSJ;
    private TextView mTextWD;
    private int index;
    private int selectColor;
    private int unSelectColor;
    private MenuPageAdapter pageAdapter;
    private TextView mTextTitle;
    private RelativeLayout mLayoutToolbar;
    private DownloadBuilder builder;
    public String lat;
    public String lon;
    private RelativeLayout mLayoutTips;
    private ImageView mImgCloseTips;
    private TextView mTextRightTitle;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;


    public void setLocation(String lat, String lon) {
        this.lat = lat;
        this.lon = lon;
    }

    private ScreenBroadcastReceiver screenBroadcastReceiver = new ScreenBroadcastReceiver();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public void init() {
        selectColor = getResources().getColor(R.color.actionbar_color);
        unSelectColor = getResources().getColor(R.color.color_text_gray);
        initView();
        getPersimmions();
        initEvent();
        initDatas();
        initOpenApi(this);
        registerBroadcastReceiver(this);
        AppInLineHelper.newInstance().init(this);
        AppInLineHelper.newInstance().appInLine(1);

    }


    private void initOpenApi(MainActivity context) {
        LocationOpenApi.init(context,
                Constants.LOCATION_API_APPID,
                Constants.LOCATION_APPSECURITY,
                Constants.LOCATION_API_ENTERPRISESENDERCODE,
                Constants.LOCATION_API_ENVIRONMENT,
                new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("initOpenApi", "initOpenApi onSuccess");
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("initOpenApi", "initOpenApi onFailure=" + s + ";" + s1);
                    }
                });

    }

    private void initQuestion() {
        final QuestionDialogFragment questionDialogFragment = QuestionDialogFragment.getInstance();
        questionDialogFragment.show(getSupportFragmentManager(), "questionDialog");
        questionDialogFragment.setOnQuestionSelectListener(new QuestionDialogFragment.OnQuestionSelectListener() {
            @Override
            public void onQuestionSelect(boolean isTrue) {
                questionDialogFragment.dismiss();
                if (isTrue) {
                    Toast.makeText(MainActivity.this, "回答正确！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "回答错误！", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void initDatas() {
        getUserInfo();
        checkVerson();
        checkToken();
    }

    private void checkToken() {
        String id = SharePreferenceUtil.getInstance(this).getUserId();
        String userinfo = SharePreferenceUtil.getInstance(this).getUserinfo();
        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(userinfo)) {
            return;
        }
        try {
            JSONObject object = new JSONObject(userinfo);
            String token = object.getString("token");
            if (TextUtils.isEmpty(token)) {
                Toast.makeText(this, "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, String> map = new HashMap<>();
            map.put("mobile", id);
            map.put("token", token);
            RequestManager.getInstance()
                    .mServiceStore
                    .checkTokenTimeout(map)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                        @Override
                        public void onSuccess(String msg) {
                            Log.e("checkTokenTimeout", msg);
                            boolean isSuccess = false;
                            try {
                                JSONObject jsonObject = new JSONObject(msg);
                                isSuccess = jsonObject.getBoolean("success");
                                if (!isSuccess) {
                                    String error = jsonObject.getString("msg");
                                    if (error.contains("token")) {
                                        confirmTokenTips("token已过期,请重新登录！");
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String msg) {
                            Log.e("checkTokenTimeout", msg);
                        }
                    }));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkVerson() {
        RequestManager.getInstance()
                .mServiceStore
                .checkVerson()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("checkVerson", msg);
                        if (!TextUtils.isEmpty(msg)) {
                            try {
                                JSONObject jsonObject = new JSONObject(msg);
                                if (jsonObject.getBoolean("success")) {
                                    JSONObject object = new JSONObject(jsonObject.getString("entity"));
                                    String content = object.getString("content");
                                    String isMust = object.getString("isMustUpdate");
                                    boolean isMustUpdate = false;
                                    if ("Yes".equals(isMust)) {
                                        isMustUpdate = true;
                                    }
                                    String url = object.getString("url");
                                    Log.e("app url====", url);
                                    double strverson = object.getDouble("versionCode");
                                    float apkCode = APKVersionCodeUtils.getVersionCode(MainActivity.this);
                                    if (strverson > apkCode) {
                                        showVersonView(content, url, isMustUpdate);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(String msg) {
                    }
                }));

    }

    private void showVersonView(final String content, final String url, boolean isNeed) {
        builder = AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(Constants.WEBSERVICE_URL + "app/checkVersion")
                .request(new RequestVersionListener() {

                    @Override
                    public UIData onRequestVersionSuccess(String result) {
                        return crateUIData(content, url);
                    }

                    @Override
                    public void onRequestVersionFailure(String message) {

                    }
                });

        if (isNeed) {
            builder.setForceUpdateListener(new ForceUpdateListener() {
                @Override
                public void onShouldForceUpdate() {
                    forceUpdate();
                }
            });
        }
        builder.setForceRedownload(true);

        builder.setDownloadAPKPath(Constants.UPDATEAPP_LOCATION);
        builder.excuteMission(MainActivity.this);

    }

    private void forceUpdate() {
        App.getInstance().exit();
    }


    private UIData crateUIData(String content, String url) {
        UIData uiData = UIData.create();
        uiData.setTitle("新版本更新");
        uiData.setDownloadUrl(url);
        uiData.setContent(content);
        return uiData;
    }

    private void getUserInfo() {
        String userid = SharePreferenceUtil.getInstance(this).getUserId();
        if (!TextUtils.isEmpty(userid)) {
            getRzInfo(userid);
        } else {
            Toast.makeText(this, "手机号为空，请重新登录！", Toast.LENGTH_SHORT).show();
        }
    }

    private void getRzInfo(final String id) {
        String userinfo = SharePreferenceUtil.getInstance(this).getUserinfo();
        try {
            JSONObject object = new JSONObject(userinfo);
            String token = object.getString("token");
            if (TextUtils.isEmpty(token)) {
                Toast.makeText(this, "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String, String> map = new HashMap<>();
            map.put("mobile", id);
            map.put("token", token);
            RequestManager.getInstance()
                    .mServiceStore
                    .checkDriverAuthentication(map)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                        @Override
                        public void onSuccess(String msg) {
                            Log.e("getRzInfo onSuccess", msg);
                            boolean isSuccess = false;
                            try {
                                JSONObject jsonObject = new JSONObject(msg);
                                isSuccess = jsonObject.getBoolean("success");
                                if (isSuccess) {
                                    String str = jsonObject.getString("entity");
                                    Gson gson = new Gson();
                                    UDriver uDriver = gson.fromJson(str, UDriver.class);
                                    int statu = uDriver.getStatus();
                                    if (statu == 0) {
                                        confirmRZTips("用户未认证,请认证！");
                                    } else if (statu == 2) {
                                        confirmRZTips("用户未认证未通过！");
                                    } else if (statu == 3) {
                                        //未审核
                                    } else if (statu == 1) {
                                        //审核通过，查询银行卡信息
                                        getMyCardInfo(id);
                                        initQuestion();
                                    }
                                    SharePreferenceUtil.getInstance(MainActivity.this).setUserinfo(str);
                                    startLocalService();
                                    JSONArray jsonArray = new JSONArray(uDriver.getAlct());
                                    JSONArray array = new JSONArray();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        AlctEntity entity = gson.fromJson(jsonArray.getString(i), AlctEntity.class);
                                        JSONObject obj = new JSONObject();
                                        obj.put("alctkey", entity.getAlctSecret());
                                        obj.put("alctid", entity.getAlctKey());
                                        obj.put("alctcode", entity.getAlctCode());
                                        array.put(obj);
                                    }
                                    AlctManager alctManager = AlctManager.newInstance();
                                    alctManager.setOnAlctResultListener(new MyAlctListener());
                                    Log.e("alctRegister", "msg===" + array.toString() + "身份证==" + uDriver.getIdentityNo());
                                    alctManager.alctRegister(array.toString(), uDriver.getIdentityNo());

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String msg) {
                        }
                    }));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getMyCardInfo(final String userid) {

        Map<String, String> map = new HashMap<>();
        map.put("mobile", userid);
        RequestManager.getInstance()
                .mServiceStore
                .selectCardInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("onSuccess getMyCardInfo", "====" + msg);
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getBoolean("success")) {
                                analysisJson(object.getString("entity"));
                            } else {
                                String error = object.getString("msg");
                                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);

                    }
                }));
    }

    private void analysisJson(String entity) {
        try {
            JSONArray array = new JSONArray(entity);
            if (array.length() == 0) {
                confirmBankTips("请绑定银行卡！");
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class MyAlctListener implements AlctManager.OnAlctResultListener {

        @Override
        public void onSuccess(int type, UWaybill uWaybill) {
            Log.e("MainAlctArr", "type==" + type);
            if (type == AlctConstants.REGISTER_SUCCESS) {
                Log.e("alct error", "msg==安联登录成功");

            }

        }

        @Override
        public void onError(int type, UWaybill uWaybill, String msg) {
            Log.e("MainAlctArr", "type==" + type);
            if (type == AlctConstants.REGISTER_ERROR) {
                Log.e("alct error", "msg==" + msg);
                confirmTips("登录错误:" + msg + "\n" + "联系客服！！！");
            }
        }
    }

    private void startLocalService() {
        String userInfo = SharePreferenceUtil.getInstance(this).getUserinfo();
        if (!TextUtils.isEmpty(userInfo)) {
            Gson gson = new Gson();
            UDriver uDriver = gson.fromJson(userInfo, UDriver.class);
            int statu = uDriver.getStatus();
            if (statu == 1) {
                Log.e("ServiceUtils", ServiceUtils.isServiceWork(getApplicationContext(), "com.hykc.cityfreight.service.MQTTService") + "");
                if (!ServiceUtils.isServiceWork(getApplicationContext(), "com.hykc.cityfreight.service.MQTTService")) {
                    Intent mqttService = new Intent(getApplicationContext(), MQTTService.class);
                    startService(mqttService);
                }
                if (!ServiceUtils.isServiceWork(getApplicationContext(), "com.hykc.cityfreight.service.ConnectService")) {
                    Intent intent = new Intent(getApplicationContext(), ConnectService.class);
                    intent.putExtra("id", uDriver.getMobile());
                    startService(intent);
                }

            }
        }
    }

    private void initView() {
        mLayoutYD = findViewById(R.id.layout_yd);
        mLayoutHY = findViewById(R.id.layout_hy);
        mLayoutSJ = findViewById(R.id.layout_sj);
        mLayoutWD = findViewById(R.id.layout_wd);

        mImgYD = findViewById(R.id.img_yd);
        mImgHY = findViewById(R.id.img_hy);
        mImgSJ = findViewById(R.id.img_sj);
        mImgWD = findViewById(R.id.img_wd);

        mTextYD = findViewById(R.id.tv_yd);
        mTextHY = findViewById(R.id.tv_hy);
        mTextSJ = findViewById(R.id.tv_sj);
        mTextWD = findViewById(R.id.tv_wd);
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setScroll(false);
        mLayoutToolbar = findViewById(R.id.layout_toolbar);
        mTextTitle = findViewById(R.id.tv_title);
        mTextTitle.setText("运单");

        mLayoutTips = findViewById(R.id.layout_tips);
        mImgCloseTips = findViewById(R.id.img_close_tips);
        mTextRightTitle = findViewById(R.id.tv_right_title);

    }

    private void initEvent() {
        mLayoutYD.setOnClickListener(this);
        mLayoutHY.setOnClickListener(this);
        mLayoutSJ.setOnClickListener(this);
        mLayoutWD.setOnClickListener(this);
        fragments.add(OrderFragment.newInstance());
        fragments.add(SourceFragment.newInstance());
        fragments.add(DriverHomeFragment.newInstance());
        fragments.add(MyInfoFragment.newInstance());
        mViewPager.setOffscreenPageLimit(mTitles.size());
        pageAdapter = new MenuPageAdapter(getSupportFragmentManager()
                , fragments);
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setCurrentItem(0);

        mLayoutTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TipsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


            }
        });

        mImgCloseTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutTips.setVisibility(View.GONE);
            }
        });
        mTextRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermiss();
            }
        });

    }

    private void checkPermiss() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            } else {
                callPhone();
            }
        } else {
            callPhone();
        }
    }


    private void callPhone() {
        confirmUserPhone("确定拨打救援电话？");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callPhone();
            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void confirmUserPhone(String msg) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getSupportFragmentManager(), "CallDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onClickOk() {
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + Constants.YJJY_NUM);
                intent.setData(data);
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_yd:
                index = 0;
                setTabColor(index);
                break;
            case R.id.layout_hy:
                index = 1;
                setTabColor(index);
                break;
            case R.id.layout_sj:
                index = 2;
                setTabColor(index);
                break;
            case R.id.layout_wd:
                index = 3;
                setTabColor(index);
                break;
        }
        mViewPager.setCurrentItem(index);
    }

    private void setTabColor(int index) {
        resetTab();
        switch (index) {
            case 0:

                mTextTitle.setText(mTitles.get(0));
                mLayoutToolbar.setVisibility(View.VISIBLE);
                mTextYD.setTextColor(selectColor);
                mImgYD.setImageResource(R.mipmap.icon_yd_select);
                break;
            case 1:
                mTextTitle.setText(mTitles.get(1));
                mLayoutToolbar.setVisibility(View.VISIBLE);
                mTextHY.setTextColor(selectColor);
                mImgHY.setImageResource(R.mipmap.icon_hy_select);
                break;
            case 2:
                mTextTitle.setText(mTitles.get(2));
                mLayoutToolbar.setVisibility(View.VISIBLE);
                mTextSJ.setTextColor(selectColor);
                mImgSJ.setImageResource(R.mipmap.icon_sj_select);
                break;
            case 3:
                mLayoutToolbar.setVisibility(View.GONE);
                mTextWD.setTextColor(selectColor);
                mImgWD.setImageResource(R.mipmap.icon_wd_select);
                break;
        }
    }

    private void resetTab() {
        mTextYD.setTextColor(unSelectColor);
        mImgYD.setImageResource(R.mipmap.icon_yd_normal);
        mTextHY.setTextColor(unSelectColor);
        mImgHY.setImageResource(R.mipmap.icon_hy_normal);
        mTextSJ.setTextColor(unSelectColor);
        mImgSJ.setImageResource(R.mipmap.icon_sj_normal);
        mTextWD.setTextColor(unSelectColor);
        mImgWD.setImageResource(R.mipmap.icon_wd_normal);
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(screenBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        confirmExit("确定退出应用?");
    }

    private void confirmExit(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.showF(getSupportFragmentManager(), "ExitDialog");

        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialog.dismissAllowingStateLoss();
                Intent intent = new Intent(getApplicationContext(), PlayerMusicService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
                AppInLineHelper.newInstance().appInLine(2);
                App.getInstance().exit();
            }
        });
    }

    private void confirmRZTips(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.showF(getSupportFragmentManager(), "rzDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialog.dismissAllowingStateLoss();
                Intent intent = new Intent(MainActivity.this, RzTextActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

    }

    private void confirmBankTips(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.showF(getSupportFragmentManager(), "rzDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialog.dismissAllowingStateLoss();
                Intent intent = new Intent(MainActivity.this, MyCardActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    private void confirmTokenTips(String msg) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.showF(getSupportFragmentManager(), "confirmTokenTips");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismissAllowingStateLoss();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                dialog.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialog.dismissAllowingStateLoss();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });


    }

    private void confirmTips(String msg) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.showF(getSupportFragmentManager(), "confirmTipssd");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {

                dialog.dismissAllowingStateLoss();
            }
        });


    }

    private void registerBroadcastReceiver(Context context) {

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        context.registerReceiver(screenBroadcastReceiver, filter);
    }


    class ScreenBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                Intent pintent = new Intent(getApplicationContext(), PlayerMusicService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(pintent);
                } else {
                    startService(pintent);
                }
            }
        }
    }

}
