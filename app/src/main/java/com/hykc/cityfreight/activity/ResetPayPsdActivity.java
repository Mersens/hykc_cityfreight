package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.service.RegisterCodeTimerService;
import com.hykc.cityfreight.utils.IOUtils;
import com.hykc.cityfreight.utils.RegisterCodeTimer;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.hykc.cityfreight.view.LoadingDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class ResetPayPsdActivity extends BaseActivity {
    private ImageView mImgBack;
    private EditText mEditPhone;
    private EditText mEdCode;
    private EditText mEditPass;
    private TextView mGetCode;
    private Button mBtnSave;
    private String mobile = null;
    private String chkCode = null;
    private Intent mIntent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_findpsd);
        init();
    }

    @Override
    public void init() {
        RegisterCodeTimerService.setHandler(mCodeHandler);
        mIntent = new Intent(ResetPayPsdActivity.this,
                RegisterCodeTimerService.class);
        initView();
        initEvent();
    }

    private void initView() {
        mImgBack=findViewById(R.id.img_back);
        mEditPhone=(EditText)findViewById(R.id.editPhone);
        mEdCode=(EditText)findViewById(R.id.edCode);
        mEditPass=(EditText)findViewById(R.id.editPass_again);
        mGetCode=(TextView)findViewById(R.id.tv_getCode);
        mBtnSave=(Button)findViewById(R.id.btn_save);
    }

    private void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        mGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGetCode();
            }
        });
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    doSave();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void doSave() throws JSONException {
        String telnum = mEditPhone.getText().toString().trim();
        String psd = mEditPass.getText().toString().trim();
        String code = mEdCode.getText().toString().trim();
        String userinfo=SharePreferenceUtil.getInstance(ResetPayPsdActivity.this).getUserinfo();
        JSONObject object=new JSONObject(userinfo);
        String token=object.getString("token");
        if (TextUtils.isEmpty(telnum)) {
            Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;

        }
        if (!IOUtils.isMobileNO(telnum)) {
            Toast.makeText(this, "请输入正确手机号！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(code)){
            Toast.makeText(this, "验证码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!code.equals(chkCode)) {
            Toast.makeText(this, "验证码不一致！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!telnum.equals(mobile)) {
            Toast.makeText(this, "手机号码不一致！", Toast.LENGTH_SHORT).show();
            return;
        }
        final LoadingDialogFragment findPsdDialogFragment=LoadingDialogFragment.getInstance();
        findPsdDialogFragment.showF(getSupportFragmentManager(),"findPsd");
        Map<String,String> map=new HashMap<>();
        map.put("mobile",telnum);
        map.put("sms",code);
        map.put("token",token);
        RequestManager.getInstance()
                .mServiceStore
                .resetPayPsd(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        findPsdDialogFragment.dismissAllowingStateLoss();
                        Log.e("onSuccess", msg);
                        analysisJson(msg);
                    }
                    @Override
                    public void onError(String msg) {
                        findPsdDialogFragment.dismissAllowingStateLoss();
                        Log.e("onError", msg);
                    }
                }));
    }
    private void analysisJson(String msg) {
        boolean isSuccess = false;
        try {
            JSONObject mySO = new JSONObject(msg);
            isSuccess = mySO.getBoolean("success");
            if (isSuccess) {
                confirmFind("重置密码成功，请重新登录！");
            } else {
                String str = mySO.getString("message");
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {

        }
    }

    private void confirmFind(String s) {

        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(s);
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

            }

            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);


            }
        });
        dialog.show(getSupportFragmentManager(), "ExitDialogFind");
    }




    private void doGetCode() {
        String tel = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(tel)) {
            Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;

        }
        if (!IOUtils.isMobileNO(tel)) {
            Toast.makeText(this, "请输入正确手机号！", Toast.LENGTH_SHORT).show();
            return;
        }
        mGetCode.setEnabled(false);
        startService(mIntent);
        Map<String,String> map=new HashMap<>();
        map.put("mobile",tel);
        RequestManager.getInstance()
                .mServiceStore
                .getSms(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("getSms onSuccess", "getSms=="+msg);
                        if(!TextUtils.isEmpty(msg)){
                            try {
                                JSONObject object=new JSONObject(msg);
                                chkCode=object.getString("sms");
                                mobile=object.getString("mobile");
                                //mEdCode.setText(chkCode);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(ResetPayPsdActivity.this, "验证码获取失败！", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("onError", msg);
                        Toast.makeText(ResetPayPsdActivity.this, "获取验证码失败！", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    Handler mCodeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == RegisterCodeTimer.IN_RUNNING) {// 正在倒计时
                mGetCode.setText(msg.obj.toString());
                mGetCode.setEnabled(false);
            } else if (msg.what == RegisterCodeTimer.END_RUNNING) {// 完成倒计时
                mGetCode.setEnabled(true);
                mGetCode.setText("获取验证码");
            }
        }

        ;
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(mIntent);
        mCodeHandler.removeCallbacksAndMessages(null);
    }


}
