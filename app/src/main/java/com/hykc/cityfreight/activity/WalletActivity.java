package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.BalanceDetailAdapter;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.entity.ZDriver;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.hykc.cityfreight.view.ScrollListView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class WalletActivity extends BaseActivity {
    private ImageView mImgBack;
    private ScrollListView listView;
    private TextView mTextMoney;
    private TextView mTextTips;
    private TextView mTextBankCard;
    private TextView mTextTx;
    private int statu=0;
    private String userid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_wallet);
        init();
    }

    @Override
    public void init() {
        initView();
        initEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initData() {
        userid= SharePreferenceUtil.getInstance(this).getUserId();
        getMoneyInfo(userid);
        getCashWithdrawal();

    }

    private void getCashWithdrawal(){
        Map<String,String> map=new HashMap<>();
        String userInfo=SharePreferenceUtil.getInstance(this).getUserinfo();
        if(!TextUtils.isEmpty(userInfo)){
            Gson gson=new Gson();
            UDriver uDriver= gson.fromJson(userInfo,UDriver.class);
            long id=uDriver.getId();
            map.put("driverId",id+"");
        }
        RequestManager.getInstance()
                .mServiceStore
                .selectCashWithdrawal(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("applyCashWithdrawal", msg);
                        boolean isSuccess = false;
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            isSuccess = jsonObject.getBoolean("success");
                            if (isSuccess) {
                                String str=jsonObject.getString("entity");
                                if(!"null".equals(str)){
                                    List<ZDriver> list=new ArrayList<>();
                                    Gson gson=new Gson();
                                    JSONArray array=new JSONArray(str);
                                    for (int i = 0; i <array.length() ; i++) {
                                        String string=array.getString(i);
                                        ZDriver zDriver=gson.fromJson(string,ZDriver.class);
                                        list.add(zDriver);
                                    }
                                    if(list.size()==0){
                                        return;
                                    }
                                    List<ZDriver> mList=new ArrayList<>();
                                    if(list.size()>5){
                                        for(int i=0;i<5;i++){
                                            mList.add(list.get(i));
                                        }
                                    }else {
                                        mList.addAll(list);
                                    }
                                    BalanceDetailAdapter adapter = new BalanceDetailAdapter(
                                            WalletActivity.this, mList);
                                    listView.setAdapter(adapter);
                                }else {

                                }
                            }else {
                                Toast.makeText(WalletActivity.this, "查询失败",
                                        Toast.LENGTH_SHORT).show();
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
    }

    private void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        mTextBankCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statu==0){
                    showDialog("用户未认证,请认证！",1);
                }else if(statu==1){
                    //已通过
                    Intent intent=new Intent(WalletActivity.this,MyCardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                }else if(statu==2){
                    showDialog("认证未通过，请重新认证！",2);
                }else if(statu==3){
                    //未审核
                    showDialog("认证信息正在审核中！",3);

                }

            }
        });

        mTextTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statu==0){
                    showDialog("用户未认证,请认证！",1);
                }else if(statu==1){
                    //已通过
                    Intent intent=new Intent(WalletActivity.this,TXInputMoneyActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }else if(statu==2){
                    showDialog("认证未通过，请重新认证！",2);
                }else if(statu==3){
                    //未审核
                    showDialog("认证信息正在审核中！",3);
                }
            }
        });
    }

    public void showDialog(String msg,final int type){
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getSupportFragmentManager(), "RZDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                if(type==0||type==2){
                    Intent intent = new Intent(WalletActivity.this, RzTextActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    dialog.dismiss();
                }else {
                    dialog.dismiss();
                }

            }
        });
    }



    private void initView() {
        mImgBack=findViewById(R.id.img_back);
        listView=findViewById(R.id.listView);
        mTextMoney=findViewById(R.id.tv_money);
        mTextTips=findViewById(R.id.tv_tips);
        mTextBankCard=findViewById(R.id.tv_bankcard);
        mTextTx=findViewById(R.id.tv_tx);
        userid= SharePreferenceUtil.getInstance(this).getUserId();
        if(TextUtils.isEmpty(userid)){
            Toast.makeText(this, "用户id为空", Toast.LENGTH_SHORT).show();
            return;
        }
        String userInfo=SharePreferenceUtil.getInstance(this).getUserinfo();
        if(!TextUtils.isEmpty(userInfo)){
            Gson gson=new Gson();
            UDriver uDriver= gson.fromJson(userInfo,UDriver.class);
            statu=uDriver.getStatus();
        }else {
            Toast.makeText(this, "用户信息为空", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void getMoneyInfo(final String mobile) {
        Map<String,String> map=new HashMap<>();
        map.put("mobile",mobile);
        RequestManager.getInstance()
                .mServiceStore
                .getDriverMoneyInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                        Log.e(" getMoneyInfo onSuccess", "====" + msg);
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getBoolean("success")) {
                                String entity=object.getString("entity");
                                Gson gson=new Gson();
                                UDriver driver=gson.fromJson(entity,UDriver.class);
                                mTextMoney.setText(driver.getMoney()+"");
                            } else {
                                String errorMsg = object.getString("msg");
                                Toast.makeText(WalletActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {

                        }
                    }

                    @Override
                    public void onError(String msg) {

                        Log.e("onError", "====" + msg);
                        Toast.makeText(WalletActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

}
