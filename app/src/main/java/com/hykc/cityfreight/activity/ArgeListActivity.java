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
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.ArgeListAdapter;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.entity.BestSignAgreementEntity;
import com.hykc.cityfreight.service.ServiceStore;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ArgeListActivity extends BaseActivity {
    private ImageView mImgBack;
    private static final int REQUEST_CODE=1001;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<BestSignAgreementEntity> list = new ArrayList<>();
    private RecyclerView recyclerView;
    private RelativeLayout mLayoutNoMsg;
    private RelativeLayout mLayoutLoading;
    private static final int pageSize = 10;
    private int pageCurrent = 1;
    ArgeListAdapter argeListAdapter;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private boolean isLoadMoreEmpty = false;
    private boolean isFirst=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_arge_list);
        init();
    }

    @Override
    public void init() {
        initView();
        initEvent();
        initData();

    }
    private void refreshDatas() {
        swipeRefreshLayout.setRefreshing(true);
        isRefresh = true;
        isLoadMore = false;
        isLoadMoreEmpty = false;
        list.clear();
        pageCurrent = 1;
        initData();
    }

    private void initView() {
        mImgBack=findViewById(R.id.img_back);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        int color = getResources().getColor(R.color.actionbar_color);
        swipeRefreshLayout.setColorSchemeColors(color, color, color);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        argeListAdapter=new ArgeListAdapter(this,list);
        recyclerView.setAdapter(argeListAdapter);
        mLayoutNoMsg = findViewById(R.id.layout_nomsg);
        mLayoutLoading = findViewById(R.id.layout_loading);
        mLayoutLoading.setVisibility(View.GONE);
    }

    private void getArgeUrl(String userid, String waybillId) {
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(),"getArgeUrlView");
        Map<String,String> map=new HashMap<>();
        map.put("account",userid);
        map.put("rowid",waybillId);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.checkAgreByRowid(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialogFragment.dismissAllowingStateLoss();
                ResponseBody body=response.body();
                String str= null;
                try {
                    if(null!=body){
                        str = body.string();
                        JSONObject object=new JSONObject(str);
                        boolean success=object.getBoolean("success");
                        if(success){
                           String argeUrl=object.getString("message");
                            Intent intent = new Intent(ArgeListActivity.this, CheckAgreActivity.class);
                            intent.putExtra("url",argeUrl);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("getArgeUrl","getArgeUrl=="+str);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogFragment.dismissAllowingStateLoss();
                Log.e("onFailure","onFailure=="+t.getMessage());
            }
        });

    }

    private void initEvent() {
        argeListAdapter.setOnItemBtnClickListener(new ArgeListAdapter.OnItemBtnClickListener() {
            @Override
            public void onDetailsClick(int pos, BestSignAgreementEntity entity) {
                String id=SharePreferenceUtil.getInstance(ArgeListActivity.this).getUserId();
                String uid=entity.getRowid();
                getArgeUrl(id,uid);

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
                            Toast.makeText(ArgeListActivity.this, "正在加载第" + pageCurrent + "页", Toast.LENGTH_SHORT).show();
                            initData();
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

    private void initData() {
        String userid=SharePreferenceUtil.getInstance(this).getUserId();
        if(!TextUtils.isEmpty(userid)){
            getArgeInfo(userid);
        }else {
            Toast.makeText(this, "用户id为空，请重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    private void getArgeInfo(String userid) {
        if(swipeRefreshLayout!=null && !swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(true);
        }
        Map<String, String> map = new HashMap<>();
        map.put("account", userid);
        map.put("pageSize", pageSize + "");
        map.put("pageCurrent", pageCurrent + "");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.selectAllArgeByAccount(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mLayoutLoading.setVisibility(View.GONE);
                if(swipeRefreshLayout!=null && swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                if (isRefresh) {
                    swipeRefreshLayout.setRefreshing(false);
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
                            String entity = object.getString("entity");
                            JSONArray array = new JSONArray(entity);
                            if(array.length()==0){
                                Toast.makeText(ArgeListActivity.this, "数据为空！",
                                        Toast.LENGTH_SHORT).show();
                                if(isFirst){
                                    mLayoutNoMsg.setVisibility(View.VISIBLE);
                                    isFirst=false;
                                }
                                isLoadMoreEmpty = true;
                                return;
                            }
                            isFirst=false;
                            analysisJson(array);
                        }else {
                            String error=object.getString("msg");
                            Toast.makeText(ArgeListActivity.this, error,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("getArgeUrl","getArgeUrl=="+str);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(swipeRefreshLayout!=null && swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                Toast.makeText(ArgeListActivity.this, "加载失败！"+t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                mLayoutLoading.setVisibility(View.GONE);
                Log.e("onFailure","onFailure=="+t.getMessage());
            }
        });

    }

    private void analysisJson(JSONArray array) throws JSONException {
        List<BestSignAgreementEntity> mList = new ArrayList<>();
        Log.e("array size", "array size==" + array.length());
        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                String str = array.getString(i);
                Gson gson = new Gson();
                BestSignAgreementEntity entity = gson.fromJson(str, BestSignAgreementEntity.class);
                mList.add(entity);
            }
            list.addAll(mList);
            argeListAdapter.setDatas(list);
        } else {
            if (isLoadMore) {
                isLoadMore = false;
                isLoadMoreEmpty = true;
                Toast.makeText(ArgeListActivity.this, "暂无更多！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
