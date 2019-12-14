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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;


public class CheckRzImgActivity extends BaseActivity {
    private ImageView mImgBack;
    private TextView mTextResetRz;
    private ImageView mImgCard_Z;
    private ImageView mImgCard_F;
    private ImageView mImgJsz;
    private ImageView mImgXsz;
    private ImageView mImgDlysz;
    private ImageView mImgCyzgz;
    private ImageView mImgXSZ_Z;
    private ImageView mImgXSZ_F;
    private ImageView mImgXSZFB;
    private ImageView mImgGCZL;
    private ImageView mImgGCZLFYF;
    private ImageView mImgDLYSJYXKZ_GS;
    private String userid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_check_rz_img);
        init();
    }

    @Override
    public void init() {

        initViews();
        initEvent();
        initDatas();
    }


    private void getUserRzInfo(){
        userid=SharePreferenceUtil.getInstance(CheckRzImgActivity.this).getUserId();
        if(TextUtils.isEmpty(userid)){
            Toast.makeText(this, "id为空，请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        String userinfo=SharePreferenceUtil.getInstance(CheckRzImgActivity.this).getUserinfo();
        try {
            JSONObject object = new JSONObject(userinfo);
            String token=object.getString("token");
            if(TextUtils.isEmpty(token)){
                Toast.makeText(this, "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            Map<String,String> map=new HashMap<>();
            map.put("mobile",userid);
            map.put("token",token);
            RequestManager.getInstance()
                    .mServiceStore
                    .getDirverInfo(map)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                        @Override
                        public void onSuccess(String msg) {
                            Log.e("getDirverInfo onSuccess", msg);
                            analysisJson(msg);
                        }

                        @Override
                        public void onError(String msg) {
                            Log.e("getDirverInfo onError", msg);
                        }
                    }));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }

    private void analysisJson(String msg) {
        try {
            JSONObject object=new JSONObject(msg);
            if(object.getBoolean("success")){
                String str=object.getString("entity");
                Gson gson=new Gson();
                UDriver uDriver= gson.fromJson(str,UDriver.class);

                if(null!=uDriver){
                    downLoadImg(uDriver);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void initDatas() {
        getUserRzInfo();

    }

    private void downLoadImg(UDriver uDriver){

        if(!TextUtils.isEmpty(uDriver.getSfz_z_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getSfz_z_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgCard_Z);
        }
        if(!TextUtils.isEmpty(uDriver.getSfz_f_url())){

            Glide.with(this)
                    .load(getUrl(uDriver.getSfz_f_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgCard_F);
        }
        if(!TextUtils.isEmpty(uDriver.getJsz_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getJsz_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgJsz);
        }
        if(!TextUtils.isEmpty(uDriver.getSfz_z_url())){

            Glide.with(this)
                    .load(getUrl(uDriver.getXsz_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgXsz);
        }
        if(!TextUtils.isEmpty(uDriver.getSfz_z_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getDlysz_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgDlysz);
        }
        if(!TextUtils.isEmpty(uDriver.getCyzgz_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getCyzgz_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgCyzgz);
        }
        if(!TextUtils.isEmpty(uDriver.getXsz_z_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getXsz_z_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgXSZ_Z);
        }
        if(!TextUtils.isEmpty(uDriver.getXsz_f_url())){

            Glide.with(this)
                    .load(getUrl(uDriver.getXsz_f_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgXSZ_F);
        }
        if(!TextUtils.isEmpty(uDriver.getGczzy_url())){

            Glide.with(this)
                    .load(getUrl(uDriver.getGczzy_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgXSZFB);
        }
        if(!TextUtils.isEmpty(uDriver.getGczzy_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getGczzy_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgGCZL);
        }
        if(!TextUtils.isEmpty(uDriver.getGczfy_f_url())){
            Glide.with(this)
                    .load(getUrl(uDriver.getGczfy_f_url()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgGCZLFYF);
        }
        if(!TextUtils.isEmpty(uDriver.getDlysjyxkz_gs())){
            Glide.with(this)
                    .load(getUrl(uDriver.getDlysjyxkz_gs()))
                    .apply(RequestOptions.placeholderOf(R.mipmap.ic_no_img).
                            diskCacheStrategy(DiskCacheStrategy.NONE)
                            .circleCrop()
                            .skipMemoryCache(true)
                            .centerCrop()
                    )
                    .into(mImgDLYSJYXKZ_GS);
        }


    }

    private String getUrl(String name){
        if(name.contains("driver_picture/"+userid+"/")){
            name=name.replaceAll("driver_picture/"+userid+"/","");
        }
        Log.e("driver_picture",""+Constants.WEBSERVICE_URL+"driver_picture/"+userid+"/"+name);
        return Constants.WEBSERVICE_URL+"/driver_picture/"+userid+"/"+name;
    }

    private void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        mTextResetRz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CheckRzImgActivity.this,RzTextActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    private void initViews() {
        mImgBack=findViewById(R.id.img_back);
        mTextResetRz=findViewById(R.id.tv_add);
        mImgXSZ_Z = findViewById(R.id.img_xsz_z);
        mImgXSZ_F = findViewById(R.id.img_xsz_f);
        mImgXSZFB = findViewById(R.id.img_xszfb);
        mImgGCZL = findViewById(R.id.img_gczl);
        mImgCard_Z = findViewById(R.id.img_card_z);
        mImgCard_F = findViewById(R.id.img_card_f);
        mImgJsz = findViewById(R.id.img_jsz);
        mImgXsz = findViewById(R.id.img_xsz);
        mImgDlysz = findViewById(R.id.img_dlysz);
        mImgCyzgz = findViewById(R.id.img_cyzgz);
        mImgGCZLFYF=findViewById(R.id.img_gczfyf);
        mImgDLYSJYXKZ_GS=findViewById(R.id.img_dlysxkz_gs);
    }


}
