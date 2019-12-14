package com.hykc.cityfreight.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.activity.NewsDetailsActivity;
import com.hykc.cityfreight.adapter.NewsAdapter;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.entity.NewsEntity;
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

public class NewsFragment extends BaseFragment {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout mLayoutNoMsg;
    private static final int pageSize = 10;
    private int pageCurrent = 1;
    private String type=null;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private boolean isLoadMoreEmpty = false;
    private boolean isFirst = true;
    private List<NewsEntity> list = new ArrayList<>();
    private NewsAdapter adapter;
    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_news;
    }

    @Override
    protected void initView(View view) {
        Bundle bundle=getArguments();
        if(bundle!=null){
            type=bundle.getString("type");
        }
        if(TextUtils.isEmpty(type)){
            type="头条";
        }
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        int color = getResources().getColor(R.color.actionbar_color);
        swipeRefreshLayout.setColorSchemeColors(color, color, color);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        mLayoutNoMsg = view.findViewById(R.id.layout_nomsg);
        adapter=new NewsAdapter(getActivity(),list);
        recyclerView.setAdapter(adapter);
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

        adapter.setOnItemBtnClickListener(new NewsAdapter.OnItemBtnClickListener() {

            @Override
            public void onItemClick(int pos, NewsEntity newsEntity) {
                String url=newsEntity.getUrl();
                Intent intent=new Intent(getActivity(),NewsDetailsActivity.class);
                intent.putExtra("url",url);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

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
        initData();
    }
    @Override
    protected void initData() {
        if(null!=swipeRefreshLayout && !swipeRefreshLayout.isRefreshing()){
            if(isRefresh || isFirst){
                swipeRefreshLayout.setRefreshing(true);
            }
        }
        Map<String,String> map=new HashMap<>();
        map.put("pageSize", pageSize + "");
        map.put("pageCurrent", pageCurrent + "");
        map.put("category", type);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.OIL_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.getNewsInfo(map);
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
                        if(object.getBoolean("success")){
                            JSONArray array = new JSONArray(object.getString("result"));
                            if (array.length() == 0) {
                                if (isFirst) {
                                    mLayoutNoMsg.setVisibility(View.VISIBLE);
                                    isFirst = false;
                                }
                                isLoadMoreEmpty = true;
                                Toast.makeText(getActivity(), "数据为空！", Toast.LENGTH_SHORT).show();
                                if (isLoadMore) {
                                    isLoadMore = false;
                                    isLoadMoreEmpty = true;
                                    Toast.makeText(getActivity(), "暂无更多！", Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            isFirst = false;
                            analysisJson(array);

                        }else {
                            Toast.makeText(getActivity(), "数据获取失败！", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(getActivity(), "数据获取失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("getNewsInfo","getNewsInfo=="+str);
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
            }
        });

    }

    private void analysisJson(JSONArray array) throws JSONException {
        List<NewsEntity> mList = new ArrayList<>();
        Log.e("array size", "array size==" + array.length());
        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                String str = array.getString(i);
                Gson gson = new Gson();
                NewsEntity newsEntity = gson.fromJson(str, NewsEntity.class);
                mList.add(newsEntity);
            }
            mLayoutNoMsg.setVisibility(View.GONE);
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

    public static NewsFragment newInstance(String type){
        Bundle bundle=new Bundle();
        bundle.putString("type",type);
        NewsFragment fragment=new NewsFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

}
