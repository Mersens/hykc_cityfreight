package com.hykc.cityfreight.utils;

import android.content.Context;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class AppInLineHelper {
    public static AppInLineHelper sHelper;
    private Context mContext;


    private AppInLineHelper(){

    }
    public static AppInLineHelper newInstance(){
        if(sHelper==null){
            sHelper=new AppInLineHelper();
        }
        return sHelper;
    }

    public void init(Context mContext){
        this.mContext=mContext;
    }
    public void appInLine(int statu){
        if(null==mContext){
            return;
        }
        String userid=SharePreferenceUtil.getInstance(mContext).getUserId();
        if(TextUtils.isEmpty(userid)){
            userid="";
        }
        Map<String,String> map=new HashMap<>();
        map.put("mobile",userid);
        map.put("mobileModel",SystemUtil.getSystemModel());
        map.put("mobileVersion",SystemUtil.getSystemVersion());
        map.put("status",statu+"");
        RequestManager.getInstance()
                .mServiceStore
                .statisticsAppInLineCount(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                    }

                    @Override
                    public void onError(String msg) {
                    }
                }));

    }








}
