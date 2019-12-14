package com.hykc.cityfreight.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class PSLocationService extends Service {
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationClient mLocClient;
    private OnLocationListener listener;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("service onBind","PSLocationService onBind");
        initClient();
        return new MyBind();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("service onUnbind","service onUnbind");
        stop();
        return super.onUnbind(intent);

    }
    private void initClient() {
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        int t=10*1000;
        option.setScanSpan(t);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }


    public class MyBind extends Binder {
        public PSLocationService getService(){
            return PSLocationService.this;
        }
        public void startLocationService(){
            start();
        }
        public void stopLocationService(){
            stop();
        }
        public boolean isStarted(){
            return isLocClientStarted();
        }
    }
    public boolean isLocClientStarted(){
        boolean isStarted=false;
        if (mLocClient != null  ) {
            isStarted= mLocClient.isStarted();
        }
        return isStarted;
    }
    public void start(){
        Log.e("start","start==");
        if (mLocClient != null  ) {
            mLocClient.start();
        }
    }

    public void stop(){
        Log.e("stop","stop==");
        if (mLocClient != null) {
            mLocClient.stop();
        }
    }
    public void  setOnLocationListener(OnLocationListener locationListener){
        this.listener=locationListener;

    }
    public interface OnLocationListener{
        void onLocationReceive(BDLocation bdLocation);
    }

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation!=null){
                if(listener!=null){
                    listener.onLocationReceive(bdLocation);
                }
            }
        }
    }



}
