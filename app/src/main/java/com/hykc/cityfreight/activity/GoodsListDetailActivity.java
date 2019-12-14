package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.entity.UWaybill;
import com.hykc.cityfreight.service.ServiceStore;
import com.hykc.cityfreight.utils.SharePreferenceUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GoodsListDetailActivity extends BaseActivity {
    private ImageView mImgBack;
    private UWaybill uWaybill;
    private TextView mTextShrName;
    private TextView mTextShrTel;
    private TextView mTextFhrName;
    private TextView mTextFhrTel;
    private TextView mTextFromAddr;
    private TextView mTextToAddr;
    private TextView mTextHth;
    private TextView mTextHwmc;
    private TextView mTextHwsl;
    private TextView mTextHwfl;
    private TextView mTextDj;
    private TextView mTextSzgs;
    private TextView mTextHwms;
    private TextView mTextZz;
    private TextView mTextCc;
    private TextView mTextYwlx;
    private TextView mTextBz;
    private RelativeLayout mLayoutAgre;

    private TextView mTextDriverName;
    private TextView mTextCardNum;
    private TextView mTextCph;
    private TextView mTextKHM;
    private TextView mTextDunwei;
    private String argeUrl;
    private TextView mTextTel;
    private TextView mTextDriverPrice;

    private TextView mTextCC;
    private TextView mTextZZ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_goods_details);
        init();
    }

    @Override
    public void init() {
        uWaybill= (UWaybill) getIntent().getSerializableExtra("entity");
        initView();
        initEvent();
        initData();

    }


    private void initData() {
        if(uWaybill==null){
            return;
        }

        mTextTel=findViewById(R.id.tv_tel);
        mTextShrName.setText(uWaybill.getConsigneeName());
        mTextShrTel.setText(uWaybill.getConsigneePhone());
        mTextFhrName.setText(uWaybill.getShipperName());
        mTextFhrTel.setText(uWaybill.getShipperPhone());
        mTextFromAddr.setText(uWaybill.getFromLocation());
        mTextToAddr.setText(uWaybill.getToLocation());
        mTextHth.setText(uWaybill.getContractNum());
        mTextHwmc.setText(uWaybill.getGoodsName());
        mTextHwsl.setText(uWaybill.getUcreditId()+"");
        mTextHwfl.setText(uWaybill.getGoodName());

        double price=uWaybill.getOilPrice()+uWaybill.getDriverPrice();
        double ucreditId=uWaybill.getUcreditId();
        double d=price/ucreditId;
        String strMoney = String.format("%.2f", d);
        String dw = uWaybill.getTaskUnit();
        if(TextUtils.isEmpty(dw)){
            dw="";
        }
        int isDriverShowPrice=uWaybill.getIsDriverShowPrice();
        if(isDriverShowPrice==0){
            //显示运费
            mTextDriverPrice.setText(uWaybill.getDriverPrice()+"元");
            mTextDj.setText(strMoney+"元/"+dw);
        }else {
            //不显示运费
            mTextDj.setText("****元/"+dw);
            mTextDriverPrice.setText("****元");
        }

        mTextSzgs.setText(uWaybill.getCompanyName());
        mTextHwms.setText(uWaybill.getGoodsDescribe());
        mTextZz.setText("");
        mTextCc.setText("");
        mTextYwlx.setText("");
        mTextBz.setText(uWaybill.getRemarks());

        String userid=SharePreferenceUtil.getInstance(this).getUserId();
        if(!TextUtils.isEmpty(userid)){
            getArgeUrl(userid,uWaybill.getWaybillId());
        }
        mTextTel.setText(uWaybill.getDriverMobile());
        mTextDriverName.setText(uWaybill.getDriverName());
        mTextCardNum.setText(uWaybill.getDriverCardId());
        mTextCph.setText(uWaybill.getCarNumber());
        mTextKHM.setText(uWaybill.getCustomerName());
        mTextDunwei.setText(uWaybill.getUcreditId()+"");


        String carLength=uWaybill.getCarLength();
        String carLoad=uWaybill.getCarLoad();
        if(!TextUtils.isEmpty(carLength)){
            mTextCC.setText(uWaybill.getCarLength()+" 米");
        }
        if(!TextUtils.isEmpty(carLoad)){
            mTextZZ.setText(uWaybill.getCarLoad()+" 吨");
        }
    }


    private void getArgeUrl(String userid, String waybillId) {
        Map<String,String> map=new HashMap<>();
        map.put("account",userid);
        map.put("rowid",waybillId);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.checkAgreByRowid(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body=response.body();
                String str= null;
                try {
                    if(null!=body){
                        str = body.string();
                        JSONObject object=new JSONObject(str);
                        boolean success=object.getBoolean("success");
                        if(success){
                            argeUrl=object.getString("message");
                            mLayoutAgre.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("getArgeUrl","getArgeUrl=="+str);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure","onFailure=="+t.getMessage());
            }
        });

    }

    private void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        mLayoutAgre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(argeUrl)){
                    Toast.makeText(GoodsListDetailActivity.this,
                            "合同不存在！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(GoodsListDetailActivity.this, CheckAgreActivity.class);
                intent.putExtra("url",argeUrl);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    private void initView() {
        mLayoutAgre=findViewById(R.id.layout_agre);
        mImgBack=findViewById(R.id.img_back);
        mTextShrName=findViewById(R.id.tv_shrname);
        mTextShrTel=findViewById(R.id.tv_shrdh);
        mTextFhrName=findViewById(R.id.tv_fhrname);
        mTextFhrTel=findViewById(R.id.tv_fhrdh);
        mTextFromAddr=findViewById(R.id.tv_from_addr);
        mTextToAddr=findViewById(R.id.tv_to_addr);
        mTextHth=findViewById(R.id.tv_hth);
        mTextHwmc=findViewById(R.id.tv_hwmc);
        mTextHwsl=findViewById(R.id.tv_hwsl);
        mTextHwfl=findViewById(R.id.tv_hwfl);
        mTextDj=findViewById(R.id.tv_dj);
        mTextSzgs=findViewById(R.id.tv_szgs);
        mTextHwms=findViewById(R.id.tv_hwms);
        mTextZz=findViewById(R.id.tv_clzz);
        mTextCc=findViewById(R.id.tv_ccyq);
        mTextYwlx=findViewById(R.id.tv_ywlx);
        mTextBz=findViewById(R.id.tv_bz);

        mTextDriverName=findViewById(R.id.tv_drivername);
        mTextCardNum=findViewById(R.id.tv_cardnum);
        mTextCph=findViewById(R.id.tv_cph);
        mTextKHM=findViewById(R.id.tv_khm);
        mTextDunwei=findViewById(R.id.tv_dunwei);
        mTextDriverPrice=findViewById(R.id.tv_driver_price);
        mTextCC=findViewById(R.id.tv_cc);
        mTextZZ=findViewById(R.id.tv_zz);
    }
}
