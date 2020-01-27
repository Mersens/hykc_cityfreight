package com.hykc.cityfreight.fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.hdgq.locationlib.LocationOpenApi;
import com.hdgq.locationlib.entity.ShippingNoteInfo;
import com.hdgq.locationlib.listener.OnResultListener;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.activity.GoodsListDetailActivity;
import com.hykc.cityfreight.activity.MainActivity;
import com.hykc.cityfreight.activity.MyCardActivity;
import com.hykc.cityfreight.activity.UpLoadImgActivity;
import com.hykc.cityfreight.activity.UpLoadPSImgActivity;
import com.hykc.cityfreight.adapter.WWCAdapter;
import com.hykc.cityfreight.app.AlctConstants;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.db.DBDao;
import com.hykc.cityfreight.db.DBDaoImpl;
import com.hykc.cityfreight.entity.EventEntity;
import com.hykc.cityfreight.entity.LocationEntity;
import com.hykc.cityfreight.entity.UCardEntity;
import com.hykc.cityfreight.entity.UCompany;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.entity.UWaybill;
import com.hykc.cityfreight.service.MqttManagerV3;
import com.hykc.cityfreight.service.PSLocationService;
import com.hykc.cityfreight.service.ServiceStore;
import com.hykc.cityfreight.service.WbCoudFaceManager;
import com.hykc.cityfreight.utils.AlctManager;
import com.hykc.cityfreight.utils.DateUtils;
import com.hykc.cityfreight.utils.LocationOpenApiHelper;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.RxBus;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.utils.StatusType;
import com.hykc.cityfreight.utils.WaybillStatus;
import com.hykc.cityfreight.view.EvaluateDialogFragment;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.hykc.cityfreight.view.FactTestDialog;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.hykc.cityfreight.view.RecyclerViewNoBugLinearLayoutManager;
import com.hykc.cityfreight.view.TXMoneyDialog;
import com.hykc.cityfreight.view.ValidationCodeFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WWCFragment extends BaseFragment {
    private static final int REQUEST_CODE = 1001;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout mLayoutNoMsg;
    private RelativeLayout mLayoutLoading;
    private static final int pageSize = 10;
    private int pageCurrent = 1;
    WeakReference<MainActivity> mActivityReference;
    private List<UWaybill> list = new ArrayList<>();
    private WWCAdapter adapter;
    private boolean isRefresh = false;
    private boolean isLoadMore = false;
    private boolean isLoadMoreEmpty = false;
    private boolean isFirst = true;
    private CompositeDisposable mCompositeDisposable;
    private PSLocationService.MyBind psbind = null;
    private MyPsLocationConnection psconnection = new MyPsLocationConnection();

    private DBDao dao;
    AlctManager alctManager = AlctManager.newInstance();
    MqttManagerV3 mqttManagerV3;
    private String area = null;
    private List<UCardEntity> mCardList = new ArrayList<>();
    private TextView mTextReClick;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityReference = new WeakReference<>((MainActivity) context);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_wwc;
    }

    @Override
    protected void initView(View view) {

        final String userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        mqttManagerV3 = MqttManagerV3.getInstance(userid);
        dao = new DBDaoImpl(getActivity());
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        int color = getResources().getColor(R.color.actionbar_color);
        swipeRefreshLayout.setColorSchemeColors(color, color, color);
        recyclerView.setItemAnimator(null);
        recyclerView.setLayoutManager(new RecyclerViewNoBugLinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(null);
        mLayoutNoMsg = view.findViewById(R.id.layout_nomsg);
        mLayoutLoading = view.findViewById(R.id.layout_loading);
        adapter = new WWCAdapter(getActivity(), list);
        recyclerView.setAdapter(adapter);
        mTextReClick=view.findViewById(R.id.btn_reclick);
        initEvent();
/*        Intent intent = new Intent(getActivity(), LocService.class);
        getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);*/
        Intent psIntent = new Intent(getActivity(), PSLocationService.class);
        getActivity().bindService(psIntent, psconnection, Context.BIND_AUTO_CREATE);
        alctManager.setOnAlctResultListener(new MyAlctListener());
        initBus();

    }

    private void initBus() {
        mCompositeDisposable = new CompositeDisposable();
        //监听订阅事件
        Disposable d = RxBus.getInstance()
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof EventEntity) {
                            EventEntity e = (EventEntity) o;
                            String type = e.type;
                            String v = e.value;
                            if (type.equals("wwc_refresh")) {
                                refreshDatas();
                            }
                        }
                    }
                });
        //subscription交给compositeSubscription进行管理，防止内存溢出
        mCompositeDisposable.add(d);

    }



    @Override
    protected void initData() {
        String userinfo = SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        if (TextUtils.isEmpty(userinfo)) {
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
        if (TextUtils.isEmpty(userid)) {
            Toast.makeText(getActivity(), "userid为空，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("mobile", userid);
        map.put("id", id);
        map.put("token", token);
        map.put("pageSize", pageSize + "");
        map.put("pageCurrent", pageCurrent + "");
        map.put("type", StatusType.WWC_STATUS + "");
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
                                if (array.length() == 0) {
                                    if (isFirst) {
                                        mLayoutNoMsg.setVisibility(View.VISIBLE);
                                        isFirst = false;
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
                                isFirst = false;
                                analysisJson(array);
                            } else {
                                String error = object.getString("msg");
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
                        Toast.makeText(getActivity(), "加载失败！" + msg, Toast.LENGTH_SHORT).show();
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
        adapter.setOnItemBtnClickListener(new WWCAdapter.OnItemBtnClickListener() {
            @Override
            public void onPSClick(int pos, UWaybill uWaybill) {
                //配送
                // doPs(uWaybill);
                if (TextUtils.isEmpty(area)) {
                    Toast.makeText(getActivity(), "定位信息为空！", Toast.LENGTH_SHORT).show();
                    if (psbind != null) {
                        psbind.startLocationService();
                    }
                    return;
                }
                String uWaybillLocation = uWaybill.getFromArea();
                if (area.contains(uWaybillLocation)) {
                    //再提货区域内
                    Intent intent = new Intent(getActivity(), UpLoadPSImgActivity.class);
                    intent.putExtra("entity", uWaybill);
                    startActivityForResult(intent, REQUEST_CODE);
                    getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                } else {
                    //不在区域内
                    String from_city=uWaybill.getFromCity();
                    String msg = "你当前处于 [ " + area+" ]"+ "\n请到 [" +from_city+" - "+ uWaybillLocation + " ]提货!";
                    showPsLocationView(msg);
                }
            }

            @Override
            public void onSDClick(int pos, UWaybill uWaybill) {
                //送达
                if (TextUtils.isEmpty(area)) {
                    Toast.makeText(getActivity(), "定位信息为空！", Toast.LENGTH_SHORT).show();
                    if (psbind != null) {
                        psbind.startLocationService();
                    }
                    return;
                }
                String uWaybillLocation = uWaybill.getToArea();
                if (area.contains(uWaybillLocation)) {
                    showEvaluateView(uWaybill);
                }else {
                    //不在区域内
                    String to_city=uWaybill.getToCity();
                    String msg = "你当前处于 [ " + area +" ] "+ "\n请到 [" +to_city+"-"+ uWaybillLocation + "] 卸货!";
                    showPsLocationView(msg);
                }
            }
            @Override
            public void onJDClick(int pos, UWaybill uWaybill) {
                //身份信息一致性校验
               // selectUDriverIsFaceTest(uWaybill);
                //接单
                //doJD(uWaybill);
                final String userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
                if (TextUtils.isEmpty(userid)) {
                    Toast.makeText(getActivity(), "userid为空，请重新登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                getMyCardInfo(userid,uWaybill);

            }

            @Override
            public void onCancelClick(int pos, UWaybill uWaybill) {
                //showCancelTips("确定取消运单?",uWaybill);

            }

            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
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
                //拍照
                Intent intent = new Intent(getActivity(), UpLoadImgActivity.class);
                intent.putExtra("entity", uWaybill);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }


    private void showEvaluateView(final UWaybill uWaybill) {
        final EvaluateDialogFragment evaluateDialogFragment = EvaluateDialogFragment.getInstance();
        evaluateDialogFragment.show(getChildFragmentManager(), "EvaluateView");
        evaluateDialogFragment.setOnReasonDialogListener(new EvaluateDialogFragment.OnReasonDialogListener() {
            @Override
            public void onCloseListener() {
                evaluateDialogFragment.dismiss();
                doSD(uWaybill);
            }
            @Override
            public void onComplateListener(int adminpingjia, int huozhupingjia) {
                evaluateDialogFragment.dismiss();
                evaluate_huozhu(uWaybill,adminpingjia,huozhupingjia);
                doSD(uWaybill);
            }
        });
    }


    private void evaluate_huozhu(final UWaybill uWaybill,int adminpingjia,int huozhupingjia ) {

        Map<String, String> map = new HashMap<>();
        map.put("waybillId", uWaybill.getWaybillId());
        map.put("adminpingjia", adminpingjia+"");
        map.put("huozhupingjia", huozhupingjia + "");
        RequestManager.getInstance()
                .mServiceStore
                .evaluate_huozhu(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("evaluate_huozhu", msg);

                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("evaluate_huozhu", "onError=="+msg);
                    }
                }));

    }


    private void getMyCardInfo(final String userid,final UWaybill uWaybill) {
        final LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
        loadingDialogFragment.showF(getChildFragmentManager(),"getCardInfoView");
        Map<String,String> map=new HashMap<>();
        map.put("mobile",userid);
        RequestManager.getInstance()
                .mServiceStore
                .selectCardInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Log.e("onSuccess getMyCardInfo", "====" + msg);
                        try {
                            JSONObject object=new JSONObject(msg);
                            if(object.getBoolean("success")){
                                analysisJson(object.getString("entity"),uWaybill);
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
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);

                    }
                }));
    }

    private void analysisJson(String entity ,final UWaybill uWaybill) {
        try {
            JSONArray array = new JSONArray(entity);
            if(array.length()==0){
                confirmBankTips("请绑定银行卡！");
                return;
            }
            mCardList.clear();
            Gson gson=new Gson();
            for (int i = 0; i <array.length() ; i++) {
                String string=array.getString(i);
                UCardEntity uCardEntity=gson.fromJson(string,UCardEntity.class);
                mCardList.add(uCardEntity);
            }
            showSelectCard(uWaybill);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showSelectCard(final UWaybill uWaybill){
            TXMoneyDialog txDialog = TXMoneyDialog.getInstance(mCardList,true);
            txDialog.show(getChildFragmentManager(), "txDialog");
            txDialog.setOnSelectListener(new TXMoneyDialog.OnSelectListener() {
                @Override
                public void onSelect(int pos, UCardEntity uCardEntity) {
                    if(null==uCardEntity){
                        Toast.makeText(getActivity(), "请选择提现类型", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(uCardEntity.getCardType()!=3){
                        Toast.makeText(getActivity(), "目前仅支持银行卡类型！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    checkUserType(uWaybill,uCardEntity);
                }
            });

    }

    private void checkUserType(final UWaybill uWaybill,UCardEntity uCardEntity){
        String userinfo = SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        if (TextUtils.isEmpty(userinfo)) {
            Toast.makeText(getActivity(), "用户信息为空，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        Gson gson=new Gson();
        UDriver uDriver=gson.fromJson(userinfo,UDriver.class);
        String driverName=uDriver.getDriverName();
        String cardName=uCardEntity.getName();
        if(cardName.equals(driverName)){
            //名字一致
            uWaybill.setUserType(1);
            //添加银行卡信息 提现校验需要
            uWaybill.setBank_user_name(cardName);
            uWaybill.setBank_user_account(uCardEntity.getAccount());
            uWaybill.setIsSelf(0);
        }else {
            //名字不一致
            uWaybill.setUserType(3);
            uWaybill.setBank_user_name(cardName);
            uWaybill.setBank_user_account(uCardEntity.getAccount());
            uWaybill.setIsSelf(1);
        }
        //身份信息一致性校验
        selectUDriverIsFaceTest(uWaybill);
    }

    private void confirmBankTips(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.showF(getChildFragmentManager(), "bankDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialog.dismissAllowingStateLoss();
                Intent intent = new Intent(getActivity(), MyCardActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

    }

    private void selectUDriverIsFaceTest(final UWaybill uWaybill) {

        String account = null;
        String driverName = null;
        String identityNo = null;
        String userinfo = SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        try {
            JSONObject object = new JSONObject(userinfo);
            account = object.getString("mobile");
            if (TextUtils.isEmpty(account)) {
                Toast.makeText(getActivity(), "手机号为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            driverName = object.getString("driverName");
            if (TextUtils.isEmpty(driverName)) {
                Toast.makeText(getActivity(), "姓名为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            identityNo = object.getString("identityNo");
            if (TextUtils.isEmpty(identityNo)) {
                Toast.makeText(getActivity(), "身份证号为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
        loadingDialogFragment.showF(getChildFragmentManager(),"IsFaceTestView");
        final String account1 = account;
        final String driverName1 = driverName;
        final String identityNo1 = identityNo;
        Map<String, String> map = new HashMap<>();
        map.put("account", account);
        map.put("statu", 1 + "");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call = serviceStore.selectUDriverIsFaceTest(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                loadingDialogFragment.dismissAllowingStateLoss();
                ResponseBody body = response.body();
                String str = null;
                try {
                    if (null != body) {
                        str = body.string();
                        JSONObject object = new JSONObject(str);
                        boolean success = object.getBoolean("success");
                        if (success) {
                            //已校验
                            doJD(uWaybill);
                            //doPayMoney(entity.getZyf(), Double.parseDouble(entity.getBl()), entity);
                        } else {
                            //未校验
                            showFaceView(account1, driverName1, identityNo1, uWaybill);
                        }
                    } else {
                        Toast.makeText(getActivity(), "用户信息查询失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("selectUDriverIsFaceTest", "selectUDriverIsFaceTest==" + str);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loadingDialogFragment.dismissAllowingStateLoss();
                Log.e("onFailure", "onFailure==" + t.getMessage());
            }
        });
    }

    private void showFaceView(final String account1,
                              final String driverName1,
                              final String identityNo1,
                              final UWaybill uWaybill) {
        final ExitDialogFragment dialogFragment = ExitDialogFragment.getInstance("请进行刷脸认证！");
        dialogFragment.show(getChildFragmentManager(), "showFaceView");
        dialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialogFragment.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialogFragment.dismissAllowingStateLoss();
                idcardFaceVerify(account1, driverName1, identityNo1, uWaybill);
            }
        });

    }

    private void idcardFaceVerify(final String account1,
                                  final String driverName1,
                                  final String identityNo1,
                                  final UWaybill uWaybill) {
        Map<String, String> map = new HashMap<>();
        map.put("account", account1);
        map.put("name", driverName1);
        map.put("identity", identityNo1);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call = serviceStore.idcardFaceVerify(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body = response.body();
                String str = null;
                try {
                    if (null != body) {
                        str = body.string();
                        JSONObject object = new JSONObject(str);
                        boolean success = object.getBoolean("success");
                        if (success) {
                            JSONObject jsonObject = new JSONObject(object.getString("msg"));
                            Log.e("idcardFaceVerify", "idcardFaceVerify==" + jsonObject.toString());
                            initFaceTest(jsonObject, account1, driverName1, identityNo1, uWaybill);
                        } else {
                            String error = object.getString("msg");
                            Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "用户信息查询失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("idcardFaceVerify", "idcardFaceVerify==" + str);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "onFailure==" + t.getMessage());
            }
        });
    }

    private void initFaceTest(JSONObject jsonObject,
                              final String account1,
                              final String driverName1,
                              final String identityNo1,
                              final UWaybill uWaybill) throws JSONException {

        String orderNo = jsonObject.getString("orderNo");
        String webankUserid = jsonObject.getString("webankUserId");
        String randomStr = jsonObject.getString("randomStr");
        String faceAuthSign = jsonObject.getString("faceAuthSign");
        WbCoudFaceManager wbCoudFaceManager = new WbCoudFaceManager(webankUserid,
                randomStr,
                orderNo,
                Constants.FACE_APPID,
                identityNo1,
                driverName1,
                faceAuthSign, getActivity());
        wbCoudFaceManager.setOnFaceListener(new WbCoudFaceManager.OnFaceListener() {
            @Override
            public void onSucccess() {
                Log.e("wbCoudFaceManager", "initFaceTest==onSucccess");
                //支付
                // 添加信息
                addDriverSignInfo(account1, driverName1, identityNo1, uWaybill, 0, 1,orderNo);
            }

            @Override
            public void onFail(String msg) {
                Toast.makeText(getActivity(), "认证失败" + msg, Toast.LENGTH_SHORT).show();
                Log.e("wbCoudFaceManager", "initFaceTest==" + msg);
                showFaceTestView(account1, driverName1, identityNo1, uWaybill, 0,orderNo);
            }
        });
        wbCoudFaceManager.execute();
    }

    private void showFaceTestView(final String account1,
                                  final String driverName1,
                                  final String identityNo1,
                                  final UWaybill uWaybill,
                                  final int statu,
                                  final String orderNo
                                  ) {

        final FactTestDialog factTestDialog = FactTestDialog.newInstance(account1, driverName1, identityNo1);
        factTestDialog.show(getChildFragmentManager(), "showFaceTestView");
        factTestDialog.setOnCheckListener(new FactTestDialog.OnCheckListener() {
            @Override
            public void onCheck() {
                factTestDialog.dismissAllowingStateLoss();
                addDriverSignInfo(account1, driverName1, identityNo1, uWaybill, 1, statu,orderNo);
            }

            @Override
            public void onDismiss() {
                factTestDialog.dismissAllowingStateLoss();
            }
        });
    }

    private void addDriverSignInfo(String account, String name, String identity,
                                   final UWaybill entity, int fromStatus,
                                   final int statu,final String orderNo) {
        Map<String, String> map = new HashMap<>();
        map.put("account", account);
        map.put("name", name);
        map.put("identity", identity);
        map.put("fromStatus", fromStatus + "");
        map.put("statu", statu + "");
        map.put("orderNo",orderNo);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call = serviceStore.addDriverSignInfo(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body = response.body();
                String str = null;
                try {
                    if (null != body) {
                        str = body.string();
                        JSONObject object = new JSONObject(str);
                        boolean success = object.getBoolean("success");
                        if (success) {
                            if (statu == 1) {
                                doJD(entity);
                            }
                        } else {
                            Toast.makeText(getActivity(), "认证失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "认证失败", Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("idcardFaceVerify", "idcardFaceVerify==" + str);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "认证失败", Toast.LENGTH_SHORT).show();
                Log.e("onFailure", "onFailure==" + t.getMessage());
            }
        });
    }

    private void doJD(final UWaybill uWaybill) {
        Map<String, String> map = new HashMap<>();
        String userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        String userinfo = SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        try {
            JSONObject object = new JSONObject(userinfo);
            final String token = object.getString("token");
            if (TextUtils.isEmpty(token)) {
                Toast.makeText(getActivity(), "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            final LoadingDialogFragment dialogFragment = LoadingDialogFragment.getInstance();
            dialogFragment.showF(getChildFragmentManager(), "dojd");
            map.put("token", token);
            map.put("mobile", userid);
            map.put("id", uWaybill.getId() + "");
            map.put("status", WaybillStatus.YJD_STATUS + "");
            RequestManager.getInstance()
                    .mServiceStore
                    .updateUWaybillStatuById(map)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                        @Override
                        public void onSuccess(String msg) {
                            Log.e("updateUWaybillStatu", msg);
                            dialogFragment.dismissAllowingStateLoss();
                            try {
                                JSONObject object = new JSONObject(msg);
                                if (object.getBoolean("success")) {
                                    Toast.makeText(getActivity(), "接单成功！", Toast.LENGTH_SHORT).show();
                                    getCompanyInfo(uWaybill);
                                    submitBestSignInfo(uWaybill);
                                    refreshDatas();
                                } else {
                                    String error = object.getString("msg");
                                    Toast.makeText(getActivity(), "接单失败！" + error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String msg) {
                            dialogFragment.dismissAllowingStateLoss();
                            Toast.makeText(getActivity(), "接单失败！", Toast.LENGTH_SHORT).show();
                        }
                    }));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getCompanyInfo(final UWaybill uWaybill){
        long companyid=uWaybill.getCompanyId();
        Map<String,String> map=new HashMap<>();
        map.put("id",companyid+"");
        RequestManager.getInstance()
                .mServiceStore
                .selectCompanyInfoById(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("CompanyInfo", msg);
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getBoolean("success")) {
                                Gson gson=new Gson();
                                String results=object.getString("entity");
                                UCompany uCompany=gson.fromJson(results,UCompany.class);
                                submitBestSignInfoForCompany(uWaybill,uCompany);
                            } else {
                                String error = object.getString("msg");
                                Log.e("CompanyInfo", error);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("CompanyInfo", msg);

                    }
                }));
    }


    private  Map<String,String> creatCompanyParams(UWaybill uWaybill,UCompany uCompany){
        Gson gson=new Gson();
        Log.e("UCompany params",gson.toJson(uCompany));
        String userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        String userinfo = SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        String driverName = "";
        try {
            JSONObject object = new JSONObject(userinfo);
            driverName = object.getString("driverName");
            if (TextUtils.isEmpty(driverName)) {
                Toast.makeText(getActivity(), "姓名为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return null;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String,String> map=new HashMap<>();
        String account=uCompany.getLegalPhone();
        if(!TextUtils.isEmpty(account)){
            if(account.length()==11){
                map.put("account",account);
                map.put("legalPhone",account);
            }else {
                return null;
            }

        }
        String corporateName=uCompany.getCorporateName();
        if(!TextUtils.isEmpty(corporateName)){
            map.put("corporateName",corporateName);
        }
        map.put("userType","2");
        String creditId=uCompany.getCreditId();
        if(!TextUtils.isEmpty(creditId)){
            map.put("creditId",creditId);
        }
        String legalName=uCompany.getLegalName();
        if(!TextUtils.isEmpty(legalName)){
            if(legalName.length()<2){
                return null;
            }
            map.put("legalName",legalName);
        }
        String legalId=uCompany.getLegalId();
        if(!TextUtils.isEmpty(legalId)){
            if(legalId.length()==15 || legalId.length()==18){
                map.put("legalId",legalId);
            }else {
                return null;
            }

        }
        String rowid = uWaybill.getWaybillId();
        map.put("rowid", rowid);
        map.put("tyf",uCompany.getCorporateName());
        String sjcyr = driverName;
        map.put("sjcyr", sjcyr);
        String hwmc = uWaybill.getGoodsName();
        map.put("hwmc", hwmc);
        String fhr = uWaybill.getShipperName();
        map.put("fhr", fhr);
        String fhrdh = uWaybill.getShipperPhone();
        map.put("fhrdh", fhrdh);
        String qyd = uWaybill.getFromProvince() + uWaybill.getFromCity();
        map.put("qyd", qyd);
        String shr = uWaybill.getConsigneeName();
        map.put("shr", shr);
        String shrdh = uWaybill.getConsigneePhone();
        map.put("shrdh", shrdh);
        String mdd = uWaybill.getToProvince() + uWaybill.getToCity();
        map.put("mdd", mdd);
        String jsy = driverName;
        map.put("jsy", jsy);
        String jsydh = userid;
        map.put("jsydh", jsydh);
        String dw = uWaybill.getTaskUnit();
        if (TextUtils.isEmpty(dw)) {
            dw = "";
        }
        String zl = uWaybill.getUcreditId() + "/" + dw;
        map.put("dw", zl);
        String bzfs = dw;
        map.put("bzfs", bzfs);
        String cph = uWaybill.getCarNumber();
        map.put("cph", cph);
        String zhsj = "2小时内";
        map.put("zhsj", zhsj);
        String ydsj = "2天内";
        map.put("ydsj", ydsj);
        map.put("hz", uWaybill.getHwjz() + "");
        double orderPrice = uWaybill.getOrderPrice();
        double adminPrice = uWaybill.getAdminPrice();
        double d = orderPrice - adminPrice;
        map.put("yunfei", d + "");
        Calendar calendar = Calendar.getInstance();
        String jf = "河南省脱颖实业有限公司";
        map.put("jf", jf);
        map.put("yf",corporateName);
        String n = calendar.get(Calendar.YEAR) + "";

        map.put("n", n);
        String y = (calendar.get(Calendar.MONTH) + 1) + "";

        map.put("y", y);
        String r = calendar.get(Calendar.DATE) + "";

        map.put("r", r);
        String hth = uWaybill.getWaybillId();
        map.put("hth", hth);
        String qdd = uWaybill.getFromProvince() + uWaybill.getFromCity();
        map.put("qdd", qdd);
        return map;
    }

    private void submitBestSignInfoForCompany(UWaybill uWaybill,UCompany uCompany){
        Map<String,String> map=creatCompanyParams(uWaybill,uCompany);
        if(map==null){
            Log.e("creatCompanyParams","map is null");
            return;
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call = serviceStore.autoSignAgreToCompanmy(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body = response.body();
                String str = null;
                try {
                    if (null != body) {
                        str = body.string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("autoSignAgreToCompanmy",
                        "autoSignAgreToCompanmy onResponse==" + str);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("autoSignAgreToCompanmy",
                        "autoSignAgreToCompanmy onResponse==" + t.getMessage());
            }
        });
    }

    private Map<String, String> complateParms(UWaybill uWaybill) {
        String userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        String userinfo = SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        String driverName = "";
        String identityNo = "";
        try {
            JSONObject object = new JSONObject(userinfo);
            driverName = object.getString("driverName");
            if (TextUtils.isEmpty(driverName)) {
                Toast.makeText(getActivity(), "姓名为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return null;
            }
            identityNo = object.getString("identityNo");
            if (TextUtils.isEmpty(identityNo)) {
                Toast.makeText(getActivity(), "身份证号为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        Map<String, String> map = new HashMap<>();
        String rowid = uWaybill.getWaybillId();
        map.put("rowid", rowid);
        String account = userid;
        map.put("isSelf",uWaybill.getIsSelf()+"");
        map.put("account", account);
        String name = driverName;
        map.put("name", name);
        String userType = uWaybill.getUserType()+"";
        map.put("userType", userType);
        map.put("agreType", userType);
       String bank_user_name=uWaybill.getBank_user_name();
       if(TextUtils.isEmpty(bank_user_name)){
           bank_user_name="";
       }
        String bank_user_account=uWaybill.getBank_user_account();
       if(TextUtils.isEmpty(bank_user_account)){
           bank_user_account="";
       }
        map.put("bank_user_name",bank_user_name);
        map.put("bank_user_account",bank_user_account);
        String identity = identityNo;
        map.put("identity", identity);
        String sjcyr = driverName;
        map.put("sjcyr", sjcyr);
        String hwmc = uWaybill.getGoodsName();
        map.put("hwmc", hwmc);
        String fhr = uWaybill.getShipperName();
        map.put("fhr", fhr);
        String fhrdh = uWaybill.getShipperPhone();
        map.put("fhrdh", fhrdh);
        String qyd = uWaybill.getFromProvince() + uWaybill.getFromCity();
        map.put("qyd", qyd);
        String shr = uWaybill.getConsigneeName();
        map.put("shr", shr);
        String shrdh = uWaybill.getConsigneePhone();
        map.put("shrdh", shrdh);
        String mdd = uWaybill.getToProvince() + uWaybill.getToCity();
        map.put("mdd", mdd);
        String jsy = driverName;
        map.put("jsy", jsy);
        String jsydh = userid;
        map.put("jsydh", jsydh);
        String dw = uWaybill.getTaskUnit();
        if (TextUtils.isEmpty(dw)) {
            dw = "";
        }
        String zl = uWaybill.getUcreditId() + "/" + dw;
        map.put("dw", zl);
        String bzfs = dw;
        map.put("bzfs", bzfs);
        String cph = uWaybill.getCarNumber();
        map.put("cph", cph);

        String zhsj = "2小时内";
        map.put("zhsj", zhsj);
        String ydsj = "2天内";
        map.put("ydsj", ydsj);
        map.put("hz", uWaybill.getHwjz() + "");
        double orderPrice = uWaybill.getOrderPrice();
        double adminPrice = uWaybill.getAdminPrice();
        double d = orderPrice - adminPrice;
        map.put("yunfei", d + "");
        String jf = "河南省脱颖实业有限公司";
        map.put("jf", jf);
        String yf = driverName;
        map.put("yf", yf);
        String n = calendar.get(Calendar.YEAR) + "";

        map.put("n", n);
        String y = (calendar.get(Calendar.MONTH) + 1) + "";

        map.put("y", y);
        String r = calendar.get(Calendar.DATE) + "";

        map.put("r", r);
        String hth = uWaybill.getWaybillId();
        map.put("hth", hth);
        String qdd = uWaybill.getFromProvince() + uWaybill.getFromCity();
        map.put("qdd", qdd);
        Log.e("autoSignParms", map.toString());
        return map;
    }


    private void submitBestSignInfo(UWaybill uWaybill) {
        Map<String, String> map = complateParms(uWaybill);
        if (map == null) {
            return;
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call = serviceStore.autoSignAgre(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body = response.body();
                String str = null;
                try {
                    if (null != body) {
                        str = body.string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("autoSignAgre", "autoSignAgre onResponse==" + str);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    class MyPsLocationConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            psbind = (PSLocationService.MyBind) service;
            psbind.getService().setOnLocationListener(new PSLocationService.OnLocationListener() {
                @Override
                public void onLocationReceive(BDLocation bdLocation) {
                    MainActivity activity = mActivityReference.get();
                    if (activity != null) {
                        activity.lon = bdLocation.getLongitude() + "";
                        activity.lat = bdLocation.getLatitude() + "";
                    }
                    area = bdLocation.getDistrict();
                    Log.e("onServiceConnected", "onServiceConnected===" + area);
                }
            });

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            psbind = null;

        }
    }

    class MyAlctListener implements AlctManager.OnAlctResultListener {
        @Override
        public void onSuccess(int type, UWaybill uWaybill) {
            Log.e("wwc alct", "wwc alct success   " + type);
            if (type == AlctConstants.REGISTER_SUCCESS) {
                return;
            }
            int updateType = -1;
            try {
                JSONObject object = new JSONObject();
                if (type == AlctConstants.PICKUP_SUCCESS) {
                    object.put("type", type + "");
                    object.put("success", "true");
                    object.put("msg", "提货成功！");
                    object.put("waybillId", uWaybill.getWaybillId());
                    uWaybill.setPickupMsg("提货成功！");
                    mqttManagerV3.sendWithThread(object.toString(), "");
                    updateType = 1;
                } else if (type == AlctConstants.POD_SUCCESS) {
                    object.put("type", type + "");
                    object.put("success", "true");
                    uWaybill.setUnloadMsg("卸货成功！");
                    object.put("msg", "卸货成功！");
                    object.put("waybillId", uWaybill.getWaybillId());
                    mqttManagerV3.sendWithThread(object.toString(), "");
                    updateType = 2;
                    Intent intent = new Intent(getActivity(), UpLoadImgActivity.class);
                    intent.putExtra("entity", uWaybill);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                } else if (type == AlctConstants.XHZ_SUCCESS) {
                    object.put("type", type + "");
                    object.put("success", "true");
                    object.put("msg", "卸货照上传成功！");
                    object.put("waybillId", uWaybill.getWaybillId());
                    mqttManagerV3.sendWithThread(object.toString(), "");
                    updateType = 3;
                    uWaybill.setAlctUnloadMsg("卸货照上传成功！");
                    upAlctImgMsg(uWaybill, "卸货照上传成功！");
                } else if (type == AlctConstants.HDZ_SUCCESS) {
                    object.put("type", type + "");
                    object.put("success", "true");
                    object.put("msg", "回单照上传成功！");
                    object.put("waybillId", uWaybill.getWaybillId());
                    mqttManagerV3.sendWithThread(object.toString(), "");
                }

                if (updateType != -1) {
                    updateOrderAlctMsg(updateType, uWaybill);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(int type, UWaybill uWaybill, String msg) {
            Log.e("wwc alct", "wwc alct onError   " + type + ";" + msg);
            int updateType = -1;
            if (type == AlctConstants.REGISTER_ERROR) {
                return;
            }
            if (type == AlctConstants.PICKUP_ERROR) {
                updateType = 1;
                uWaybill.setPickupMsg(msg);
            } else if (type == AlctConstants.POD_ERROR) {
                updateType = 2;
                uWaybill.setUnloadMsg(msg);
            } else if (type == AlctConstants.XHZ_ERROR) {
                updateType = 3;
                uWaybill.setAlctUnloadMsg(msg);
                upAlctImgMsg(uWaybill, msg);
            }

            if (updateType != -1) {
                updateOrderAlctMsg(updateType, uWaybill);
            }
            try {
                JSONObject object = new JSONObject();
                object.put("type", type + "");
                object.put("success", "false");
                object.put("msg", msg);
                object.put("waybillId", uWaybill.getWaybillId());
                mqttManagerV3.sendWithThread(object.toString(), "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateOrderAlctMsg(int updateType, UWaybill uWaybill) {
        String pickupMsg = uWaybill.getPickupMsg();
        if (pickupMsg == null) {
            pickupMsg = "";
        }
        String unloadMsg = uWaybill.getUnloadMsg();
        if (unloadMsg == null) {
            unloadMsg = "";
        }
        String alctUnloadMsg = uWaybill.getAlctUnloadMsg();
        if (alctUnloadMsg == null) {
            alctUnloadMsg = "";
        }
        Map<String, String> map = new HashMap<>();
        map.put("id", uWaybill.getId() + "");
        map.put("updateType", updateType + "");
        map.put("pickupMsg", pickupMsg);
        map.put("unloadMsg", unloadMsg);
        map.put("alctUnloadMsg", alctUnloadMsg);
        RequestManager.getInstance()
                .mServiceStore
                .updateOrderAlctMsg(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("updateOrderAlctMsg", msg);
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("updateOrderAlctMsg", msg);
                    }
                }));
    }

    private void showPsLocationView(String msg) {
        final ExitDialogFragment exitDialogFragment = ExitDialogFragment.getInstance(msg);
        exitDialogFragment.showF(getChildFragmentManager(), "locview");
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

    private void upAlctImgMsg(UWaybill waybill, String msg) {
        Map<String, String> map = new HashMap<>();
        map.put("id", waybill.getId() + "");
        map.put("upimageMsg", msg);
        RequestManager.getInstance()
                .mServiceStore
                .upAlctImgMsg(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("upAlctImgMsg", "upLoadOrderImg===" + msg);
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("upAlctImgMsg", msg);
                    }
                }));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == UpLoadPSImgActivity.RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                if (data.hasExtra("entity")) {
                    UWaybill uWaybill = (UWaybill) data.getSerializableExtra("entity");
                    doPs(uWaybill);
                }
            }
        }
    }

    private void doPs(final UWaybill uWaybill) {
        submitOrderEtcInfo(uWaybill, 1);
        Map<String, String> map = new HashMap<>();
        String userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        String userinfo = SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        try {
            JSONObject object = new JSONObject(userinfo);
            final String token = object.getString("token");
            if (TextUtils.isEmpty(token)) {
                Toast.makeText(getActivity(), "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            final LoadingDialogFragment dialogFragment = LoadingDialogFragment.getInstance();
            dialogFragment.showF(getChildFragmentManager(), "dops");
            map.put("token", token);
            map.put("mobile", userid);
            map.put("id", uWaybill.getId() + "");
            map.put("status", WaybillStatus.KSPS_STATUS + "");
            RequestManager.getInstance()
                    .mServiceStore
                    .updateUWaybillStatuById(map)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                        @Override
                        public void onSuccess(String msg) {
                            alctManager.alctPickup(uWaybill);
                            Log.e("updateUWaybillStatu", msg);
                            dialogFragment.dismissAllowingStateLoss();
                            try {
                                JSONObject object = new JSONObject(msg);
                                if (object.getBoolean("success")) {
                                    openApiStart(uWaybill);
                                    Toast.makeText(getActivity(), "配送成功！", Toast.LENGTH_SHORT).show();
         /*                           if (bind != null) {
                                        bind.startLocationService(uWaybill.getWaybillId());
                                    }*/
                                    refreshDatas();
                                } else {
                                    String error = object.getString("msg");
                                    Toast.makeText(getActivity(), "配送失败！" + error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String msg) {
                            alctManager.alctPickup(uWaybill);
                            dialogFragment.dismissAllowingStateLoss();
                            Toast.makeText(getActivity(), "配送失败！", Toast.LENGTH_SHORT).show();
                        }
                    }));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ShippingNoteInfo creatShippingNoteInfo(UWaybill uWaybill){
        ShippingNoteInfo shippingNoteInfo=new ShippingNoteInfo();
        shippingNoteInfo.setShippingNoteNumber(uWaybill.getWaybillId());
        shippingNoteInfo.setSerialNumber("0101");
        shippingNoteInfo.setStartCountrySubdivisionCode(uWaybill.getFromAreaId()+"");
        shippingNoteInfo.setEndCountrySubdivisionCode(uWaybill.getToAreaId()+"");
        Gson gson=new Gson();
        Log.e("ShippingNoteInfo","==="+gson.toJson(shippingNoteInfo));
        return shippingNoteInfo;
    }

    private void openApiStart(UWaybill uWaybill){
        ShippingNoteInfo[] shippingNoteInfos=new ShippingNoteInfo[1];
        shippingNoteInfos[0]=creatShippingNoteInfo(uWaybill);
        MainActivity activity = mActivityReference.get();
        if (activity == null) {
            return;
        }
        LocationOpenApi.start(activity, shippingNoteInfos, new OnResultListener() {
            @Override
            public void onSuccess() {
                Log.e("LocationOpenApi","onApiStart onSuccess");
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("LocationOpenApi","onApiStart onFailure="+s+";"+s1);
            }
        });

    }

    private void openApiStop(UWaybill uWaybill){
        ShippingNoteInfo[] shippingNoteInfos=new ShippingNoteInfo[1];
        shippingNoteInfos[0]=creatShippingNoteInfo(uWaybill);
        MainActivity activity = mActivityReference.get();
        if (activity == null) {
            return;
        }
        LocationOpenApi.stop(activity, shippingNoteInfos, new OnResultListener() {
            @Override
            public void onSuccess() {
                Log.e("LocationOpenApi","onApiSotp onSuccess");
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("LocationOpenApi","onApiSotp onFailure="+s+";"+s1);
            }
        });
    }

    public void submitOrderEtcInfo(UWaybill uWaybill, int t) {
        Map<String, String> map = new HashMap<>();
        map.put("alctCode", uWaybill.getAlctCode());
        map.put("num", uWaybill.getWaybillId());
        map.put("plateNum", uWaybill.getCarNumber());
        map.put("plateColor", "1");
        map.put("rowid", uWaybill.getWaybillId());
        map.put("startTime", DateUtils.getTimeWishT(new Date()));
        map.put("sourceAddr", uWaybill.getFromLocation());
        map.put("destAddr", uWaybill.getToLocation());
        Date date = DateUtils.getNextDay(new Date(), 3);
        map.put("predictEndTime", DateUtils.getTimeWishT(date));
        double d = Double.valueOf(uWaybill.getOrderPrice());
        long b = (long) (d * 100);
        map.put("fee", b + "");
        map.put("titleType", "2");
        map.put("type", "1");
        map.put("statuType", t + "");
        Gson gson = new Gson();
        String params = gson.toJson(map);
        Log.e("params", "params==" + params);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.ETC_URL).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call = null;
        if (t == 1) {
            call = serviceStore.submitOrderEctInfoStart(map);
        } else {
            call = serviceStore.submitOrderEctInfoEnd(map);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body = response.body();
                String str = null;
                try {
                    if (null != body) {
                        str = body.string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("onResponse", "onResponse==" + str);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure", "onFailure==" + t.getMessage());
            }
        });
    }

    private void doSD(final UWaybill uWaybill) {
        submitOrderEtcInfo(uWaybill, 2);
        Map<String, String> map = new HashMap<>();
        String userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        String userinfo = SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        try {
            JSONObject object = new JSONObject(userinfo);
            final String token = object.getString("token");
            if (TextUtils.isEmpty(token)) {
                Toast.makeText(getActivity(), "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            final LoadingDialogFragment dialogFragment = LoadingDialogFragment.getInstance();
            dialogFragment.showF(getChildFragmentManager(), "dosd");
            map.put("token", token);
            map.put("mobile", userid);
            map.put("id", uWaybill.getId() + "");
            map.put("status", WaybillStatus.YSD_STATUS + "");
            RequestManager.getInstance()
                    .mServiceStore
                    .updateUWaybillStatuById(map)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                        @Override
                        public void onSuccess(String msg) {
                            Log.e("updateUWaybillStatu", msg);
                            dialogFragment.dismissAllowingStateLoss();
                            try {
                                JSONObject object = new JSONObject(msg);
                                if (object.getBoolean("success")) {
                                    openApiStop(uWaybill);
                                    Toast.makeText(getActivity(), "送达成功！", Toast.LENGTH_SHORT).show();
/*                                    if (bind != null) {
                                        bind.stopLocationService();
                                        upLoadLocation(uWaybill.getWaybillId());
                                    }*/
                                    alctManager.alctUnLoad(uWaybill);
                                    refreshDatas();
                                } else {
                                    String error = object.getString("msg");
                                    Toast.makeText(getActivity(), "送达失败！" + error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(String msg) {
                            alctManager.alctUnLoad(uWaybill);
                            dialogFragment.dismissAllowingStateLoss();
                            Toast.makeText(getActivity(), "送达失败！", Toast.LENGTH_SHORT).show();
                        }
                    }));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void upLoadLocation(final String id) {
        if (dao == null) {
            return;
        }
        LocationEntity locationEntity = dao.findLocInfoById(id);
        if (locationEntity == null) {
            return;
        }
        String location = locationEntity.getLocation();
        Log.e("upLoadLocation", "location==" + location);
        //上传位置信息
        Map<String, String> map = new HashMap<>();
        map.put("id", id);
        map.put("location", location);
        RequestManager.getInstance()
                .mServiceStore
                .upLoadUWaybillLocation(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("upLoadUWaybillLocation", msg);

                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getBoolean("success")) {
                                if (dao != null) {
                                    dao.delLocInfo(id);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("upLoadUWaybillLocation", msg);
                    }
                }));
    }

    private void showCancelTips(String msg, final UWaybill uWaybill) {
        final ValidationCodeFragment codeView = ValidationCodeFragment.getInstance(msg);
        codeView.showF(getChildFragmentManager(), "showCancelTips");
        codeView.setOnValidationComplListener(new ValidationCodeFragment.OnValidationComplListener() {
            @Override
            public void onClickCancel() {
                codeView.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                doCancel(uWaybill);
            }
        });
    }

    private void doCancel(final UWaybill uWaybill) {
        Map<String, String> map = new HashMap<>();
        String userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        String userinfo = SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        try {
            JSONObject object = new JSONObject(userinfo);
            final String token = object.getString("token");
            if (TextUtils.isEmpty(token)) {
                Toast.makeText(getActivity(), "token为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return;
            }
            final LoadingDialogFragment dialogFragment = LoadingDialogFragment.getInstance();
            dialogFragment.showF(getChildFragmentManager(), "docancel");
            map.put("token", token);
            map.put("mobile", userid);
            map.put("id", uWaybill.getId() + "");
            map.put("status", WaybillStatus.QX_STATUS + "");
            RequestManager.getInstance()
                    .mServiceStore
                    .updateUWaybillStatuById(map)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                        @Override
                        public void onSuccess(String msg) {
                            Log.e("updateUWaybillStatu", msg);
                            dialogFragment.dismissAllowingStateLoss();
                            try {
                                JSONObject object = new JSONObject(msg);
                                if (object.getBoolean("success")) {
                                    Toast.makeText(getActivity(), "取消成功！", Toast.LENGTH_SHORT).show();
                                   /* if (bind != null) {
                                        bind.stopLocationService();
                                        if (dao != null) {
                                            dao.delLocInfo(uWaybill.getWaybillId());
                                        }
                                    }*/
                                    refreshDatas();
                                } else {
                                    String error = object.getString("msg");
                                    Toast.makeText(getActivity(), "取消失败！" + error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();

                            }
                        }

                        @Override
                        public void onError(String msg) {
                            dialogFragment.dismissAllowingStateLoss();
                            Toast.makeText(getActivity(), "取消失败！", Toast.LENGTH_SHORT).show();

                        }
                    }));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(psconnection);
        super.onDestroy();
        mCompositeDisposable.clear();
    }

    public static WWCFragment newInstance() {
        WWCFragment fragment = new WWCFragment();
        return fragment;
    }

    private void showTimeShortTips(String msg){
        final ExitDialogFragment exitDialogFragment=ExitDialogFragment.getInstance(msg);
        exitDialogFragment.showF(getChildFragmentManager(),"showTimeShortTips");
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
}
