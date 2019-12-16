package com.hykc.cityfreight.fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.BalanceDetailAdapter;
import com.hykc.cityfreight.entity.UDriver;
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

public class YTXInfoFragment extends BaseFragment  {
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
        getCashWithdrawal();
    }
    private void getCashWithdrawal(){
        Map<String,String> map=new HashMap<>();
        String userInfo= SharePreferenceUtil.getInstance(getActivity()).getUserinfo();
        if(!TextUtils.isEmpty(userInfo)){
            Gson gson=new Gson();
            UDriver uDriver= gson.fromJson(userInfo,UDriver.class);
            long id=uDriver.getId();
            map.put("driverId",id+"");
        }
        RequestManager.getInstance()
                .mServiceStore
                .selectCashWithdrawal(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("applyCashWithdrawal", msg);
                        boolean isSuccess = false;
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            isSuccess = jsonObject.getBoolean("success");
                            if (isSuccess) {
                                String str=jsonObject.getString("entity");
                                if(!"null".equals(str)){
                                    List<ZDriver> list=new ArrayList<>();
                                    Gson gson=new Gson();
                                    JSONArray array=new JSONArray(str);
                                    for (int i = 0; i <array.length() ; i++) {
                                        String string=array.getString(i);
                                        ZDriver zDriver=gson.fromJson(string,ZDriver.class);
                                        list.add(zDriver);
                                    }
                                    if(list.size()==0){
                                        return;
                                    }
                                    List<ZDriver> mList=new ArrayList<>();
                                    if(list.size()>5){
                                        for(int i=0;i<5;i++){
                                            mList.add(list.get(i));
                                        }
                                    }else {
                                        mList.addAll(list);
                                    }
                                    BalanceDetailAdapter adapter = new BalanceDetailAdapter(
                                            getActivity(), mList);
                                    listView.setAdapter(adapter);
                                }else {

                                }
                            }else {
                                Toast.makeText(getActivity(), "查询失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e("checkTokenTimeout", msg);
                    }
                }));
    }

    public static YTXInfoFragment newInstance(){
        return new YTXInfoFragment();
    }

}
