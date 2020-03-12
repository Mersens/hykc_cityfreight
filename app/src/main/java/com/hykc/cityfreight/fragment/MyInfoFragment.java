package com.hykc.cityfreight.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.activity.AboutActivity;
import com.hykc.cityfreight.activity.ChatActivity;
import com.hykc.cityfreight.activity.CheckRzTextActivity;
import com.hykc.cityfreight.activity.ContactActivity;
import com.hykc.cityfreight.activity.LoginActivity;
import com.hykc.cityfreight.activity.MainActivity;
import com.hykc.cityfreight.activity.MyCarInfoActivity;
import com.hykc.cityfreight.activity.MyCardActivity;
import com.hykc.cityfreight.activity.MySuggListActivity;
import com.hykc.cityfreight.activity.OilActivity;
import com.hykc.cityfreight.activity.RzTextActivity;
import com.hykc.cityfreight.activity.SettingActivity;
import com.hykc.cityfreight.activity.WalletActivity;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.entity.AlctEntity;
import com.hykc.cityfreight.entity.EventEntity;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.service.ConnectService;
import com.hykc.cityfreight.service.MQTTService;
import com.hykc.cityfreight.utils.AppInLineHelper;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.RxBus;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.google.gson.Gson;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyInfoFragment extends BaseFragment implements View.OnClickListener {
    private static final String USER_DEFULT_PSD="123456";
    private TextView mTextStatu;
    private RelativeLayout mLayoutExit;
    private RelativeLayout mLayoutQB;
    private RelativeLayout mLayoutYJ;
    private RelativeLayout mLayoutYK;
    private RelativeLayout mLayoutXX;
    private RelativeLayout mLayoutCL;
    private RelativeLayout mLayoutSZ;
    private RelativeLayout mLayoutKF;
    private RelativeLayout mLayoutWM;
    private RelativeLayout mLayoutZXJD;
    private ImageView imgUser;
    private TextView mTextUserName;
    int statu=0;
    private boolean isKeFuLoginSuccess=false;
    private CompositeDisposable mCompositeDisposable;
    @Override
    protected int getLayoutResource() {
        return R.layout.layout_myinfo;
    }

    @Override
    protected void initView(View view) {
        mTextStatu=view.findViewById(R.id.tv_status);
        mLayoutExit=view.findViewById(R.id.layout_exit);
        mLayoutQB=view.findViewById(R.id.layout_wdqb);
        mLayoutYJ=view.findViewById(R.id.layout_yjfk);
        mLayoutYK=view.findViewById(R.id.layout_dzyk);
        mLayoutXX=view.findViewById(R.id.layout_wdxx);
        mLayoutCL=view.findViewById(R.id.layout_wdcl);
        mLayoutSZ=view.findViewById(R.id.layout_xtsz);
        mLayoutKF=view.findViewById(R.id.layout_wdkf);
        mLayoutWM=view.findViewById(R.id.layout_gywm);
        mLayoutZXJD=view.findViewById(R.id.layout_zxjd);
        mTextUserName=view.findViewById(R.id.tv_user_name);
        imgUser=view.findViewById(R.id.img_user);
        mLayoutExit.setOnClickListener(this);
        mLayoutQB.setOnClickListener(this);
        mLayoutYJ.setOnClickListener(this);
        mLayoutYK.setOnClickListener(this);
        mLayoutXX.setOnClickListener(this);
        mLayoutCL.setOnClickListener(this);
        mLayoutSZ.setOnClickListener(this);
        mLayoutKF.setOnClickListener(this);
        mLayoutWM.setOnClickListener(this);
        imgUser.setOnClickListener(this);
        mLayoutZXJD.setOnClickListener(this);
        checkUser();
        initBus();
    }
    private void initBus() {
        mCompositeDisposable = new CompositeDisposable();
        //监听订阅事件
        Disposable d = RxBus.getInstance()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof EventEntity) {
                            EventEntity e = (EventEntity) o;
                            String type = e.type;
                            String v = e.value;
                            if (type.equals("1002")) {
                                getUserInfo();
                            }
                        }
                    }
                });
        //subscription交给compositeSubscription进行管理，防止内存溢出
        mCompositeDisposable.add(d);

    }
    @Override
    protected void initData() {
        String userInfo=SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        if(!TextUtils.isEmpty(userInfo)){
            Gson gson=new Gson();
            UDriver uDriver= gson.fromJson(userInfo,UDriver.class);
            statu=uDriver.getStatus();
            if(!TextUtils.isEmpty(uDriver.getDriverName())){
                mTextUserName.setText(uDriver.getDriverName());
            }

        }else {
            Toast.makeText(getActivity(), "用户信息为空", Toast.LENGTH_SHORT).show();
            return;
        }
        getUserInfo();
        checkUser();
    }


    private void getUserInfo() {
        String userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        if (!TextUtils.isEmpty(userid)) {
            getRzInfo( userid);
        }else {
            Toast.makeText(getActivity(), "手机号为空，请重新登录！", Toast.LENGTH_SHORT).show();
        }
    }

    private void getRzInfo(final String id) {
        String userinfo=SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        if(TextUtils.isEmpty(userinfo)){
            Toast.makeText(getActivity(), "用户信息为空", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject object = new JSONObject(userinfo);
            String token=object.getString("token");
            if(TextUtils.isEmpty(token)){
                Toast.makeText(getActivity(), "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String,String> map=new HashMap<>();
            map.put("mobile",id);
            map.put("token",token);
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
                                    Gson gson=new Gson();
                                    UDriver uDriver= gson.fromJson(str,UDriver.class);
                                    if(!TextUtils.isEmpty(uDriver.getDriverName())){
                                        mTextUserName.setText(uDriver.getDriverName());
                                    }
                                    int statu=uDriver.getStatus();
                                    if(statu==0){
                                        mTextStatu.setText("未认证");
                                    }else if(statu==2){
                                        mTextStatu.setText("未通过");
                                    }else if(statu==3){
                                        //未审核
                                        mTextStatu.setText("未审核");
                                    }else if (statu==1){
                                        mTextStatu.setText("已通过");
                                        getMyCardInfo(id);
                                    }
                                    SharePreferenceUtil.getInstance(getActivity()).setUserinfo(str);
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

        Map<String,String> map=new HashMap<>();
        map.put("mobile",userid);
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
                            JSONObject object=new JSONObject(msg);
                            if(object.getBoolean("success")){
                                analysisJson(object.getString("entity"));
                            }else {
                                String error=object.getString("msg");
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);

                    }
                }));
    }

    private void analysisJson(String entity) {
        try {
            JSONArray array = new JSONArray(entity);
            if(array.length()==0){
                confirmBankTips("请绑定银行卡！");
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void confirmBankTips(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.showF(getChildFragmentManager(), "rzDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialog.dismissAllowingStateLoss();
                Intent intent = new Intent(getActivity(), MyCardActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

    }
    private void confirmExit(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getChildFragmentManager(), "ExitDialogTips");

        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                dialog.dismiss();
                AppInLineHelper.newInstance().init(getActivity());
                AppInLineHelper.newInstance().appInLine(2);

                Intent mqttService = new Intent(getActivity().getApplicationContext(), MQTTService.class);
                getActivity().stopService(mqttService);
                Intent connectService = new Intent(getActivity().getApplicationContext(), ConnectService.class);
                getActivity().stopService(connectService);

                SharePreferenceUtil.getInstance(getActivity()).setUserId("");
                SharePreferenceUtil.getInstance(getActivity()).setUserinfo("");


                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                getActivity().finish();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

    public static  MyInfoFragment newInstance(){
        MyInfoFragment fragment=new MyInfoFragment();
        return fragment;
    }

    @Override
    public void onClick(View v) {
        Intent mIntent=null;
        switch (v.getId()){
            case R.id.layout_exit:
                confirmExit("确定退出登录？");
                break;
            case R.id.layout_wdqb:
                mIntent=new Intent(getActivity(),WalletActivity.class);
                break;
            case R.id.layout_yjfk://意见反馈
                mIntent=new Intent(getActivity(),MySuggListActivity.class);
                break;
            case R.id.layout_dzyk:
                mIntent=new Intent(getActivity(),OilActivity.class);
                break;
            case R.id.layout_wdxx:
                Toast.makeText(getActivity(), "该功能暂未开放！", Toast.LENGTH_SHORT).show();
               // mIntent=new Intent(getActivity(),MyMsgActivity.class);
                break;
            case R.id.layout_wdcl:
                mIntent=new Intent(getActivity(),MyCarInfoActivity.class);
                break;
            case R.id.layout_xtsz:
                //系统设置
                mIntent=new Intent(getActivity(),SettingActivity.class);

                break;
            case R.id.layout_wdkf:
                //我的客服
                mIntent=new Intent(getActivity(),ContactActivity.class);
                break;
            case R.id.layout_gywm:
                //关于我们
                mIntent=new Intent(getActivity(),AboutActivity.class);
                break;
            case R.id.img_user:
                checkRzMsg();
                break;
            case R.id.layout_zxjd:
                if(ChatClient.getInstance().isLoggedInBefore()){
                    //已经登录，可以直接进入会话界面
                    mIntent = new IntentBuilder(getActivity())
                            .setShowUserNick(false)
                            .setTargetClass(ChatActivity.class)
                            .setServiceIMNumber(Constants.ServiceIMNumber).build();

                }else{
                    //未登录，需要登录后，再进入会话界面
                    String user=SharePreferenceUtil.getInstance(getActivity()).getUserId();
                    if(TextUtils.isEmpty(user)){
                        Toast.makeText(getActivity(), "用户信息为空，请重新登录", Toast.LENGTH_SHORT).show();
                    }
                    login(user,USER_DEFULT_PSD);

                }

                break;
        }
        if(mIntent!=null){
            startActivity(mIntent);
            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }
    }



    private void checkUser() {
        String user=SharePreferenceUtil.getInstance(getActivity()).getUserId();
        if(TextUtils.isEmpty(user)){
            Toast.makeText(getActivity(), "用户信息为空，请重新登录", Toast.LENGTH_SHORT).show();
        }
        register(user);
    }

    private void register(final String name){
        ChatClient.getInstance().register(name, USER_DEFULT_PSD, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int errorCode, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });

    }

    private void login(final String name,final String psd) {
        ChatClient.getInstance().login(name, psd, new Callback(){

            @Override
            public void onSuccess() {
               Intent mIntent = new IntentBuilder(getActivity())
                        .setShowUserNick(false)
                        .setTargetClass(ChatActivity.class)
                        .setServiceIMNumber(Constants.ServiceIMNumber).build();
                startActivity(mIntent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }

            @Override
            public void onError(int code, String error) {
                Log.e("login", "error: "+error);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });

    }

    private void checkRzMsg() {
        String msg = mTextStatu.getText().toString();
        if ("未认证".equals(msg) || "未通过".equals(msg)) {

            Intent intent = new Intent(getActivity(), RzTextActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }else if("已通过".equals(msg)){
            //跳转到查看个人认证信息页面
            //跳转到查看页面
            Intent intent=new Intent(getActivity(),CheckRzTextActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

        }else {
            //未审核
            showWSHView("你已提交实名认证信息，正在等待后台审核！");
        }

    }


    private void showWSHView(String msg){
        final ExitDialogFragment exitDialogFragment=ExitDialogFragment.getInstance(msg);
        exitDialogFragment.showF(getChildFragmentManager(),"showWSHView");
        exitDialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                exitDialogFragment.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                exitDialogFragment.dismissAllowingStateLoss();
            }
        });



    }

}
