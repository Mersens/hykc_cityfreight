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

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.OilListAdapter;
import com.hykc.cityfreight.app.Constants;
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

public class OilListActivity extends BaseActivity {
    private ImageView mImgBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout mLayoutNoMsg;
    private OilListAdapter adapter;
    private static final int pageSize = 10;
    private int pageCurrent = 1;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private boolean isLoadMoreEmpty = false;
    private boolean isFirst = true;
    private List<OilEntity> list=new ArrayList<>();
    private String cityCode="";
    private TextView mTextAddress;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationClient mLocClient;
    private String lat;
    private String lon;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_oil_list);
        init();
    }

    @Override
    public void init() {
        initView();
        initLocClient();
    }

    private void initLocClient() {
        swipeRefreshLayout.setRefreshing(true);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        int t=1*1000;
        option.setScanSpan(t);
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    private void initView() {
        mImgBack=findViewById(R.id.img_back);
        mTextAddress=findViewById(R.id.tv_address);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        adapter=new OilListAdapter(this,list,lat,lon);
        recyclerView.setAdapter(adapter);
        mLayoutNoMsg=findViewById(R.id.layout_nomsg);
        int color = getResources().getColor(R.color.actionbar_color);
        swipeRefreshLayout.setColorSchemeColors(color, color, color);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDatas();
            }
        });
        adapter.setOnItemBtnClickListener(new OilListAdapter.OnItemBtnClickListener() {
            @Override
            public void onItemClick(int pos, OilEntity entity) {
                Intent intent=new Intent(OilListActivity.this,OilDetailsActivity.class);
                intent.putExtra("entity",entity);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }

            @Override
            public void onMapClick(int pos, OilEntity entity) {
                Intent intent=new Intent(OilListActivity.this,OilMapActivity.class);
                intent.putExtra("lat",entity.getLat());
                intent.putExtra("lon",entity.getLng());
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
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
                refreshDatas();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //设置什么布局管理器,就获取什么的布局管理器
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当停止滑动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition ,角标值
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    //所有条目,数量值
                    int totalItemCount = manager.getItemCount();

                    // 判断是否滚动到底部，并且是向右滚动
                    if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                        //加载更多功能的代码
                        isLoadMore = true;
                        if (!isLoadMoreEmpty) {
                            pageCurrent = pageCurrent + 1;
                            initDatas();
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    isSlidingToLast = true;
                } else {
                    isSlidingToLast = false;
                }
            }
        });
    }
    private void refreshDatas() {
        swipeRefreshLayout.setRefreshing(true);
        isRefresh = true;
        isLoadMore = false;
        isLoadMoreEmpty = false;
        list.clear();
        pageCurrent = 1;
        initDatas();
    }

    private void initDatas() {
        Map<String,String> map=new HashMap<>();
        map.put("pageSize", pageSize + "");
        map.put("pageCurrent", pageCurrent + "");
        map.put("cityCode",cityCode);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.OIL_URL_TEST)
                .build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.getStationAndFuels(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(null!=swipeRefreshLayout && swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (isRefresh) {
                    isRefresh = false;
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
                            if (array.length() == 0) {
                                if (isFirst) {
                                    mLayoutNoMsg.setVisibility(View.VISIBLE);
                                    isFirst = false;
                                }
                                isLoadMoreEmpty = true;
                                Toast.makeText(OilListActivity.this, "数据为空！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            isFirst = false;
                            analysisJson(array);
                        }else {
                            Toast.makeText(OilListActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                            mLayoutNoMsg.setVisibility(View.VISIBLE);
                        }
                    }else {
                        Toast.makeText(OilListActivity.this, "信息查询失败！", Toast.LENGTH_SHORT).show();
                        mLayoutNoMsg.setVisibility(View.VISIBLE);

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
                if (isFirst) {
                    mLayoutNoMsg.setVisibility(View.VISIBLE);
                }
                Log.e("onFailure","onFailure=="+t.getMessage());
                Toast.makeText(OilListActivity.this, "信息查询失败！", Toast.LENGTH_SHORT).show();
                mLayoutNoMsg.setVisibility(View.VISIBLE);
            }
        });
    }

    //解析油站数据
    private void analysisJson(JSONArray array) throws JSONException {
        List<OilEntity> mList = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            Gson gson=new Gson();
            OilEntity entity =gson.fromJson(array.getString(i),OilEntity.class);
            mList.add(entity);
        }
        list.addAll(mList);
        if(adapter!=null){
            adapter.setDatas(list,lat,lon);
        }
    }

    @Override
    protected void onDestroy() {
        if (mLocClient != null) {
            mLocClient.stop();
        }
        super.onDestroy();
    }

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if(bdLocation!=null){
                Log.e("cityCode","cityCode====="+cityCode);
               cityCode=bdLocation.getCity();
               lat=bdLocation.getLatitude()+"";
               lon=bdLocation.getLongitude()+"";
               mTextAddress.setText(cityCode);
               if(!TextUtils.isEmpty(cityCode)){
                   initDatas();
                   if (mLocClient != null) {
                       mLocClient.stop();
                   }
               }
            }
        }
    }




}
