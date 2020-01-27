package com.hykc.cityfreight.utils;

import android.content.Context;

import com.hdgq.locationlib.LocationOpenApi;
import com.hdgq.locationlib.entity.ShippingNoteInfo;
import com.hdgq.locationlib.listener.OnResultListener;
import com.hykc.cityfreight.activity.MainActivity;
import com.hykc.cityfreight.app.Constants;

import java.security.PublicKey;

public class LocationOpenApiHelper {

    private static LocationOpenApiHelper sLocationOpenApiHelper;

    private LocationOpenApiHelper() {

    }

    public static LocationOpenApiHelper newInstance() {
        if (sLocationOpenApiHelper == null) {
            sLocationOpenApiHelper = new LocationOpenApiHelper();
        }
        return sLocationOpenApiHelper;
    }

    /**
     * LocationOpenApiHelper 初始化
     * @param context
     * @param listener
     */
    public void init(Context context, OnAipResultListener listener) {
        LocationOpenApi.init(context,
                Constants.LOCATION_API_APPID,
                Constants.LOCATION_APPSECURITY,
                Constants.LOCATION_API_ENTERPRISESENDERCODE,
                Constants.LOCATION_API_ENVIRONMENT,
                new OnResultListener() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                if (listener != null) {
                    listener.onFailure(s, s1);
                }
            }
        });

    }


    /**
     * LocationOpenApi开始收集定位
     * @param context
     * @param shippingNoteInfos
     * @param listener
     */
    public void onApiStart(MainActivity context,
                           ShippingNoteInfo[] shippingNoteInfos,
                           OnAipResultListener listener){
        LocationOpenApi.start(context, shippingNoteInfos, new OnResultListener() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                if (listener != null) {
                    listener.onFailure(s, s1);
                }
            }
        });
    }

    /**
     * LocationOpenApi停止收集定位
     * @param context
     * @param shippingNoteInfos
     * @param listener
     */
    public void onApiStop(Context context,
                           ShippingNoteInfo[] shippingNoteInfos,
                           OnAipResultListener listener){
        LocationOpenApi.stop(context, shippingNoteInfos, new OnResultListener() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess();
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                if (listener != null) {
                    listener.onFailure(s, s1);
                }
            }
        });
    }


    public interface OnAipResultListener {
        void onSuccess();
        void onFailure(String errorCode, String errorMsg);

    }


}
