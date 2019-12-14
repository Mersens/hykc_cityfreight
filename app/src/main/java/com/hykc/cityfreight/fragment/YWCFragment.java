package com.hykc.cityfreight.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.activity.GoodsListDetailActivity;
import com.hykc.cityfreight.activity.MainActivity;
import com.hykc.cityfreight.adapter.YWCAdapter;
import com.hykc.cityfreight.entity.UWaybill;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.utils.StatusType;
import com.hykc.cityfreight.view.RecyclerViewNoBugLinearLayoutManager;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class YWCFragment extends BaseFragment {
    private static final int REQUEST_CODE=1001;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout mLayoutNoMsg;
    private RelativeLayout mLayoutLoading;
    private static final int pageSize = 10;
    private int pageCurrent = 1;
    WeakReference<MainActivity> mActivityReference;
    private List<UWaybill> list = new ArrayList<>();
    private YWCAdapter adapter;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private boolean isLoadMoreEmpty = false;
    private boolean isFirst=true;
    private TextView mTextReClick;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityReference = new WeakReference<>((MainActivity) context);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_ywc;
    }

    @Override
    protected void initView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        int color = getResources().getColor(R.color.actionbar_color);
        swipeRefreshLayout.setColorSchemeColors(color, color, color);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new RecyclerViewNoBugLinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(null);
        adapter = new YWCAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
        mLayoutNoMsg = view.findViewById(R.id.layout_nomsg);
        mLayoutLoading = view.findViewById(R.id.layout_loading);
        mTextReClick=view.findViewById(R.id.btn_reclick);
        initEvent();

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
    private void initEvent() {
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
                            Toast.makeText(getActivity(), "正在加载第" + pageCurrent + "页", Toast.LENGTH_SHORT).show();
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
        mTextReClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutNoMsg.setVisibility(View.GONE);
                isFirst=true;
                refreshDatas();
            }
        });
        adapter.setOnItemBtnClickListener(new YWCAdapter.OnItemBtnClickListener() {
            @Override
            public void onJDClick(int pos, UWaybill uWaybill) {

            }

            @Override
            public void onPSClick(int pos, UWaybill uWaybill) {

            }

            @Override
            public void onSDClick(int pos, UWaybill uWaybill) {

            }

            @Override
            public void onCancelClick(int pos, UWaybill uWaybill) {

            }

            @Override
            public void onDetailsClick(int pos, UWaybill uWaybill) {
                Intent intent = new Intent(getActivity(), GoodsListDetailActivity.class);
                intent.putExtra("entity", uWaybill);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }

            @Override
            public void onItemClick(int pos, UWaybill uWaybill) {
                Intent intent = new Intent(getActivity(), GoodsListDetailActivity.class);
                intent.putExtra("entity", uWaybill);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }

            @Override
            public void onPZClick(int pos, UWaybill uWaybill) {

            }
        });
    }

    @Override
    protected void initData() {
        String userinfo = SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        if(TextUtils.isEmpty(userinfo)){
            Toast.makeText(getActivity(), "用户信息为空，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject object = new JSONObject(userinfo);
            String id = object.getString("id");
            if (TextUtils.isEmpty(id)) {
                Toast.makeText(getActivity(), "用户id为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            final String token = object.getString("token");
            if (TextUtils.isEmpty(token)) {
                Toast.makeText(getActivity(), "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            getWaybillInfo(id, token);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getWaybillInfo(String id, String token) {
        Log.e("userid", "userid==" + id);
        final String userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        if(TextUtils.isEmpty(userid)){
            Toast.makeText(getActivity(), "userid为空，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("mobile", userid);
        map.put("id", id);
        map.put("token", token);
        map.put("pageSize", pageSize + "");
        map.put("pageCurrent", pageCurrent + "");
        map.put("type", StatusType.YWC_STATUS + "");
        RequestManager.getInstance()
                .mServiceStore
                .getWaybillList(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        Log.e("wwcgetWaybillList", msg);
                        mLayoutLoading.setVisibility(View.GONE);
                        if (isRefresh) {
                            swipeRefreshLayout.setRefreshing(false);
                            isRefresh = false;
                        }
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getBoolean("success")) {
                                String str = object.getString("entity");
                                JSONArray array = new JSONArray(str);
                                if(array.length()==0){
                                    if(isFirst){
                                        mLayoutNoMsg.setVisibility(View.VISIBLE);
                                        isFirst=false;
                                    }
                                    if (isLoadMore) {
                                        isLoadMore = false;
                                        isLoadMoreEmpty = true;
                                        Toast.makeText(getActivity(), "暂无更多！", Toast.LENGTH_SHORT).show();
                                    }
                                    isLoadMoreEmpty = true;
                                    Toast.makeText(getActivity(), "数据为空！", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                isFirst=false;
                                analysisJson(array);
                            }else {
                                String error=object.getString("msg");
                                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        Toast.makeText(getActivity(), "加载失败！"+msg, Toast.LENGTH_SHORT).show();
                        mLayoutLoading.setVisibility(View.GONE);
                        Log.e("wwcgetWaybillList ", msg);
                    }
                }));

    }

    private void analysisJson(JSONArray array) throws JSONException {
        List<UWaybill> mList = new ArrayList<>();
        Log.e("array size", "array size==" + array.length());
        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                String str = array.getString(i);
                Gson gson = new Gson();
                UWaybill uWaybill = gson.fromJson(str, UWaybill.class);
                mList.add(uWaybill);
            }
            list.addAll(mList);
            adapter.setDatas(list);
        } else {
            if (isLoadMore) {
                isLoadMore = false;
                isLoadMoreEmpty = true;
                Toast.makeText(getActivity(), "暂无更多！", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static YWCFragment newInstance(){
        YWCFragment fragment=new YWCFragment();
        return fragment;
    }
}
