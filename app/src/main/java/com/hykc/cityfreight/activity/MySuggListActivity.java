package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.MySuggAdapter;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.entity.SuggestionEntity;
import com.hykc.cityfreight.service.ServiceStore;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
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

public class MySuggListActivity extends BaseActivity {
    private ImageView mImgBack;
    private TextView mTextAdd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout mLayoutNoMsg;
    private List<SuggestionEntity> mList=new ArrayList<>();
    private MySuggAdapter adapter=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_sugg);
        init();
    }

    @Override
    public void init() {
        mImgBack=findViewById(R.id.img_back);
        mTextAdd=findViewById(R.id.tv_add);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        int color = getResources().getColor(R.color.actionbar_color);
        swipeRefreshLayout.setColorSchemeColors(color, color, color);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        mLayoutNoMsg = findViewById(R.id.layout_nomsg);
        adapter=new MySuggAdapter(this,mList);
        recyclerView.setAdapter(adapter);
        initEvent();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initDatas();
    }

    private void initDatas() {
        String userid=SharePreferenceUtil.getInstance(this).getUserId();
        if(TextUtils.isEmpty(userid)){
            Toast.makeText(this, "手机号为空，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if(swipeRefreshLayout!=null && !swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(true);
        }
        mList.clear();
        Map<String,String> map=new HashMap<>();
        map.put("mobile",userid);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.WEBSERVICE_URL)
                .build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.selectSuggByMobile(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(swipeRefreshLayout!=null && swipeRefreshLayout.isRefreshing()){
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
                            analysisJson(object.getString("entity"));
                        }else {
                            String string=object.getString("message");
                            Toast.makeText(MySuggListActivity.this, string, Toast.LENGTH_SHORT).show();
                            mLayoutNoMsg.setVisibility(View.VISIBLE);
                        }
                    }else {
                        Toast.makeText(MySuggListActivity.this, "信息查询失败！", Toast.LENGTH_SHORT).show();
                        mLayoutNoMsg.setVisibility(View.VISIBLE);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("getStationAndFuels","getStationAndFuels=="+str);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(swipeRefreshLayout!=null && swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                Log.e("onFailure","onFailure=="+t.getMessage());
                Toast.makeText(MySuggListActivity.this, "信息查询失败！", Toast.LENGTH_SHORT).show();
                mLayoutNoMsg.setVisibility(View.VISIBLE);
            }
        });
    }

    private void analysisJson(String result) throws JSONException {
        if(TextUtils.isEmpty(result)){
            mLayoutNoMsg.setVisibility(View.VISIBLE);
            return;
        }
        JSONArray array=new JSONArray(result);
        if(array.length()==0){
            mLayoutNoMsg.setVisibility(View.VISIBLE);
            return;
        }
        mLayoutNoMsg.setVisibility(View.GONE);
        Gson gson=new Gson();
        for (int i = 0; i <array.length() ; i++) {
            String string=array.getString(i);
            SuggestionEntity entity=gson.fromJson(string,SuggestionEntity.class);
            mList.add(entity);
        }
        if (null!=adapter)
            adapter.setDatas(mList);
    }

    private void initEvent() {
        adapter.setOnItemBtnClickListener(new MySuggAdapter.OnItemBtnClickListener() {
            @Override
            public void onItemClick(int pos, SuggestionEntity entity) {
                Intent intent=new Intent(MySuggListActivity.this,SuggestionDetailsActivity.class);
                intent.putExtra("entity",entity);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                initDatas();
            }
        });
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
                Intent mIntent=new Intent(MySuggListActivity.this,SuggestionActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }


}
