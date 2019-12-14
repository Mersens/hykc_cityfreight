package com.hykc.cityfreight.service;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hykc.cityfreight.db.DBDao;
import com.hykc.cityfreight.db.DBDaoImpl;
import com.hykc.cityfreight.entity.LocEntity;
import com.hykc.cityfreight.entity.LocationEntity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocService extends Service {
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationClient mLocClient;
    private String rowid;
    private OnLocationListener listener;
    private DBDao dao;

    private void initClient() {
        dao=new DBDaoImpl(this);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        int t = (int) (2.5 * 60 * 1000);
         //int t=3000;
        option.setScanSpan(t);
        mLocClient.setLocOption(option);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("service onBind","service onBind");
        initClient();
        return new MyBind();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("service onUnbind","service onUnbind");
        stop();
        return super.onUnbind(intent);

    }

    public void start(String rowid){
        Log.e("start","start==");
        this.rowid=rowid;
        if (mLocClient != null  ) {
            myListener.setRid(rowid);
            mLocClient.start();
        }
    }

    public void stop(){
        Log.e("stop","stop==");
        if (mLocClient != null) {
            myListener.setRid(null);
            mLocClient.stop();
        }
    }

    public class MyBind extends Binder {
        public LocService getService(){
            return LocService.this;
        }
        public void startLocationService(String id){
            start(id);
        }
        public void stopLocationService(){
            stop();
        }
    }

    public void  setOnLocationListener(OnLocationListener locationListener){
        this.listener=locationListener;

    }
    public interface OnLocationListener{
        void onLocationReceive(BDLocation bdLocation);
    }

    public class MyLocationListenner implements BDLocationListener {
        private String rid;

        public void setRid(String rid) {
            this.rid = rid;
        }

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                return;
            }
            Log.e("BDLocation","BDLocation=="+bdLocation.getLatitude()+";"+bdLocation.getLongitude());
            if(listener!=null){
                listener.onLocationReceive(bdLocation);
            }
            if (dao != null && !TextUtils.isEmpty(rid)) {
                String loc = bdLocation.getAddrStr();
                String lat = bdLocation.getLatitude() + "";
                String lon = bdLocation.getLongitude() + "";
                String time = changeTtime(bdLocation.getTime());
                if (dao.findLocInfoIsExist(rid)) {
                    //存在更新
                    LocationEntity entity = dao.findLocInfoById(rid);
                    String str = entity.getLocation();
                    try {
                        if (!TextUtils.isEmpty(str)) {
                            JSONArray array = new JSONArray(str);
                            Gson gson = new Gson();
                            LocEntity locEntity = new LocEntity();
                            locEntity.setTime(time);
                            locEntity.setLongitude(lon);
                            locEntity.setLatitude(lat);
                            locEntity.setLocation(loc);
                            entity.setRowid(rid);
                            JSONObject object = new JSONObject(gson.toJson(locEntity));
                            array.put(object);
                            entity.setLocation(array.toString());
                            dao.updateLocInfo(entity, rid);
                            Log.e("updateLocInfo", "updateLocInfo===" + entity.getLocation());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    //不存在添加
                    Gson gson = new Gson();
                    LocEntity locEntity = new LocEntity();
                    locEntity.setTime(time);
                    locEntity.setLongitude(lon);
                    locEntity.setLatitude(lat);
                    locEntity.setLocation(loc);
                    LocationEntity entity = new LocationEntity();
                    JSONArray array = new JSONArray();
                    entity.setRowid(rid);
                    try {
                        JSONObject object = new JSONObject(gson.toJson(locEntity));
                        array.put(object);
                        entity.setLocation(array.toString());
                        dao.addLocInfo(entity);
                        Log.e("addLocInfo", "addLocInfo===" + entity.getLocation());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public static Date getNextDay(Date date,int d) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, d);
        date = calendar.getTime();
        return date;
    }
    public static String changeTtime(String time) {
        String str = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str = format.format(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }




}
