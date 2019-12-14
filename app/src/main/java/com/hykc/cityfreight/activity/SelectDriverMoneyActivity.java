package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.OrderMoneyAdapter;
import com.hykc.cityfreight.entity.ZDriver;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class SelectDriverMoneyActivity extends BaseActivity {
    private ImageView mImgBack;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout mLayoutNoMsg;
    private String userid=null;
    List<ZDriver> mZDriverList = new ArrayList<>();
    private OrderMoneyAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_select_order_money);
        init();
    }

    @Override
    public void init() {
        userid=getIntent().getStringExtra("userid");
        initViews();
        initEvent();
        initData();

    }

    private void initData() {
        if(TextUtils.isEmpty(userid)){
            Toast.makeText(this, "司机手机号为空，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        getMoneyInfo(userid);
    }

    private void getMoneyInfo(final String mobile) {
        if(null!=swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(true);
        }
        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobile);
        RequestManager.getInstance()
                .mServiceStore
                .getDriverOrderInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        if(null!=swipeRefreshLayout && swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Log.e(" getMoneyInfo onSuccess", "====" + msg);
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getBoolean("success")) {
                                String entity = object.getString("entity");
                                JSONArray array=new JSONArray(entity);
                                if(array.length()==0){
                                    mLayoutNoMsg.setVisibility(View.VISIBLE);
                                }else {
                                    mLayoutNoMsg.setVisibility(View.GONE);
                                    Gson gson = new Gson();
                                    for (int i = 0; i <array.length() ; i++) {
                                        String string=array.getString(i);
                                        ZDriver driver = gson.fromJson(string, ZDriver.class);
                                        mZDriverList.add(driver);
                                    }
                                    //设置数据源
                                    if(adapter!=null){
                                        adapter.setDatas(mZDriverList);
                                    }
                                }
                            } else {
                                String errorMsg = object.getString("msg");
                                Toast.makeText(SelectDriverMoneyActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        if(null!=swipeRefreshLayout && swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        mLayoutNoMsg.setVisibility(View.VISIBLE);
                        Log.e("onError", "====" + msg);
                        Toast.makeText(SelectDriverMoneyActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void initViews() {
        mImgBack=findViewById(R.id.img_back);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        int color = getResources().getColor(R.color.actionbar_color);
        swipeRefreshLayout.setColorSchemeColors(color, color, color);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        adapter=new OrderMoneyAdapter(this,mZDriverList);
        recyclerView.setAdapter(adapter);
        mLayoutNoMsg = findViewById(R.id.layout_nomsg);

    }
    private void initEvent() {
        adapter.setOnItemBtnClickListener(new OrderMoneyAdapter.OnItemBtnClickListener() {
            @Override
            public void onItemClick(int pos, ZDriver entity) {
                String msg="确定提现"+entity.getSurplusPrice()+"元？";
                showSelecdView(msg,entity);
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
                mZDriverList.clear();
                initData();
            }
        });
    }

    private void showSelecdView(String msg,final ZDriver entity){
        final ExitDialogFragment dialogFragment=ExitDialogFragment.getInstance(msg);
        dialogFragment.showF(getSupportFragmentManager(),"showSelecdView");
        dialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialogFragment.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialogFragment.dismissAllowingStateLoss();
                Intent intent=new Intent();
                intent.putExtra("entity", entity);
                setResult(RESULT_OK,intent);
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

            }
        });


    }


}
