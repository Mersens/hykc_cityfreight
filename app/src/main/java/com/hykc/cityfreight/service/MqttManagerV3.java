package com.hykc.cityfreight.service;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.hykc.cityfreight.app.AlctConstants;
import com.hykc.cityfreight.app.App;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.app.MQConstants;
import com.hykc.cityfreight.entity.EventEntity;
import com.hykc.cityfreight.entity.UWaybill;
import com.hykc.cityfreight.utils.AlctManager;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.RxBus;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MqttManagerV3 implements INetManager {

    String userId = null;
    private boolean isWorking = false;
    private String[] topics = null;
    private final static int[] QOS_VALUES = {0, 0};
    private MqttClient mqttClient = null;

    private static MqttManagerV3 instance = null;
    private AlctManager alctManager = AlctManager.newInstance();

    private MemoryPersistence persistence = new MemoryPersistence();

    public static String msgCenter = "VirtualTopic.pm.Local";

    public static MqttManagerV3 getInstance(String id) {
        if (instance == null)
            instance = new MqttManagerV3(id);

        return instance;
    }

    private MqttManagerV3(String id) {
        this.userId = id;
        topics = new String[2];
        topics[0] = userId + "-MQ";
        topics[1] = userId + "-MQ";
        alctManager.setOnAlctResultListener(new MyAlctListener());
    }

    @Override
    public void setRelayServer(String server) {
        // TODO Auto-generated method stub
    }

    @Override
    public void startManager() {

        // TODO Auto-generated method stub
        isWorking = true;

        new Thread() {
            public void run() {
                while (isWorking) {
                    if (mqttClient != null && mqttClient.isConnected()) {
                        try {
                            Thread.sleep(10000);
                            Log.e("mqttClient","1111111111");
                        } catch (Exception ex) {
                        }
                        Log.e("mqttClient","222222222222");
                        continue;
                    }
                    Log.e("mqttClient","33333333333333");
                    notifyConnectionEvent(INetManager.CONNECTING);
                    boolean isCreated = createClient();
                    if (isCreated && mqttClient != null) {
                        Log.e("mqttClient","555555555555");
                        try {
                            mqttClient.subscribe(topics, QOS_VALUES);
                            notifyConnectionEvent(INetManager.CONNECTED);
                        } catch (MqttException e) {
                            // TODO Auto-generated catch block
                            if (mqttClient.isConnected()) {
                                try {
                                    mqttClient.disconnect();
                                } catch (MqttException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (Exception ex) {
                    }
                }
            }
        }.start();
    }

    public void subscribe(String topic) throws MqttException {
        if (mqttClient != null) {
            mqttClient.subscribe(topic);
        }
    }

    public void unsubscribe(String topic) throws MqttException {
        if (mqttClient != null) {
            mqttClient.unsubscribe(topic);
        }
    }


    @Override
    public void notifyConnectionEvent(String evt) {
        Log.e("ConnectionEvent", "ConnectionEvent==>>" + evt);

    }

    @Override
    public void send(String msg, String to) {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {

                mqttClient.publish("checkUser", msg.getBytes(), 0, false);
                Log.e("mqttClient", msg);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


    public void sendConnectLoc(final String topic,String msg, String to) {
        if (mqttClient != null && mqttClient.isConnected()) {
            try {

                mqttClient.publish(topic, msg.getBytes(), 0, false);
                Log.e("mqttClient",topic+";"+ msg);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendWithThread(final String topic, final String msg, final String to) {
        new Thread() {
            public void run() {
                try {
                    sendConnectLoc(topic,msg,to);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }


    @Override
    public void sendWithThread(final String msg, final String to) {
        new Thread() {
            public void run() {
                try {
                    send(msg, to);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void stopManager() {
        // TODO Auto-generated method stub
        isWorking = false;
        if (mqttClient != null) {
            try {
                mqttClient.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mqttClient = null;
            }
        }
        Context context=App.getInstance().getApplicationContext();
        Intent intent=new Intent(context,ConnectService.class);
        intent.putExtra("id",userId);
        context.stopService(intent);
    }

    private boolean createClient() {
        try {
            Log.e("mqttClient","4444444444444444444");
            String user = userId;
            String password = Constants.MQTT_PWD;
            String broker = Constants.MQTT_URL;
            String clientid = MqttClient.generateClientId();
            mqttClient = new MqttClient(broker, userId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(user);
            connOpts.setPassword(password.toCharArray());
            // 设置超时时间，单位：秒
            connOpts.setConnectionTimeout(10);
            // 心跳包发送间隔，单位：秒
            connOpts.setKeepAliveInterval(20);
            System.out.println("Connecting to broker: " + broker);
            mqttClient.connect(connOpts);
            mqttClient.setCallback(new MqttCallbackHandler());

        } catch (MqttException e) {
            e.printStackTrace();
            mqttClient = null;
            return false;
        }

        return true;
    }

    class MqttCallbackHandler implements MqttCallback {
        @Override
        public void connectionLost(Throwable arg0) {
            Log.e("connectionLost", "connectionLost");
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken arg0) {
            Log.e("deliveryComplete", "deliveryComplete");

        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void messageArrived(String topic, MqttMessage msg)
                throws Exception {
            String strmsg = new String(msg.getPayload(), "utf-8");
            Log.e("messageArrived", "deliveryComplete" + topic + ";" + strmsg.toString());
            EventEntity eventEntity = new EventEntity();
            eventEntity.type = MQConstants.MQ_MSG_KEY;
            eventEntity.value = strmsg;
            RxBus.getInstance().send(eventEntity);
            JSONObject object = new JSONObject(strmsg);
            if (MQConstants.MQ_MSG_JC_KEY.equals(object.getString("type"))) {
                sendWithThread(strmsg, "");
            } else if (MQConstants.MQ_MSG_TH_KEY.equals(object.getString("type"))) {
                //提货
                UWaybill waybill = new UWaybill();
                waybill.setWaybillId(object.getString("waybillId"));
                waybill.setAlctCode(object.getString("alctCode"));
                waybill.setFromlat(object.getString("fromlat"));
                waybill.setFromlong(object.getString("fromlong"));
                waybill.setTolat(object.getString("tolat"));
                waybill.setTolong(object.getString("tolong"));
                alctManager.alctPickup(waybill);
            }else if(MQConstants.MQ_MSG_XH_KEY.equals(object.getString("type"))){
                //卸货
                UWaybill waybill = new UWaybill();
                waybill.setWaybillId(object.getString("waybillId"));
                waybill.setAlctCode(object.getString("alctCode"));
                waybill.setFromlat(object.getString("fromlat"));
                waybill.setFromlong(object.getString("fromlong"));
                waybill.setTolat(object.getString("tolat"));
                waybill.setTolong(object.getString("tolong"));
                alctManager.alctUnLoad(waybill);
            }else if(MQConstants.MQ_MSG_SCZP_KEY.equals(object.getString("type"))) {
                UWaybill waybill = new UWaybill();
                waybill.setWaybillId(object.getString("waybillId"));
                waybill.setAlctCode(object.getString("alctCode"));
                waybill.setFromlat(object.getString("fromlat"));
                waybill.setFromlong(object.getString("fromlong"));
                waybill.setTolat(object.getString("tolat"));
                waybill.setTolong(object.getString("tolong"));
                if(object.has("unloadURL")){
                    String unloadURL=object.getString("unloadURL");//卸货照地址
                    alctManager.downloadImg(waybill,unloadURL,"1");
                }
                if(object.has("receiptURL")){
                    String receiptURL=object.getString("receiptURL");//回单照地址
                    alctManager.downloadImg(waybill,receiptURL,"2");
                }
            }

        }
    }

    class MyAlctListener implements AlctManager.OnAlctResultListener {
        @Override
        public void onSuccess(int type,UWaybill uWaybill) {
            Log.e("AlctListener", "type==" + type);
            int updateType=-1;
            if (type == AlctConstants.REGISTER_SUCCESS) {
                return;
            }
            try {
                JSONObject object = new JSONObject();
                if (type == AlctConstants.PICKUP_SUCCESS) {
                    object.put("type", type + "");
                    object.put("success","true");
                    uWaybill.setPickupMsg("提货成功！");
                    updateType=1;
                    object.put("msg","提货成功！");
                    object.put("waybillId",uWaybill.getWaybillId());
                    sendWithThread(object.toString(), "");
                } else if (type == AlctConstants.POD_SUCCESS) {
                    object.put("type", type + "");
                    object.put("success","true");
                    uWaybill.setUnloadMsg("卸货成功！");
                    updateType=2;
                    object.put("msg","卸货成功！");
                    object.put("waybillId",uWaybill.getWaybillId());
                    sendWithThread(object.toString(), "");
                }else if (type == AlctConstants.XHZ_SUCCESS) {
                    object.put("type", type + "");
                    object.put("success","true");
                    object.put("msg","卸货照上传成功！");
                    object.put("waybillId",uWaybill.getWaybillId());
                    sendWithThread(object.toString(), "");
                    updateType=3;
                    uWaybill.setAlctUnloadMsg("卸货照上传成功！");
                    upAlctImgMsg(uWaybill,"卸货照上传成功！");
                } else if (type == AlctConstants.HDZ_SUCCESS) {
                    object.put("type", type + "");
                    object.put("success","true");
                    object.put("msg","回单照上传成功！");
                    object.put("waybillId",uWaybill.getWaybillId());
                    sendWithThread(object.toString(), "");
                }
                if(updateType!=-1){
                    updateOrderAlctMsg(updateType,uWaybill);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(int type, UWaybill uWaybill, String msg) {
            Log.e("AlctListener", "type==" + type + ";msg==" + msg);
            int updateType=-1;
            if (type == AlctConstants.REGISTER_ERROR) {
                return;
            }
            if(type==AlctConstants.PICKUP_ERROR){
                updateType=1;
                uWaybill.setPickupMsg(msg);
            }else if(type==AlctConstants.UNLOAD_ERROR || type==AlctConstants.SIGN_ERROR
                    ||type==AlctConstants.POD_ERROR){
                updateType=2;
                uWaybill.setUnloadMsg(msg);
            }
            if(updateType!=-1){
                updateOrderAlctMsg(updateType,uWaybill);
            }
            try {
                JSONObject object = new JSONObject();
                object.put("type", type + "");
                object.put("success","false");
                object.put("msg",msg);
                object.put("waybillId",uWaybill.getWaybillId());
                sendWithThread(object.toString(), "");
            } catch (JSONException e) {
            e.printStackTrace();
        }
        }
    }

    private void updateOrderAlctMsg( int updateType,UWaybill uWaybill){
        Map<String, String> map = new HashMap<>();
        map.put("id",uWaybill.getId()+"");
        map.put("updateType",updateType+"");
        map.put("pickupMsg",uWaybill.getPickupMsg());
        map.put("unloadMsg",uWaybill.getUnloadMsg());
        RequestManager.getInstance()
                .mServiceStore
                .updateOrderAlctMsg(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("updateOrderAlctMsg", msg);

                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("updateOrderAlctMsg", msg);
                    }
                }));

    }

    private void upAlctImgMsg(UWaybill waybill, String msg){
        Map<String,String> map=new HashMap<>();
        map.put("id",waybill.getId()+"");
        map.put("upimageMsg",msg);
        RequestManager.getInstance()
                .mServiceStore
                .upAlctImgMsg(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("upAlctImgMsg","upLoadOrderImg==="+msg);
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e("upAlctImgMsg", msg);
                    }
                }));
    }



    public boolean isConnected(){
        return mqttClient != null && mqttClient.isConnected();
    }
}
