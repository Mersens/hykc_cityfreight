package com.hykc.cityfreight.fragment;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.activity.AgreementActivity;
import com.hykc.cityfreight.activity.MainActivity;
import com.hykc.cityfreight.app.AlctConstants;
import com.hykc.cityfreight.entity.AlctEntity;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.entity.UWaybill;
import com.hykc.cityfreight.service.RegisterCodeTimerService;
import com.hykc.cityfreight.utils.AlctManager;
import com.hykc.cityfreight.utils.RegisterCodeTimer;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.utils.SystemUtil;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class CodeLoginFragment extends BaseFragment {
    private EditText mEditTel;
    private EditText mEditCode;
    private TextView mTextGetCode;
    private Button mBtnLogin;
    private String mobile = null;
    private String chkCode = null;
    private RelativeLayout mLayoutXY;
    private CheckBox checkBox;


    @Override
    protected int getLayoutResource() {
        return R.layout.layout_code_login;
    }

    @Override
    protected void initView(View view) {
        mEditTel=view.findViewById(R.id.editPhone);
        mEditCode=view.findViewById(R.id.edCode);
        mTextGetCode=view.findViewById(R.id.tv_getCode);
        mBtnLogin=view.findViewById(R.id.btnlogin);
        mLayoutXY=view.findViewById(R.id.layout_xy);
        checkBox=view.findViewById(R.id.checkBox);

        RegisterCodeTimerService.setHandler(mCodeHandler);
        initEvent();
    }
    private void initEvent() {
        mTextGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取验证码
                doGetCode();

            }
        });
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        mLayoutXY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheckXY();
            }
        });
    }

    private void doCheckXY() {
        Intent intentFind = new Intent(getActivity(), AgreementActivity.class);
        startActivity(intentFind);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }

    private void doGetCode() {
        final String tel=mEditTel.getText().toString();
        if(TextUtils.isEmpty(tel)){
            Toast.makeText(getActivity(), "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tel.length()!=11){
            Toast.makeText(getActivity(), "请输入正确手机号！", Toast.LENGTH_SHORT).show();
            return;
        }
        mTextGetCode.setEnabled(true);
        getActivity().startService(new Intent(getActivity(),
                RegisterCodeTimerService.class));
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
                                //mEditCode.setText(chkCode);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(getActivity(), "验证码获取失败！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("getSms onError", msg);
                    }
                }));


    }

    private void doLogin() {
        if(!checkBox.isChecked()){
            Toast.makeText(getActivity(), "请阅读和接受货运快车服务协议！", Toast.LENGTH_SHORT).show();
            return;
        }
        final String tel=mEditTel.getText().toString();
        final String code=mEditCode.getText().toString();
        if(TextUtils.isEmpty(tel)){
            Toast.makeText(getActivity(), "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(tel.length()!=11){
            Toast.makeText(getActivity(), "请输入正确手机号！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(code)){
            Toast.makeText(getActivity(), "验证码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!code.equals(chkCode)){
            Toast.makeText(getActivity(), "验证码错误！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!tel.equals(mobile)){
            Toast.makeText(getActivity(), "手机号与验证码不匹配！", Toast.LENGTH_SHORT).show();
            return;
        }
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getChildFragmentManager(),"codeloading");
        Map<String,String> map=new HashMap<>();
        map.put("mobile",tel);
        map.put("sms",code);
        map.put("mobileModel", SystemUtil.getSystemModel());
        map.put("mobileVersion",SystemUtil.getSystemVersion());
        RequestManager.getInstance()
                .mServiceStore
                .register(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("register onSuccess", "register=="+msg);
                        dialogFragment.dismissAllowingStateLoss();
                        if(!TextUtils.isEmpty(msg)){
                            try {
                                JSONObject object=new JSONObject(msg);
                                if(object.getBoolean("success")){
                                    String userInfo=object.getString("entity");
                                    SharePreferenceUtil.getInstance(getActivity()).setUserinfo(userInfo);
                                    SharePreferenceUtil.getInstance(getActivity()).setUserId(tel);
                                    JSONObject jsonObject=new JSONObject(userInfo);
                                    String identityNo=jsonObject.getString("identityNo");
                                    if(TextUtils.isEmpty(identityNo)){
                                        Toast.makeText(getActivity(), "登录成功！", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), MainActivity.class));
                                        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                        getActivity().finish();
                                        return;
                                    }
                                    Gson gson=new Gson();
                                    UDriver uDriver=gson.fromJson(userInfo,UDriver.class);
                                    JSONArray jsonArray=new JSONArray(uDriver.getAlct());
                                    JSONArray array=new JSONArray();
                                    for (int i = 0; i <jsonArray.length() ; i++) {
                                        AlctEntity entity=gson.fromJson(jsonArray.getString(i),AlctEntity.class);
                                        JSONObject obj=new JSONObject();
                                        obj.put("alctkey",entity.getAlctSecret());
                                        obj.put("alctid",entity.getAlctKey());
                                        obj.put("alctcode",entity.getAlctCode());
                                        array.put(obj);
                                    }
                                    AlctManager alctManager=AlctManager.newInstance();
                                    alctManager.setOnAlctResultListener(new MyAlctListener());
                                    Log.e("alctRegister","msg==="+array.toString()+"身份证=="+identityNo);
                                    alctManager.alctRegister(array.toString(),identityNo);

                                }else {
                                    Toast.makeText(getActivity(), "登录失败！", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(getActivity(), "登录失败！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        dialogFragment.dismissAllowingStateLoss();
                        Log.e("login onError", msg);
                        Toast.makeText(getActivity(), "登录失败！", Toast.LENGTH_SHORT).show();
                    }
                }));

    }


    @Override
    protected void initData() {

    }
    Handler mCodeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == RegisterCodeTimer.IN_RUNNING) {// 正在倒计时
                mTextGetCode.setText(msg.obj.toString());
                mTextGetCode.setEnabled(false);
            } else if (msg.what == RegisterCodeTimer.END_RUNNING) {// 完成倒计时
                mTextGetCode.setEnabled(true);
                mTextGetCode.setText("获取验证码");
            }
        }

        ;
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimerService();
        mCodeHandler.removeCallbacksAndMessages(null);
    }


    public  void stopTimerService(){
        mEditTel.setText("");
        mEditCode.setText("");
        mTextGetCode.setEnabled(true);
        mTextGetCode.setText("获取验证码");
        getActivity().stopService(new Intent(getActivity(),
                RegisterCodeTimerService.class));
    }


    class MyAlctListener implements AlctManager.OnAlctResultListener{

        @Override
        public void onSuccess(int type,UWaybill uWaybill) {
            if(type==AlctConstants.REGISTER_SUCCESS) {
                Toast.makeText(getActivity(), "登录成功！", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                getActivity().finish();
            }
        }

        @Override
        public void onError(int type, UWaybill uWaybill, String msg) {
            if(type==AlctConstants.REGISTER_ERROR) {
                Log.e("alct error", "msg==" + msg);
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                getActivity().finish();
            }
        }
    }

    public static CodeLoginFragment newInstance(){
        return new CodeLoginFragment();
    }
}
