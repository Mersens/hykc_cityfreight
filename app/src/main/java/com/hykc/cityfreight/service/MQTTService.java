package com.hykc.cityfreight.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hykc.cityfreight.utils.SharePreferenceUtil;


public class MQTTService extends Service {
    private INetManager manager=null;
    @Override
    public void onCreate() {
        super.onCreate();
        startManager();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void startManager(){
        Log.e("startManager","startManager");
        if(manager==null){
            String id=SharePreferenceUtil.getInstance(this).getUserId();
            Log.e("startManager","startManager id="+id);
            manager=MqttManagerV3.getInstance(id);
            manager.startManager();
        }
    }

    @Override
    public void onDestroy() {
        Log.e("stopManager","stopManager");
        if(manager!=null)
            manager.stopManager();
        super.onDestroy();
    }

}
