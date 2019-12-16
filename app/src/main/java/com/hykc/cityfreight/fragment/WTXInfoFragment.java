package com.hykc.cityfreight.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.BalanceDetailAdapter;
import com.hykc.cityfreight.adapter.WTXBalanceDetailAdapter;
import com.hykc.cityfreight.entity.ZDriver;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.ScrollListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class WTXInfoFragment extends BaseFragment  {
    private ScrollListView listView;
    @Override
    protected int getLayoutResource() {
        return R.layout.layout_tx_main;
    }

    @Override
    protected void initView(View view) {
        listView=view.findViewById(R.id.listView);
    }

    @Override
    protected void initData() {
        String userid= SharePreferenceUtil.getInstance(getActivity()).getUserId();
        if(TextUtils.isEmpty(userid)){
            Toast.makeText(getActivity(), "司机手机号为空，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        getMoneyInfo(userid);
    }

    private void getMoneyInfo(String userid) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", userid);
        RequestManager.getInstance()
                .mServiceStore
                .getDriverOrderInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                        Log.e(" getMoneyInfo onSuccess", "====" + msg);
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getBoolean("success")) {
                                String entity = object.getString("entity");
                                List<ZDriver> mZDriverList=new ArrayList<>();
                                JSONArray array=new JSONArray(entity);
                                    Gson gson = new Gson();
                                    for (int i = 0; i <array.length() ; i++) {
                                        String string=array.getString(i);
                                        ZDriver driver = gson.fromJson(string, ZDriver.class);
                                        mZDriverList.add(driver);
                                    }
                                    //设置数据源
                                List<ZDriver> mList=new ArrayList<>();
                                    if(mZDriverList.size()>5){
                                        for (int i = 0; i <5 ; i++) {
                                            mList.add(mZDriverList.get(i));
                                        }

                                    }else {
                                        mList.addAll(mZDriverList) ;
                                    }
                                WTXBalanceDetailAdapter adapter = new WTXBalanceDetailAdapter(
                                        getActivity(), mList);
                                listView.setAdapter(adapter);

                            } else {
                                String errorMsg = object.getString("msg");
                                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e("onError", "====" + msg);
                        Toast.makeText(getActivity(), "查询失败！", Toast.LENGTH_SHORT).show();
                    }
                }));




    }

    public static WTXInfoFragment newInstance(){
        return new WTXInfoFragment();
    }

}
