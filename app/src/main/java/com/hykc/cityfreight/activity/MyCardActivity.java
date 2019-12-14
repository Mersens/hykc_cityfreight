package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.MyCardAdapter;
import com.hykc.cityfreight.entity.UCardEntity;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.hykc.cityfreight.view.SelectCardTypeFragment;
import com.hykc.cityfreight.view.ZFB_WXBandDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MyCardActivity extends BaseActivity {
    private ListView mListView;
    private RelativeLayout mLayoutNo;
    private MyCardAdapter adapter;
    private String userid;
    private TextView mTextAdd;
    private ImageView mImgBack;
    private List<UCardEntity> mCardList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_card_list);
        init();
    }

    @Override
    public void init() {
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        mImgBack=findViewById(R.id.img_back);
        mTextAdd=findViewById(R.id.tv_add);
        mLayoutNo=findViewById(R.id.layout_card_nomsg);
        mListView=findViewById(R.id.cardListView);
        adapter = new MyCardAdapter(MyCardActivity.this, mCardList);
        mListView.setAdapter(adapter);
        initEvent();

    }

    private void initData() {
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        if (!TextUtils.isEmpty(userid)) {
            getMyCardInfo(userid);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initEvent() {
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
                showSelectTypeView();
            }
        });
        adapter.setOnCardItemClickListener(new MyCardAdapter.OnCardItemClickListener() {
            @Override
            public void onCardItemClick(int pos, UCardEntity entity) {

            }

            @Override
            public void onCardDelClick(int pos, UCardEntity entity) {
                cardDel(pos,entity);
            }

        });
    }


    private void showSelectTypeView(){
        final SelectCardTypeFragment fragment=SelectCardTypeFragment.newInstance();
        fragment.show(getSupportFragmentManager(),"selectcardtype");
        fragment.setOnItemSelectListener(new SelectCardTypeFragment.onItemSelectListener() {
            @Override
            public void onItemSelect(int type) {
                bandType(type);
            }
        });
    }

    private void bandType(int i) {
        switch (i) {
            case 1:
                BandZFB(1);
                break;
            case 2:
                BandWX(2);
                break;
            case 3:
                BandYHK();
                break;
        }

    }








    private void BandYHK() {
       /* final BankBandDialog yhkDialog = BankBandDialog.getInstance(3);
        yhkDialog.show(getSupportFragmentManager(), "yhkDialog");
        yhkDialog.setOnOrderListener(new BankBandDialog.OnOrderListener() {
            @Override
            public void onOrder(String psd, String accountl, String bank, String address) {
                yhkDialog.dismiss();
                uploadBankBandInfo(true,psd, accountl, bank, address);
            }
        });*/

        Intent intent=new Intent(MyCardActivity.this,AddBankActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void BandZFB(int i) {
        final ZFB_WXBandDialog zfbDialog = ZFB_WXBandDialog.getInstance(i);
        zfbDialog.show(getSupportFragmentManager(), "zfbDialog");
        zfbDialog.setOnOrderListener(new ZFB_WXBandDialog.OnOrderListener() {
            @Override
            public void onOrder(String name, String account) {
                zfbDialog.dismiss();
                uploadZFBOrWXBandInfo(true,1, name, account);
            }
        });

    }

    private void BandWX(int i) {
        final ZFB_WXBandDialog wxDialog = ZFB_WXBandDialog.getInstance(i);
        wxDialog.show(getSupportFragmentManager(), "wxDialog");
        wxDialog.setOnOrderListener(new ZFB_WXBandDialog.OnOrderListener() {
            @Override
            public void onOrder(String name, String account) {
                wxDialog.dismiss();
                uploadZFBOrWXBandInfo(true,2, name, account);
            }
        });
    }

    private void uploadBankBandInfo(boolean isAdd,String name, String account, String bank, String address) {
        String type = "银行卡";
        final LoadingDialogFragment loadingDialog = LoadingDialogFragment.getInstance();
        loadingDialog.show(getSupportFragmentManager(), "ZFBOrWXBandInfo");
        Map<String,String> map=new HashMap<>();
        map.put("cardType","3");
        map.put("name",type);
        map.put("account",account);
        map.put("mobile",userid);
        map.put("address",address);
        map.put("bank",bank);
        RequestManager.getInstance()
                .mServiceStore
                .addCardInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialog.dismiss();
                        Log.e("uploadCardInfo", "====" + msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object=new JSONObject(msg);
                            if(object.getBoolean("success")){
                                Toast.makeText(MyCardActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                initData();
                            }else {
                                String error=object.getString("msg");
                                Toast.makeText(MyCardActivity.this, error, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(MyCardActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);
                        loadingDialog.dismiss();
                    }
                }));
    }

    private void uploadZFBOrWXBandInfo(final boolean isAdd, int type, String name, String account) {
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        loadingDialogFragment.show(getSupportFragmentManager(), "ZFBOrWXBandInfo");
        if (TextUtils.isEmpty(userid)) {
            return;

        }
        Map<String,String> map=new HashMap<>();
        String t = "";
        int cardType=type;
        if (type == 1) {
            t = "支付宝";


        } else if (type == 2) {
            t = "微信";
        }
        if (TextUtils.isEmpty(t)) {
            return;
        }
        map.put("cardType",cardType+"");
        map.put("name",t);
        map.put("account",account);
        map.put("mobile",userid);
        RequestManager.getInstance()
                .mServiceStore
                .addCardInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismiss();
                        Log.e("uploadCardInfo", "====" + msg);
                        try {
                            JSONObject object=new JSONObject(msg);
                            if(object.getBoolean("success")){
                                Toast.makeText(MyCardActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                initData();
                            }else {
                                String error=object.getString("msg");
                                Toast.makeText(MyCardActivity.this, error, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(MyCardActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);
                        loadingDialogFragment.dismiss();
                    }
                }));
    }





    private void getMyCardInfo(final String userid) {

        final LoadingDialogFragment selectDialogFragment = LoadingDialogFragment.getInstance();
        selectDialogFragment.show(getSupportFragmentManager(), "selectDialogFragment");
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
                        selectDialogFragment.dismiss();
                        Log.e("onSuccess getMyCardInfo", "====" + msg);
                        try {
                            JSONObject object=new JSONObject(msg);
                            if(object.getBoolean("success")){
                                analysisJson(object.getString("entity"));
                            }else {
                                mLayoutNo.setVisibility(View.VISIBLE);
                                String error=object.getString("msg");
                                Toast.makeText(MyCardActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(MyCardActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);
                        selectDialogFragment.dismiss();

                    }
                }));
    }

    private void analysisJson(String str) {
        mCardList.clear();
        try {
            JSONArray array = new JSONArray(str);
            if(array.length()==0){
                mLayoutNo.setVisibility(View.VISIBLE);
                return;
            }
            mLayoutNo.setVisibility(View.GONE);
            for (int i = 0; i < array.length(); i++) {
                String string=array.getString(i);
                Gson gson=new Gson();
                UCardEntity entity=gson.fromJson(string,UCardEntity.class);
                mCardList.add(entity);
            }

            adapter.setList(mCardList);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void cardDel(int pos, UCardEntity entity){
        final LoadingDialogFragment loadingDialog = LoadingDialogFragment.getInstance();
        loadingDialog.show(getSupportFragmentManager(), "ZFBOrWXBandInfo");
        Map<String,String> map=new HashMap<>();
        map.put("id",entity.getId()+"");
        RequestManager.getInstance()
                .mServiceStore
                .delCardInfoById(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialog.dismiss();
                        Log.e("delCardInfoById", "====" + msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object=new JSONObject(msg);
                            if(object.getBoolean("success")){
                                Toast.makeText(MyCardActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                initData();
                            }else {
                                String error=object.getString("msg");
                                Toast.makeText(MyCardActivity.this, error, Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        Toast.makeText(MyCardActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);
                        loadingDialog.dismiss();
                    }
                }));
    }

}
