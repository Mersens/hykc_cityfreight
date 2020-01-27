package com.hykc.cityfreight.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.entity.BestSignAgreementEntity;
import com.hykc.cityfreight.entity.EventEntity;
import com.hykc.cityfreight.entity.UCardEntity;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.entity.UWaybill;
import com.hykc.cityfreight.entity.ZDriver;
import com.hykc.cityfreight.service.ServiceStore;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.RxBus;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.hykc.cityfreight.view.InputCodeDialog;
import com.hykc.cityfreight.view.InputPsdDialog;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.hykc.cityfreight.view.TXMoneyDialog;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TXInputMoneyActivity extends BaseActivity {
    private static final int SELECT_MONEY_ITEM_KEY=1001;
    private static final int UPLOAD_BANK_USER_KEY=1002;
    private EditText mEditMoney;
    private Button mBtnOk;
    private ImageView mImageType;
    private TextView mTextAccount;
    private TextView mTextType;
    private RelativeLayout mLayoutType;
    private RelativeLayout mLayoutMoney;
    private String userid;
    private TextView mTextHistory;
    protected UCardEntity entity;
    private double mYe;
    private UDriver uDriver=null;
    private ImageView mImgBack;
    List<UCardEntity> mCardList = new ArrayList<>();
    private ZDriver zDriver=null;
    List<ZDriver> mZDriverList = new ArrayList<>();
    private int selectIndex=0;
    private boolean isAddBankUser=false;
    private ImageView mImgYERight;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tx_inputmoney);
        init();
    }

    @Override
    public void init() {
        initViews();
        initEvent();
        initDatas();
    }

    private void initDatas() {
        if (!TextUtils.isEmpty(userid)) {
            String userinfo = SharePreferenceUtil.getInstance(this).getUserinfo();
            if(TextUtils.isEmpty(userinfo)){
                Toast.makeText(this, "用户信息为空，请重新登录", Toast.LENGTH_SHORT).show();
                return;
            }
            Gson gson=new Gson();
            uDriver= gson.fromJson(userinfo,UDriver.class);
            if(null==uDriver){
                Toast.makeText(this, "用户信息为空，请重新登录", Toast.LENGTH_SHORT).show();
                return;
            }
            getMoneyInfo(userid);
            getMyCardInfo(userid);
            // getTXHistory(user);
        }
    }

    private void initViews() {
        mTextHistory = findViewById(R.id.tv_history);
        mTextHistory.setVisibility(View.GONE);
        mEditMoney = findViewById(R.id.editMoney);
        mEditMoney.setFocusable(false);
        mEditMoney.setFocusableInTouchMode(false);
        mLayoutMoney=findViewById(R.id.rlaccount);
        mBtnOk = findViewById(R.id.btn_ok);
        mImageType = findViewById(R.id.img_type);
        mTextAccount = findViewById(R.id.tv_zh);
        mTextType = findViewById(R.id.tv_type);
        mLayoutType = findViewById(R.id.layout_type);
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        mImgBack=findViewById(R.id.img_back);
        mImgYERight=findViewById(R.id.tv_je_right);
    }

    private void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        mLayoutMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(userid)){
                    Toast.makeText(TXInputMoneyActivity.this, "司机手机号为空，请重新登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(TXInputMoneyActivity.this,SelectDriverMoneyActivity.class);
                intent.putExtra("userid",userid);
                startActivityForResult(intent,SELECT_MONEY_ITEM_KEY);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });
        mImgYERight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(userid)){
                    Toast.makeText(TXInputMoneyActivity.this, "司机手机号为空，请重新登录", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=new Intent(TXInputMoneyActivity.this,SelectDriverMoneyActivity.class);
                intent.putExtra("userid",userid);
                startActivityForResult(intent,SELECT_MONEY_ITEM_KEY);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        mLayoutType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCardList.size() == 0) {
                    Toast.makeText(TXInputMoneyActivity.this, "请添加银行卡信息",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                showViews();

            }
        });
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dotx();

            }
        });
    }

    private void dotx() {
        final String account=mTextAccount.getText().toString();
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "请选择账户！", Toast.LENGTH_SHORT).show();
            return;
        }
        final String type=mTextType.getText().toString();
        if (TextUtils.isEmpty(type)) {
            Toast.makeText(this, "请选择账户！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(null==zDriver){
            Toast.makeText(this, "请选择提现的金额！", Toast.LENGTH_SHORT).show();
            return;
        }
        String money = mEditMoney.getText().toString();
        if (TextUtils.isEmpty(money)) {
            Toast.makeText(this, "提现金额不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        double strToDoubleMoney = 0.00;
        try {
            strToDoubleMoney = Double.valueOf(money);
        } catch (NumberFormatException e) {
            return;
        }

        if (strToDoubleMoney == 0.00) {
            Toast.makeText(this, "提现金额不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (strToDoubleMoney > mYe) {
            Toast.makeText(this, "  请输入正确金额！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(null==entity){
            Toast.makeText(this, "银行卡信息为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        String bankUserName=entity.getName();
        String userName=uDriver.getDriverName();
        if(!userName.equals(bankUserName)){
            if(TextUtils.isEmpty(entity.getUseridentity_f_img())
                    || TextUtils.isEmpty(entity.getUseridentity_z_img())){
                String msg="非本人账户需要上传开户人的身份证信息！";
                showUpLoadBankUserImgView(msg);
                return;
            }
        }
        final String strMoney = String.format("%.2f", strToDoubleMoney);
        final InputPsdDialog inputPsdDialog = InputPsdDialog.getInstance();
        inputPsdDialog.show(getSupportFragmentManager(), "inputPsdDialog");
        inputPsdDialog.setOnOrderListener(new InputPsdDialog.OnOrderListener() {
            @Override
            public void onOrder(String psd) {
                inputPsdDialog.dismiss();
                String name=entity.getName();
                selectArgeInfoByRowid(strMoney, psd,account,type,name);
            }
        });
    }

    private void selectArgeInfoByRowid(final String strMoney, final String psd,
                                       final String account,final String type,final String name){
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(),"selectArgeView");
        Map<String,String> map=new HashMap<>();
        map.put("rowid",zDriver.getWaybillId());
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.selectAgreInfoByRowid(map);
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
                            checkUserIsSelf(object.getString("msg"),strMoney, psd,account,type,name);
                        }else {
                            String error=object.getString("msg");
                            Toast.makeText(TXInputMoneyActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("selectAgreInfoByRowid","selectAgreInfoByRowid=="+str);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogFragment.dismissAllowingStateLoss();
                Log.e("onFailure","onFailure=="+t.getMessage());
            }
        });
    }

    private void checkUserIsSelf(final String bestSigninfo,final String strMoney, final String psd,
                                 final String account,final String type,final String name){
        Gson gson=new Gson();
        BestSignAgreementEntity bestSignAgreementEntity=gson.fromJson(bestSigninfo,
                BestSignAgreementEntity.class);
        //兼容之前数据，允许userAccount，userName为空
        String userAccount=bestSignAgreementEntity.getBank_user_account();
        String userName=bestSignAgreementEntity.getBank_user_name();
        if(!TextUtils.isEmpty(userAccount) && !TextUtils.isEmpty(userName)){
            //不为空，进行签订银行卡对比
            String selectAccount=entity.getAccount();
            String selectName=entity.getName();
            //判断提现账户和签订合同的账户是否是同一个账户
            if(userAccount.equals(selectAccount) && userName.equals(selectName)){
                //执行提现
                uploadInfo(strMoney, psd,account,type,name);
            }else {
                //提示重新签订打款协议
              /*  String tips="选择提现的账户与接单时签署的银行卡账户不一致\n"+"需要进行重新签约！";
                showSignAgreAgain(tips,strMoney, psd,account,type,name);*/
                showCodeView(strMoney, psd,account,type,name);
            }
        }else {
            //之前老数据，忽略执行下一步提现
            uploadInfo(strMoney, psd,account,type,name);
        }

    }

    private void showCodeView(final String strMoney, final String psd,
                              final String account,final String type,final String name){
        final InputCodeDialog inputCodeDialog=InputCodeDialog.newInstance(strMoney);
        inputCodeDialog.showF(getSupportFragmentManager(),"InputCodeView");
        inputCodeDialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                inputCodeDialog.dismissAllowingStateLoss();
            }
            @Override
            public void onClickOk() {
                inputCodeDialog.dismissAllowingStateLoss();
                String tips="选择提现的账户与接单时签署的银行卡账户不一致\n"+"需要进行重新签约！";
                showSignAgreAgain(tips,strMoney, psd,account,type,name);
            }
        });

    }

    private void showSignAgreAgain(String msg,final String strMoney, final String psd,
                                   final String account,final String type,final String name){
        final ExitDialogFragment dialogFragment=ExitDialogFragment.getInstance(msg);
        dialogFragment.showF(getSupportFragmentManager(),"showSignAgreAgain");
        dialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialogFragment.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialogFragment.dismissAllowingStateLoss();
                //重新签约
                selectUWaybillById(strMoney, psd,account,type,name);
            }
        });
    }

    private void selectUWaybillById(final String strMoney, final String psd,
                                    final String account,final String type,final String name){

        final LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
        loadingDialogFragment.showF(getSupportFragmentManager(),"selectUWaybillByIdView");
        Map<String,String> map=new HashMap<>();
        map.put("waybillId",zDriver.getWaybillId());
        RequestManager.getInstance()
                .mServiceStore
                .selectUWaybillByWaybill(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("selectUWaybillByWaybill", msg);
                        loadingDialogFragment.dismissAllowingStateLoss();
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getBoolean("success")) {
                                String str = object.getString("entity");
                                Gson gson=new Gson();
                                UWaybill uWaybill=gson.fromJson(str,UWaybill.class);
                                uWaybill.setUserType(3);
                                uWaybill.setBank_user_name(entity.getAccount());
                                uWaybill.setBank_user_account(entity.getAccount());
                                uWaybill.setIsSelf(1);
                                submitBestSignInfo(uWaybill,strMoney, psd,account,type,name);
                            } else {
                                String error = object.getString("msg");
                                Toast.makeText(TXInputMoneyActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(TXInputMoneyActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("selectUWaybillByWaybill", msg);
                    }
                }));

    }
    private Map<String, String> complateParms(UWaybill uWaybill) {
        String userid = SharePreferenceUtil.getInstance(TXInputMoneyActivity.this).getUserId();
        String userinfo = SharePreferenceUtil.getInstance(TXInputMoneyActivity.this).getUserinfo();
        String driverName = "";
        String identityNo = "";
        try {
            JSONObject object = new JSONObject(userinfo);
            driverName = object.getString("driverName");
            if (TextUtils.isEmpty(driverName)) {
                Toast.makeText(TXInputMoneyActivity.this, "姓名为空，请重新登录！", Toast.LENGTH_SHORT).show();
                return null;
            }
            identityNo = object.getString("identityNo");
            if (TextUtils.isEmpty(identityNo)) {
                Toast.makeText(TXInputMoneyActivity.this, "身份证号为空，请重新登录！", Toast.LENGTH_SHORT).show();
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




    private void submitBestSignInfo(UWaybill uWaybill,final String strMoney, final String psd,
                                    final String account,final String type,final String name) {
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(),"submitBestSignInfoView");
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
                dialogFragment.dismissAllowingStateLoss();
                ResponseBody body = response.body();
                String str = null;
                try {
                    if (null != body) {
                        str = body.string();
                        if (!TextUtils.isEmpty(str)){
                            JSONObject object=new JSONObject(str);
                            if(object.getBoolean("success")){
                                uploadInfo(strMoney, psd,account,type,name);
                            }else {
                                String error=object.getString("message");
                                Toast.makeText(TXInputMoneyActivity.this, error,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("autoSignAgre", "autoSignAgre onResponse==" + str);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("autoSignAgre", "autoSignAgre onResponse==" + t.getMessage());
                dialogFragment.dismissAllowingStateLoss();
            }
        });
    }

    private void uploadInfo(String strMoney, final String psd,
                            String account,String type,String name) {

        Map<String,String> map=new HashMap<>();
        String userInfo=SharePreferenceUtil.getInstance(TXInputMoneyActivity.this).getUserinfo();
        if(!TextUtils.isEmpty(userInfo)){
            Gson gson=new Gson();
            UDriver uDriver= gson.fromJson(userInfo,UDriver.class);
            long id=uDriver.getId();
            map.put("userId",id+"");
        }
        map.put("type","1");
        String payType=null;
        if("支付宝".equals(type)){
            payType="1";
        }else if("微信".equals(type)){
            payType="2";
        }else {
            payType="3";
            map.put("bankName",entity.getAddress());
        }
        map.put("paytype",payType);
        map.put("account",account);
        map.put("fee",strMoney);
        map.put("name",name);
        map.put("waybillId",zDriver.getWaybillId());
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(),"txView");
        RequestManager.getInstance()
                .mServiceStore
                .applyCashWithdrawal(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        dialogFragment.dismissAllowingStateLoss();
                        Log.e("applyCashWithdrawal", msg);
                        boolean isSuccess = false;
                        try {
                            JSONObject jsonObject = new JSONObject(msg);
                            isSuccess = jsonObject.getBoolean("success");
                            if (isSuccess) {
                                RxBus.getInstance().send(new EventEntity("1001","getMoneyInfo"));
                                Toast.makeText(TXInputMoneyActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                                finish();
                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                            }else {
                                String error=jsonObject.getString("msg");
                                Toast.makeText(TXInputMoneyActivity.this, "提交失败!"+error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        dialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(TXInputMoneyActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("applyCashWithdrawal", msg);
                    }
                }));
    }

    private void showViews() {
        TXMoneyDialog txDialog = TXMoneyDialog.getInstance(mCardList);
        txDialog.show(getSupportFragmentManager(), "txDialog");
        txDialog.setOnSelectListener(new TXMoneyDialog.OnSelectListener() {
            @Override
            public void onSelect(int pos, UCardEntity uCardEntity) {
                selectIndex=pos;
                entity=uCardEntity;
                setCardInfo(entity);
            }
        });
    }

    private void getMoneyInfo(final String mobile) {
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
                        Log.e(" getMoneyInfo onSuccess", "====" + msg);
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getBoolean("success")) {
                                String entity = object.getString("entity");
                                JSONArray array=new JSONArray(entity);
                                if(array.length()==0){
                                    //不存在可用运费金额
                                    String emptyMsg="不存在可用运费金额"+"\n"+"不能进行提现";
                                    showMoneyInfoEmpty(emptyMsg);
                                    return;
                                }else {
                                    Gson gson = new Gson();
                                    for (int i = 0; i <array.length() ; i++) {
                                        String string=array.getString(i);
                                        ZDriver driver = gson.fromJson(string, ZDriver.class);
                                        mZDriverList.add(driver);
                                    }
                                }
                                zDriver=mZDriverList.get(0);
                                mYe = zDriver.getSurplusPrice();
                                mEditMoney.setText(mYe+"");
                            } else {
                                String errorMsg = object.getString("msg");
                                Toast.makeText(TXInputMoneyActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e("onError", "====" + msg);
                        Toast.makeText(TXInputMoneyActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void showMoneyInfoEmpty(String msg) {

        final ExitDialogFragment exitDialogFragment=ExitDialogFragment.getInstance(msg);
        exitDialogFragment.showF(getSupportFragmentManager(),"showMoneyInfoEmpty");
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

    private void getMyCardInfo(final String id) {
final LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
if(isAddBankUser){
    loadingDialogFragment.showF(getSupportFragmentManager(),"BankUserImgView");
}
        Map<String, String> map = new HashMap<>();
        map.put("mobile", id);
        RequestManager.getInstance()
                .mServiceStore
                .selectCardInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                        if(isAddBankUser){
                            loadingDialogFragment.dismissAllowingStateLoss();
                        }
                        isAddBankUser=false;
                        Log.e("onSuccess getMyCardInfo", "====" + msg);
                        try {
                            JSONObject object = new JSONObject(msg);
                            if (object.getBoolean("success")) {
                                analysisJson(object.getString("entity"));
                            } else {
                                String error = object.getString("msg");
                                Toast.makeText(TXInputMoneyActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        if(isAddBankUser){
                            loadingDialogFragment.dismissAllowingStateLoss();
                        }
                        isAddBankUser=false;
                        Toast.makeText(TXInputMoneyActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);
                    }
                }));
    }

    private void analysisJson(String str) {
        try {
            JSONArray array = new JSONArray(str);
            if (array.length() == 0) {
                Toast.makeText(this, "请添加银行卡信息", Toast.LENGTH_SHORT).show();
                return;
            }
            mCardList.clear();
            for (int i = 0; i < array.length(); i++) {
                String string = array.getString(i);
                Gson gson = new Gson();
                UCardEntity entity = gson.fromJson(string, UCardEntity.class);
                mCardList.add(entity);
            }

            setDefaultCardInfo(mCardList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDefaultCardInfo(List<UCardEntity> mCardList) {
        if (mCardList.size() > 0) {
            entity=mCardList.get(selectIndex);
            setCardInfo(entity);
        } else {
            Toast.makeText(this, "请添加银行卡！", Toast.LENGTH_SHORT).show();
        }
    }

    private void setCardInfo(UCardEntity entity) {
        int type = entity.getCardType();
        if (type == 1) {
            mImageType.setImageResource(R.mipmap.icon_zfb);
            mTextType.setText(entity.getName());
            mTextAccount.setText(entity.getAccount());
        } else if (type == 2) {
            mImageType.setImageResource(R.mipmap.icon_wx);
            mTextType.setText(entity.getName());
            mTextAccount.setText(entity.getAccount());
        } else if (type == 3) {
            mImageType.setImageResource(R.mipmap.icon_yhk);
            mTextType.setText(entity.getBank());
            mTextAccount.setText(entity.getAccount());
        }
        String bankUserName=entity.getName();
        String userName=uDriver.getDriverName();
        if(!userName.equals(bankUserName)){
            if(TextUtils.isEmpty(entity.getUseridentity_f_img())
                    || TextUtils.isEmpty(entity.getUseridentity_z_img())){
                String msg="非本人账户需要上传开户人的身份证信息！";
                showUpLoadBankUserImgView(msg);
            }
        }
    }

    private void showUpLoadBankUserImgView(String msg){
        final ExitDialogFragment exitDialogFragment=ExitDialogFragment.getInstance(msg);
        exitDialogFragment.showF(getSupportFragmentManager(),"BankUserImgView");
        exitDialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                exitDialogFragment.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                exitDialogFragment.dismissAllowingStateLoss();
                Intent intent=new Intent(TXInputMoneyActivity.this,
                        UpLoadBankUserImgActivity.class);
                intent.putExtra("entity",entity);
                intent.putExtra("index",selectIndex);
                startActivityForResult(intent,UPLOAD_BANK_USER_KEY);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECT_MONEY_ITEM_KEY){
            if(null!=data){
                if(data.hasExtra("entity")){
                    zDriver= (ZDriver) data.getSerializableExtra("entity");
                    mYe = zDriver.getSurplusPrice();
                    mEditMoney.setText(mYe+"");
                }
            }
        }else if(requestCode==UPLOAD_BANK_USER_KEY){
            if(null!=data){
                if(data.hasExtra("entity")){
                   // entity= (UCardEntity) data.getSerializableExtra("entity");
                    selectIndex=data.getIntExtra("index",0);
                    Log.e("selectIndex===","selectIndex==="+selectIndex);
                    //setCardInfo(entity);
                    isAddBankUser=true;
                    userid=SharePreferenceUtil.getInstance(TXInputMoneyActivity.this).getUserId();
                    if(!TextUtils.isEmpty(userid)){
                        getMyCardInfo(userid);
                    }
                }
            }
        }
    }
}
