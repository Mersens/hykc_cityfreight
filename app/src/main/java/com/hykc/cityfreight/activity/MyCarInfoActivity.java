package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.MyCarInfoAdapter;
import com.hykc.cityfreight.entity.UCarEntity;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MyCarInfoActivity extends BaseActivity {
    private ImageView  mImgBack;
    private TextView mTextAdd;
    private RelativeLayout mLayoutNoMsg;
    private ListView mListView;
    List<UCarEntity> list=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_carinfo);
        init();
    }

    @Override
    public void init() {
        initViews();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        initDatas();
    }

    private void initViews() {
        mTextAdd=findViewById(R.id.tv_add);
        mLayoutNoMsg=findViewById(R.id.layout_nomsg);
        mListView=findViewById(R.id.listView);
        mImgBack=findViewById(R.id.img_back);
    }

    private void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        mTextAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size()>=3){
                    Toast.makeText(MyCarInfoActivity.this, "最多只能添加三辆车！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MyCarInfoActivity.this, AddMyCarInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UCarEntity entity=list.get(position);
                Intent intent = new Intent(MyCarInfoActivity.this, MyCarInfoDetailsActivity.class);
                intent.putExtra("entity",entity);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    private void initDatas() {
        String userId=SharePreferenceUtil.getInstance(this).getUserId();
        if(TextUtils.isEmpty(userId)){
            Toast.makeText(this, "司机手机号为空，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        getCarInfo(userId);
    }


    private void getCarInfo( String userid) {
        final LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
        loadingDialogFragment.showF(getSupportFragmentManager(),"getCarInfoView");
        Map<String, String> map = new HashMap<>();
        map.put("mobile", userid);
        RequestManager.getInstance()
                .mServiceStore
                .selectAllCarInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Log.e("getCarInfo onSuccess", msg);
                        analysisJson(msg);
                    }

                    @Override
                    public void onError(String msg) {
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Log.e("getCarInfo onError", msg);
                    }
                }));

    }

    private void analysisJson(String msg) {
        try {
            JSONObject object=new JSONObject(msg);
            if(object.getBoolean("success")){
                String str=object.getString("entity");
                if(!TextUtils.isEmpty(str)){
                    JSONArray array=new JSONArray(str);
                    if (array.length()>0){
                        mLayoutNoMsg.setVisibility(View.GONE);
                    }else {
                        mLayoutNoMsg.setVisibility(View.VISIBLE);
                    }
                    for (int i = 0; i <array.length() ; i++) {
                        String arr= array.getString(i);
                        Gson gson=new Gson();
                        UCarEntity uCarEntity=gson.fromJson(arr,UCarEntity.class);
                        this.list.add(uCarEntity);
                    }
                    setDatas(this.list);
                }else {
                    mLayoutNoMsg.setVisibility(View.VISIBLE);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDatas(List<UCarEntity> list){
        MyCarInfoAdapter adapter=new MyCarInfoAdapter(this,list);
        mListView.setAdapter(adapter);
    }



}
