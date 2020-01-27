package com.hykc.cityfreight.fragment;

import android.content.Intent;
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
import com.hykc.cityfreight.activity.GoodsListDetailActivity;
import com.hykc.cityfreight.adapter.SourceAdapter;
import com.hykc.cityfreight.entity.EventEntity;
import com.hykc.cityfreight.entity.UCarEntity;
import com.hykc.cityfreight.entity.UCardEntity;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.entity.UWaybill;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.RxBus;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.hykc.cityfreight.view.SelectCarDialog;
import com.google.gson.Gson;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class SourceFragment extends BaseFragment implements View.OnClickListener {
    private CityPickerView mStartCityPickerView = new CityPickerView();
    private CityPickerView mEndCityPickerView = new CityPickerView();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout mLayoutNoMsg;
    private RecyclerView recyclerView;
    private static final int pageSize = 10;
    private int pageCurrent = 1;
    private TextView mTextReClick;
    private List<UWaybill> mList=new ArrayList<>();
    private SourceAdapter adapter;
    private RelativeLayout mLayoutStart;
    private RelativeLayout mLayoutEnd;
    private TextView mTextStart;
    private TextView mTextEnd;
    private ImageView mImgSearch;
    private String startArea=null;
    private String endArea=null;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private boolean isLoadMoreEmpty = false;
    private boolean isFirst = true;
    private boolean isSearch=false;
    private RelativeLayout mLayoutLoading;
    private UDriver uDriver=null;
    private List<UCardEntity> mCardList = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_source;
    }

    @Override
    protected void initView(View view) {
        String userinfo = SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        if(TextUtils.isEmpty(userinfo)){
            Toast.makeText(getActivity(), "用户信息为空，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Gson gson=new Gson();
        uDriver= gson.fromJson(userinfo,UDriver.class);
        if(null==uDriver){
            Toast.makeText(getActivity(), "用户信息为空，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        mLayoutStart=view.findViewById(R.id.layout_start);
        mLayoutEnd=view.findViewById(R.id.layout_end);
        mTextStart=view.findViewById(R.id.tv_start);
        mTextEnd=view.findViewById(R.id.tv_end);
        mImgSearch=view.findViewById(R.id.img_search);
        mTextReClick=view.findViewById(R.id.btn_reclick);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        int color = getResources().getColor(R.color.actionbar_color);
        swipeRefreshLayout.setColorSchemeColors(color, color, color);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(null);
        adapter=new SourceAdapter(getActivity(),mList);
        recyclerView.setAdapter(adapter);
        mLayoutLoading = view.findViewById(R.id.layout_loading);
        mLayoutNoMsg=view.findViewById(R.id.layout_nomsg);
        mLayoutNoMsg.setVisibility(View.GONE);
        initCityPicker();
        initEvent();
    }
    private void initCityPicker() {
        mStartCityPickerView.init(getActivity());
        mEndCityPickerView.init(getActivity());
        CityConfig cityConfig = new CityConfig.Builder().title("选择城市")
                .title("选择城市")//标题
                .confirTextColor("#298cf5")//确认按钮文字颜色
                .provinceCyclic(false)//省份滚轮是否可以循环滚动
                .cityCyclic(false)//城市滚轮是否可以循环滚动
                .districtCyclic(false)//区县滚轮是否循环滚动
                .province("河南省")//默认显示的省份
                .city("郑州市")//默认显示省份下面的城市
                .district("金水区")//默认显示省市下面的区县数据
                .build();

        mStartCityPickerView.setConfig(cityConfig);
        mEndCityPickerView.setConfig(cityConfig);
        mStartCityPickerView.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                StringBuffer sbf=new StringBuffer();
                if (province != null) {

                }

                if (city != null) {
                    String strCity=city.getName();
                    sbf.append(strCity);
                }

                if (district != null) {
                    String strdistrict=district.getName();
                    sbf.append(strdistrict);
                    startArea=strdistrict;
                }
                mTextStart.setText(sbf.toString());
            }

            @Override
            public void onCancel() {

            }
        });
        mEndCityPickerView.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                StringBuffer sbf=new StringBuffer();
                if (province != null) {

                }
                if (city != null) {
                    String strCity=city.getName();
                    sbf.append(strCity);
                }
                if (district != null) {
                    String strdistrict=district.getName();
                    sbf.append(strdistrict);
                    endArea=strdistrict;
                }
                mTextEnd.setText(sbf.toString());

            }

            @Override
            public void onCancel() {

            }
        });
    }
    private void refreshDatas() {
        swipeRefreshLayout.setRefreshing(true);
        isRefresh = true;
        isLoadMore = false;
        isLoadMoreEmpty = false;
        mList.clear();
        pageCurrent = 1;
        initData();
    }
    private void initEvent() {
        mLayoutStart.setOnClickListener(this);
        mLayoutEnd.setOnClickListener(this);
        mImgSearch.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDatas();
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
        adapter.setOnItemBtnClickListener(new SourceAdapter.OnItemBtnClickListener() {
            @Override
            public void onItemClick(int pos, UWaybill uWaybill) {
                Intent intent = new Intent(getActivity(), GoodsListDetailActivity.class);
                intent.putExtra("entity", uWaybill);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }

            @Override
            public void onQDClick(int pos, UWaybill uWaybill) {

               // getCarInfoList(uWaybill);
                getCarInfoList(uWaybill);

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
    }

    private void getCarInfoList(final UWaybill uWaybill) {
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getChildFragmentManager(),"getCardInfoListView");
        Map<String, String> map = new HashMap<>();
        map.put("mobile", uDriver.getMobile());
        RequestManager.getInstance()
                .mServiceStore
                .selectAllCarInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        dialogFragment.dismissAllowingStateLoss();
                        Log.e("getCarInfo onSuccess", msg);
                        analysisJson(msg,uWaybill);
                    }

                    @Override
                    public void onError(String msg) {
                        dialogFragment.dismissAllowingStateLoss();
                        Log.e("getCarInfo onError", msg);
                    }
                }));

    }
    private void analysisJson(String msg,final UWaybill uWaybill) {
        List<UCarEntity> list=new ArrayList<>();
        try {
            JSONObject object=new JSONObject(msg);
            if(object.getBoolean("success")){
                String str=object.getString("entity");
                if(!TextUtils.isEmpty(str)){
                    JSONArray array=new JSONArray(str);
                    for (int i = 0; i <array.length() ; i++) {
                        String arr= array.getString(i);
                        Gson gson=new Gson();
                        UCarEntity uCarEntity=gson.fromJson(arr,UCarEntity.class);
                        list.add(uCarEntity);
                    }
                    if(list.size()>1){
                        showCarSelectView(list,uWaybill);
                    }else{
                        UCarEntity carEntity=list.get(0);
                        doJD(uWaybill,carEntity);
                    }
                }else {
                    mLayoutNoMsg.setVisibility(View.VISIBLE);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //选择车辆信息
    private void showCarSelectView(final List<UCarEntity> list, final UWaybill uWaybill) {
        final SelectCarDialog selectCarDialog=SelectCarDialog.getInstance(list);
        selectCarDialog.show(getChildFragmentManager(),"SelectCarDialogView");
        selectCarDialog.setOnSelectListener(new SelectCarDialog.OnSelectListener() {
            @Override
            public void onSelect(int pos, UCarEntity entity) {
                doJD(uWaybill,entity);
            }
        });
    }

    private void doJD(final UWaybill uWaybill,final UCarEntity uCarEntity){
        final LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
        loadingDialogFragment.showF(getChildFragmentManager(),"doJDView");
        Map<String,String> map=new HashMap<>();
        map.put("waybillId",uWaybill.getWaybillId());
        map.put("driverId",uDriver.getId()+"");
        map.put("carId",uCarEntity.getId()+"");
        RequestManager.getInstance()
                .mServiceStore
                .driverStrive(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Log.e("getCarInfo onSuccess", msg);
                        try {
                            JSONObject object=new JSONObject(msg);
                            if(object.getBoolean("success")){
                                refreshDatas();
                                RxBus.getInstance().send(new EventEntity("wwc_refresh","wwc_refresh"));
                                showQDView("抢单成功！请到我的运单中进行配送。");
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
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Log.e("getCarInfo onError", msg);
                    }
                }));

    }


    private void showQDView(String msg){
        final ExitDialogFragment exitDialogFragment=ExitDialogFragment.getInstance(msg);
        exitDialogFragment.showF(getChildFragmentManager(),"showQDView");
        exitDialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                exitDialogFragment.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                exitDialogFragment.dismissAllowingStateLoss();
            }
        });




    }


    @Override
    protected void initData() {
        getUwaybillStrive();
    }

    private void getUwaybillStrive() {
        Map<String,String> map=new HashMap<>();
        if(!TextUtils.isEmpty(startArea)){
            map.put("fromArea",startArea);
        }
        if(!TextUtils.isEmpty(endArea)){
            map.put("toArea",endArea);
        }
        map.put("pageSize", pageSize + "");
        map.put("pageCurrent", pageCurrent + "");
        RequestManager.getInstance()
                .mServiceStore
                .selectUwaybillStrive(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("selectUwaybillStrive", msg);
                        mLayoutLoading.setVisibility(View.GONE);
                        if(null!=swipeRefreshLayout  && swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getBoolean("success")) {
                                String str = object.getString("entity");
                                JSONArray array = new JSONArray(str);
                                if(array.length()==0){
                                    if (isRefresh) {
                                        adapter.setDatas(mList);
                                        adapter.notifyDataSetChanged();
                                        swipeRefreshLayout.setRefreshing(false);
                                        isRefresh = false;
                                    }
                                    if(isFirst){
                                        mLayoutNoMsg.setVisibility(View.VISIBLE);
                                        isFirst=false;
                                    }
                                    isLoadMoreEmpty = true;
                                    if(isSearch){
                                        mList.clear();
                                        adapter.setDatas(mList);
                                        adapter.notifyDataSetChanged();
                                        isSearch=false;
                                    }
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
                        if(swipeRefreshLayout!=null && swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        mLayoutLoading.setVisibility(View.GONE);
                        isSearch=false;
                        Toast.makeText(getActivity(), "加载失败！"+msg, Toast.LENGTH_SHORT).show();
                        Log.e("selectUwaybillStrive ", msg);
                    }
                }));
    }

    private void analysisJson(JSONArray array) throws JSONException {
        List<UWaybill> list = new ArrayList<>();
        Log.e("array size", "array size==" + array.length());
        if (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {
                String str = array.getString(i);
                Gson gson = new Gson();
                UWaybill uWaybill = gson.fromJson(str, UWaybill.class);
                list.add(uWaybill);
            }
            mList.addAll(list);
            adapter.setDatas(list);
        } else {
            if (isLoadMore) {
                isLoadMore = false;
                isLoadMoreEmpty = true;
                Toast.makeText(getActivity(), "暂无更多！", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public static  SourceFragment newInstance(){
        SourceFragment fragment=new SourceFragment();
        return fragment;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.layout_start:
                mStartCityPickerView.showCityPicker();
                break;
            case R.id.layout_end:
                mEndCityPickerView.showCityPicker();
                break;
            case R.id.img_search:
                doSearch();
                break;

        }

    }


    private void doSearch() {
        isSearch=true;
        refreshDatas();
    }

}
