package com.hykc.cityfreight.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.alct.mdp.MDPLocationCollectionManager;
import com.alct.mdp.callback.OnDownloadResultListener;
import com.alct.mdp.callback.OnResultListener;
import com.alct.mdp.model.EnterpriseIdentity;
import com.alct.mdp.model.Goods;
import com.alct.mdp.model.Image;
import com.alct.mdp.model.Invoice;
import com.alct.mdp.model.Location;
import com.alct.mdp.model.MultiIdentity;
import com.alct.mdp.response.GetInvoicesResponse;
import com.hykc.cityfreight.app.AlctConstants;
import com.hykc.cityfreight.app.App;
import com.hykc.cityfreight.entity.UWaybill;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlctManager {
    private OnAlctResultListener listener;
    private Context mContext = App.getInstance().getApplicationContext();
    private static AlctManager sAlctManager;

    private AlctManager() {
    }

    public static AlctManager newInstance() {
        return new AlctManager();
    }

    //alct注册
    public void alctRegister(String alct, String driverIdentity) {
        final MultiIdentity mMultiIdentity = new MultiIdentity();
        try {
            JSONArray array = new JSONArray(alct);
            List<EnterpriseIdentity> mList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                EnterpriseIdentity enterpriseIdentity = new EnterpriseIdentity();
                String alctid = object.getString("alctid");
                if (TextUtils.isEmpty(alctid)) {
                    continue;
                }
                enterpriseIdentity.setAppIdentity(object.getString("alctid"));
                enterpriseIdentity.setAppKey(object.getString("alctkey"));
                enterpriseIdentity.setEnterpriseCode(object.getString("alctcode"));
                mList.add(enterpriseIdentity);
            }
            //总公司
/*            EnterpriseIdentity enterpriseIdentity = new EnterpriseIdentity();
            enterpriseIdentity.setAppIdentity(Constants.APPIDENTITY);
            enterpriseIdentity.setAppKey(Constants.APPKEY);
            enterpriseIdentity.setEnterpriseCode(Constants.ENTERPRISECODE);
            mList.add(enterpriseIdentity);*/
            mMultiIdentity.setEnterpriseIdentities(mList);
            mMultiIdentity.setDriverIdentity(driverIdentity);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        MDPLocationCollectionManager.register(mContext, mMultiIdentity, new OnResultListener() {
            @Override
            public void onSuccess() {
                getInvoices(mMultiIdentity);
                SharePreferenceUtil.getInstance(mContext).setSDMsg("0");
                if (listener != null) {
                    listener.onSuccess(AlctConstants.REGISTER_SUCCESS, null);
                }

            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("login register", "register onFailure");
                SharePreferenceUtil.getInstance(mContext).setSDMsg("1");
                if (listener != null) {
                    listener.onError(AlctConstants.REGISTER_ERROR, null, s1);
                }
            }
        });
    }

    //alct提货
    public void alctPickup(final UWaybill uWaybill) {
        //需要获取坐标

        MDPLocationCollectionManager.pickup(mContext, uWaybill.getWaybillId(), uWaybill.getAlctCode(),
                getLocation(uWaybill, true), new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("doPickUp", "onSuccess----");
                        if (listener != null) {
                            listener.onSuccess(AlctConstants.PICKUP_SUCCESS, uWaybill);

                        }
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("doPickUp onFailure", "onFailure==" + s + ";" + s1);
                        if (listener != null) {
                            listener.onError(AlctConstants.PICKUP_ERROR, uWaybill, s1);
                        }

                    }
                });
    }

    //alct卸货
    public void alctUnLoad(final UWaybill uWaybill) {
        MDPLocationCollectionManager.unload(mContext, uWaybill.getWaybillId(), uWaybill.getAlctCode(),
                getLocation(uWaybill, false), new OnResultListener() {
                    public void onSuccess() {
                        Log.e("doUnLoad onSuccess", "onSuccess----");
         /*       if(listener!=null){
                    listener.onSuccess(AlctConstants.UNLOAD_SUCCESS,uWaybill);
                }*/
                        doSign(uWaybill);

                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("doUnLoad error", "onFailure----" + s + ";" + s1);
                        doSign(uWaybill);
     /*           if(listener!=null){
                    listener.onError(AlctConstants.UNLOAD_ERROR,uWaybill,s1);
                }*/
                    }
                });

    }


    //alct签收
    private void doSign(final UWaybill uWaybill) {
        MDPLocationCollectionManager.sign(mContext, uWaybill.getWaybillId(), uWaybill.getAlctCode(),
                getLocation(uWaybill, false), getGoodsList(uWaybill.getGoodsName())
                , new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("doSign onSuccess", "onSuccess----");
          /*              if(listener!=null){
                            listener.onSuccess(AlctConstants.SIGN_SUCCESS,uWaybill);
                        }*/
                        doPod(uWaybill);
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("doSign error", "onFailure----" + s + ";" + s1);
                        doPod(uWaybill);
                       /* if(listener!=null){
                            listener.onError(AlctConstants.SIGN_ERROR,uWaybill,s1);
                        }*/
                    }
                });
    }

    //alct回单
    private void doPod(final UWaybill uWaybill) {
        MDPLocationCollectionManager.pod(mContext, uWaybill.getWaybillId(), uWaybill.getAlctCode(),
                getLocation(uWaybill, false), new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("pod onSuccess", "onSuccess----");
                        if (listener != null) {
                            listener.onSuccess(AlctConstants.POD_SUCCESS, uWaybill);
                        }
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("pod onFailure", "onFailure----" + s + ";" + s1);
                        if (listener != null) {
                            listener.onError(AlctConstants.POD_ERROR, uWaybill, s1);
                        }
                    }
                });

    }

    //上传卸货照
    public void uploadUnloadImage(final UWaybill uWaybill, final String base64) {
        MDPLocationCollectionManager.uploadUnloadImage(mContext, uWaybill.getWaybillId(),
                uWaybill.getAlctCode(), getImage(base64, "unload", uWaybill), new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("uploadUnloadImage", "onSuccess");
                        if(listener!=null){
                            listener.onSuccess(AlctConstants.XHZ_SUCCESS,uWaybill);
                        }
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("uploadUnloadImage", s1);
                        if(listener!=null){
                            listener.onError(AlctConstants.XHZ_ERROR,uWaybill,s1);

                        }
                    }
                });
    }

    //上传回单照
    public void uploadPODImage(final UWaybill uWaybill, final String base64) {
        MDPLocationCollectionManager.uploadPODImage(mContext, uWaybill.getWaybillId(),
                uWaybill.getAlctCode(), getImage(base64, "pod", uWaybill), new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("uploadPODImage", "onSuccess");
                        if(listener!=null){
                            listener.onSuccess(AlctConstants.HDZ_SUCCESS,uWaybill);

                        }
                    }
                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("uploadPODImage", s1);
                        if(listener!=null){
                            listener.onError(AlctConstants.HDZ_ERROR,uWaybill,s1);


                        }
                    }
                });
    }

    //下载卸货照、回单照
    public void downloadImg(UWaybill waybill, String url, String type){
        //type 1卸货照，2回单照
        new BitmapThread(url,type,waybill).start();
    }

    private Image getImage(String buffer, String imgName, UWaybill uWaybill) {
        Image img = new Image();
        img.setBaiduLatitude(getLocation(uWaybill.getTolat()));
        img.setBaiduLongitude(getLocation(uWaybill.getTolong()));
        img.setFileExt("jpg");
        img.setFileData("data:image/jpeg;base64," + buffer);
        img.setImageTakenDate(getNowtime());
        img.setFileName(imgName);
        img.setLocation(uWaybill.getToLocation());
        String getImage = getLocation(uWaybill.getTolat()) + ";" + getLocation(uWaybill.getTolong());
        Log.e("getImage", "getImage===" + getImage);
        return img;
    }

    private double getLocation(String loc) {
        return Double.parseDouble(loc);
    }

    private void getInvoices(final MultiIdentity mMultiIdentity) {
        List<EnterpriseIdentity> mList = mMultiIdentity.getEnterpriseIdentities();
        for (EnterpriseIdentity identity : mList) {
            MDPLocationCollectionManager.getInvoices(mContext, identity.getEnterpriseCode(), 10, 1, new OnDownloadResultListener() {
                @Override
                public void onSuccess(Object o) {
                    if (o instanceof GetInvoicesResponse) {
                        GetInvoicesResponse getInvoicesResponse = (GetInvoicesResponse) o;
                        List<Invoice> list = getInvoicesResponse.getDriverInvoices();
                        for (Invoice invoice : list) {
                            String s = invoice.getInvoiceReceiverName() + ";" + invoice.getTaxAmount();
                            Log.e("Invoice==", s);
                            confirmInvoice(invoice);
                        }
                    }
                }

                @Override
                public void onFailure(String s, String s1) {
                    Log.e("Invoice onFailure", s + s1);
                }
            });
        }
    }

    private void confirmInvoice(Invoice invoice) {
        MDPLocationCollectionManager.confirmInvoice(mContext, invoice.getEnterpriseCode(), invoice.getDriverInvoiceCode(), new OnResultListener() {
            @Override
            public void onSuccess() {
                Log.e("confirmInvoice success", "onSuccess");

            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("confirmInvoiceFailure", "onFailure");
            }
        });
    }

    private List<Goods> getGoodsList(String hwmc) {
        List<Goods> list = new ArrayList<Goods>();
        Goods good = new Goods();
        good.setItemNo(1);
        good.setGoodsName(hwmc);
        good.setQuantity(1);
        good.setReceivedQuantity(1);
        good.setDamageQuantity(1);
        good.setLostQuantity(1);
        good.setUnit("车");
        list.add(good);
        return list;

    }

    private Location getLocation(UWaybill entity, boolean isPickUp) {
        double lat;
        double lon;
        if (isPickUp) {
            lat = Double.parseDouble(entity.getFromlat());
            lon = Double.parseDouble(entity.getFromlong());
        } else {
            lat = Double.parseDouble(entity.getTolat());
            lon = Double.parseDouble(entity.getTolong());
        }
        Location location = new Location();
        location.setBaiduLatitude(lat);
        location.setBaiduLongitude(lon);
        location.setLocation("");
        location.setTime(getNowtime());
        return location;

    }

    private String getNowtime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String time = sdf.format(new Date());
        return time;
    }

    class BitmapThread extends Thread {
        private String bitmapUrl;
        private String type;
        private UWaybill uWaybill;
        BitmapThread(String bitmapUrl,String type,UWaybill uWaybill) {
            this.bitmapUrl = bitmapUrl;
            this.type=type;
            this.uWaybill=uWaybill;
        }
        @Override
        public void run() {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(bitmapUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
                if(bitmap!=null){
                    if("1".equals(type)){
                        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,
                                50, baos2);
                        byte[] bytes = baos2.toByteArray();
                        String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT)
                                .replaceAll(" ","");
                        uploadUnloadImage(uWaybill,uploadBuffer);
                    }else if("2".equals(type)){
                        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,
                                50, baos2);
                        byte[] bytes = baos2.toByteArray();
                        String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT)
                                .replaceAll(" ","").replaceAll("\\+","-");;
                        uploadPODImage(uWaybill,uploadBuffer);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



    public void setOnAlctResultListener(OnAlctResultListener listener) {
        this.listener = listener;
    }

    public interface OnAlctResultListener {
        void onSuccess(int type, UWaybill uWaybill);

        void onError(int type, UWaybill uWaybill, String msg);
    }

}
