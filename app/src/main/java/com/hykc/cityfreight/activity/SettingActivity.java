package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.app.App;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.utils.APKVersionCodeUtils;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.hykc.cityfreight.view.LoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class SettingActivity extends BaseActivity {
    private static final long SPLASH_DELAY_SECONDS = 3;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    private RelativeLayout mLayoutXgmm;
    private RelativeLayout mLayoutQchc;
    private RelativeLayout mLayoutUpdate;
    private RelativeLayout mLayoutExit;
    private RelativeLayout mLayoutUpdatePayPwd;
    private RelativeLayout mLayoutResetPayPwd;
    private LoadingView mLoadView;
    private DownloadBuilder builder;
    private ImageView mImgBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setting);
        init();
    }

    @Override
    public void init() {
        mLayoutXgmm=findViewById(R.id.layout_xgmm);
        mLayoutQchc=findViewById(R.id.layout_qchc);
        mLayoutUpdate=findViewById(R.id.layout_update);
        mLayoutExit=findViewById(R.id.layout_exit);
        mLoadView=findViewById(R.id.loadView);
        mLayoutUpdatePayPwd=findViewById(R.id.layout_updatepaypwd);
        mLayoutResetPayPwd=findViewById(R.id.layout_resetpaypwd);
        mImgBack=findViewById(R.id.img_back);
        initEvent();
    }

    private void initEvent() {
        mLayoutXgmm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findIntent=new Intent(SettingActivity.this, UpdatePsdActivity.class);
                startActivity(findIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        mLayoutQchc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutQchc.setClickable(false);
                mLoadView.setVisibility(View.VISIBLE);
                doInterval();
            }
        });
        mLayoutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVerson();
            }
        });
        mLayoutExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmExit("确定退出登录?");

            }
        });
        mLayoutUpdatePayPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findIntent=new Intent(SettingActivity.this, UpdatePayPsdActivity.class);
                startActivity(findIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        mLayoutResetPayPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findIntent=new Intent(SettingActivity.this, ResetPayPsdActivity.class);
                startActivity(findIntent);
               overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

    }

    private void confirmExit(String msg) {

        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getSupportFragmentManager(), "ExitDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                dialog.dismiss();
                SharePreferenceUtil.getInstance(SettingActivity.this).setUserId(null);
                SharePreferenceUtil.getInstance(SettingActivity.this).setUserinfo(null);
                App.getInstance().exit();

            }
        });


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
                        if(!TextUtils.isEmpty(msg)){
                            try {
                                JSONObject jsonObject = new JSONObject(msg);
                                if(jsonObject.getBoolean("success")){
                                    JSONObject object=new JSONObject(jsonObject.getString("entity"));
                                    String content = object.getString("content");
                                    boolean isMustUpdate = false;
                                    String isMust=object.getString("isMustUpdate");
                                    if("Yes".equals(isMust)){
                                        isMustUpdate=true;
                                    }else {
                                        isMustUpdate=false;
                                    }

                                    String url = object.getString("url");
                                    double strverson = object.getDouble("versionCode");
                                    float apkCode = APKVersionCodeUtils.getVersionCode(SettingActivity.this);
                                    if (strverson > apkCode) {
                                        showVersonView(content, url, isMustUpdate);
                                    }else {
                                        showTipsView("已是最新版本！");
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

    private void showTipsView(String msg){
        final ExitDialogFragment dialogFragment=ExitDialogFragment.getInstance(msg);
        dialogFragment.showF(getSupportFragmentManager(),"tipsView");
        dialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialogFragment.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialogFragment.dismissAllowingStateLoss();
            }
        });

    }
    private void showVersonView(final String content, final String url, boolean isNeed) {
        builder = AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(Constants.WEBSERVICE_URL+"app/checkVersion")
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
        builder.excuteMission(SettingActivity.this);

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
    private void doInterval() {
        Disposable mIntervalDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(SPLASH_DELAY_SECONDS + 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(@NonNull Long aLong) throws Exception {
                        return SPLASH_DELAY_SECONDS - aLong;
                    }
                }).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (aLong == 0) {
                            mLoadView.setVisibility(View.GONE);
                            mLayoutQchc.setClickable(true);
                        }
                    }
                });
        mCompositeDisposable.add(mIntervalDisposable);
    }
    @Override
    public void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();

    }
}
