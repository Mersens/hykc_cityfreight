package com.hykc.cityfreight.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.FuelsListAdapter;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.entity.FuelsEntity;
import com.hykc.cityfreight.entity.OilEntity;
import com.hykc.cityfreight.service.ServiceStore;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OilDetailsActivity extends BaseActivity {
    private ImageView mImgBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FuelsListAdapter adapter;
    List<FuelsEntity> mList=new ArrayList<>();
    private OilEntity entity=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_oil_details);

        init();
    }
    @Override
    public void init() {
        entity= (OilEntity) getIntent().getSerializableExtra("entity");
        initView();
        initDatas();
    }

    private void initView() {
        mImgBack=findViewById(R.id.img_back);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        adapter=new FuelsListAdapter(this,mList);
        recyclerView.setAdapter(adapter);
        int color = getResources().getColor(R.color.actionbar_color);
        swipeRefreshLayout.setColorSchemeColors(color, color, color);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                initDatas();
            }
        });
    }

    private void initDatas() {
        if(entity==null){
            Toast.makeText(this, "油站信息为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(null!=swipeRefreshLayout && swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(true);
        }
        Map<String,String> map=new HashMap<>();
        map.put("pid",entity.getStationId());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.OIL_URL_TEST)
                .build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.getFuelsByPid(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(null!=swipeRefreshLayout && swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }

                ResponseBody body=response.body();
                String str= null;
                try {
                    if(null!=body){
                        str = body.string();
                        JSONObject object=new JSONObject(str);
                        boolean success=object.getBoolean("success");
                        if(success){
                            JSONArray array = new JSONArray(object.getString("results"));
                            analysisJson(array);
                        }else {
                            Toast.makeText(OilDetailsActivity.this, "查询失败", Toast.LENGTH_SHORT).show();

                        }
                    }else {
                        Toast.makeText(OilDetailsActivity.this, "信息查询失败！", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("getStationAndFuels","getStationAndFuels=="+str);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(null!=swipeRefreshLayout && swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }

                Log.e("onFailure","onFailure=="+t.getMessage());
                Toast.makeText(OilDetailsActivity.this, "信息查询失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //解析油站数据
    private void analysisJson(JSONArray array) throws JSONException {
        List<FuelsEntity> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Gson gson=new Gson();
            FuelsEntity entity =gson.fromJson(array.getString(i),FuelsEntity.class);
            list.add(entity);
        }
        mList.addAll(list);
        if(adapter!=null){
            adapter.setDatas(mList);
        }
    }




}
