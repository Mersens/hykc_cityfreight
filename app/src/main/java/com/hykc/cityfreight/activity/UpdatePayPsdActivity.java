package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
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

public class UpdatePayPsdActivity extends BaseActivity {
    private ImageView mImgBack;
    private EditText mEditPhone;
    private EditText mEditPass;
    private EditText mEditNewPsd;
    private Button mBtnSave;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_updatepsd);
        init();
    }

    @Override
    public void init() {
        initView();
        initEvent();
    }

    private void initEvent() {
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

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }

    private void doSave() throws JSONException {
        String telnum = mEditPhone.getText().toString().trim();
        String psd = mEditPass.getText().toString().trim();
        String newPsd=mEditNewPsd.getText().toString().trim();
        String userinfo=SharePreferenceUtil.getInstance(UpdatePayPsdActivity.this).getUserinfo();
        JSONObject object=new JSONObject(userinfo);
        String token=object.getString("token");

        if (TextUtils.isEmpty(telnum)) {
            Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;

        }
        if (telnum.length()!=11) {
            Toast.makeText(this, "请输入正确手机号！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(psd)){
            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(psd.length()<6){
            Toast.makeText(this, "密码至少六位！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(newPsd)){
            Toast.makeText(this, "新密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(psd.length()<6){
            Toast.makeText(this, "新密码至少六位！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(token)){
            Toast.makeText(this, "token！请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        final LoadingDialogFragment updatePsdDialogFragment=LoadingDialogFragment.getInstance();
        updatePsdDialogFragment.showF(getSupportFragmentManager(),"updatePsd");
        Map<String,String> map=new HashMap<>();
        map.put("mobile",telnum);
        map.put("token",token);
        map.put("pwd",newPsd);
        map.put("oldPsd",psd);
        RequestManager.getInstance()
                .mServiceStore
                .updatePayPsd(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        updatePsdDialogFragment.dismissAllowingStateLoss();
                        Log.e("onSuccess", msg);
                        analysisJson(msg);
                    }

                    @Override
                    public void onError(String msg) {
                        updatePsdDialogFragment.dismissAllowingStateLoss();
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
                confirmFind("密码修改成功，请重新登录！");
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
                clearData();
            }

            @Override
            public void onClickOk() {
                dialog.dismiss();
                clearData();

            }
        });
        dialog.show(getSupportFragmentManager(), "ExitDialogFind");
    }
    private void clearData(){

        SharePreferenceUtil.getInstance(UpdatePayPsdActivity.this).setUserId(null);
        SharePreferenceUtil.getInstance(UpdatePayPsdActivity.this).setUserinfo(null);
        Intent intent=new Intent(UpdatePayPsdActivity.this,LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }

    private void initView() {
        mEditPhone=(EditText)findViewById(R.id.editPhone);
        mEditNewPsd=(EditText)findViewById(R.id.editPass_new);
        mEditPass=(EditText)findViewById(R.id.editPass_again);;
        mBtnSave=(Button)findViewById(R.id.btn_save);
        mImgBack=findViewById(R.id.img_back);
    }
}
