package com.hykc.cityfreight.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONException;
import org.json.JSONObject;

public class ConnectService extends Service {
    private String userid;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationClient mLocClient;
    private MqttManagerV3 mqttManagerV3;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       if(intent!=null && intent.hasExtra("id")){
           userid=intent.getStringExtra("id");
           Log.e("onStartCommand,","userid="+userid);
       }
       if(!TextUtils.isEmpty(userid)){
           mqttManagerV3=MqttManagerV3.getInstance(userid);
           initLocayion();
       }
        return START_STICKY;
    }

    private void initLocayion() {
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        int t =  (3 * 60 * 1000);
        //int t=3000;
        option.setScanSpan(t);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation!=null){
                Log.e("Connect locaion",bdLocation.getLatitude()+";"+bdLocation.getLongitude());
                if(mqttManagerV3!=null && mqttManagerV3.isConnected()){
                    JSONObject object=new JSONObject();
                    JSONObject params=new JSONObject();
                    String lat = bdLocation.getLatitude() + "";
                    String lon = bdLocation.getLongitude() + "";
                    try {
                        object.put("lat",lat);
                        object.put("lon",lon);
                        object.put("mobile",userid);
                        mqttManagerV3.sendWithThread("driverHeart",object.toString(),"");
                        Log.e("sendWithThread",object.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        if(mLocClient!=null){
            mLocClient.stop();
        }
        super.onDestroy();
    }
}
