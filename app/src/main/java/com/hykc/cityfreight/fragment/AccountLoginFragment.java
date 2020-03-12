package com.hykc.cityfreight.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.activity.AgreementActivity;
import com.hykc.cityfreight.activity.MainActivity;
import com.hykc.cityfreight.app.AlctConstants;
import com.hykc.cityfreight.entity.AlctEntity;
import com.hykc.cityfreight.entity.UDriver;
import com.hykc.cityfreight.entity.UWaybill;
import com.hykc.cityfreight.utils.AlctManager;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;
import com.hykc.cityfreight.view.AcceptAgreDialog;
import com.hykc.cityfreight.view.LoadingDialogFragment;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class AccountLoginFragment extends BaseFragment {
    private EditText mEditTel;
    private EditText mEditPsd;
    private Button mBtnLogin;
    private RelativeLayout mLayoutXY;
    private CheckBox checkBox;
    private boolean isPrepared;

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_account_login;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isPrepared &&isVisibleToUser) {
            boolean isAccept=SharePreferenceUtil.getInstance(getActivity()).getAcceptAgre();
            if(isAccept){
                checkBox.setChecked(true);
            }else {
                checkBox.setChecked(false);
            }

        }
    }

    @Override
    protected void initView(View view) {
        isPrepared = true;

        mEditTel = view.findViewById(R.id.editPhone);
        mEditPsd = view.findViewById(R.id.editPass);
        mBtnLogin = view.findViewById(R.id.btnlogin);
        mLayoutXY = view.findViewById(R.id.layout_xy);
        checkBox = view.findViewById(R.id.checkBox);
        boolean isAccept=SharePreferenceUtil.getInstance(getActivity()).getAcceptAgre();
        if(isAccept){
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();

            }
        });
        mLayoutXY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheckXY();
            }
        });
    }

    private void doCheckXY() {
        /*Intent intentFind = new Intent(getActivity(), AgreementActivity.class);
        startActivity(intentFind);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
*/

        showAcceptAgreView();

    }

    private void showAcceptAgreView() {
        final AcceptAgreDialog acceptAgreDialog = AcceptAgreDialog.newInstance();
        acceptAgreDialog.showF(getChildFragmentManager(), "AccountAgre");
        acceptAgreDialog.setOnSelectListener(new AcceptAgreDialog.OnSelectListener() {
            @Override
            public void onSelect() {
                acceptAgreDialog.dismissAllowingStateLoss();
                SharePreferenceUtil.getInstance(getActivity()).setAcceptAgre(true);
                checkBox.setChecked(true);
            }

            @Override
            public void onCancel() {
                acceptAgreDialog.dismissAllowingStateLoss();
                SharePreferenceUtil.getInstance(getActivity()).setAcceptAgre(false);
                checkBox.setChecked(false);
            }
        });

    }

    @Override
    protected void initData() {

    }

    private void doLogin() {
        if (!checkBox.isChecked()) {
            Toast.makeText(getActivity(), "请阅读和接受货运快车服务协议！", Toast.LENGTH_SHORT).show();
            return;
        }
        final String tel = mEditTel.getText().toString();
        final String psd = mEditPsd.getText().toString();
        if (TextUtils.isEmpty(tel)) {
            Toast.makeText(getActivity(), "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tel.length() != 11) {
            Toast.makeText(getActivity(), "请输入正确手机号！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(psd)) {
            Toast.makeText(getActivity(), "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        login(tel, psd);
    }

    private void login(final String tel, final String psd) {
        final LoadingDialogFragment dialogFragment = LoadingDialogFragment.getInstance();
        dialogFragment.showF(getChildFragmentManager(), "accountloading");
        Map<String, String> map = new HashMap<>();
        map.put("mobile", tel);
        map.put("pwd", psd);
        RequestManager.getInstance()
                .mServiceStore
                .login(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        dialogFragment.dismissAllowingStateLoss();
                        Log.e("login onSuccess", "login==" + msg);
                        if (!TextUtils.isEmpty(msg)) {
                            try {
                                JSONObject object = new JSONObject(msg);
                                if (object.getBoolean("success")) {
                                    String userInfo = object.getString("entity");
                                    SharePreferenceUtil.getInstance(getActivity()).setUserinfo(userInfo);
                                    SharePreferenceUtil.getInstance(getActivity()).setUserId(tel);
                                    JSONObject jsonObject = new JSONObject(userInfo);
                                    String identityNo = jsonObject.getString("identityNo");
                                    if (TextUtils.isEmpty(identityNo)) {
                                        Toast.makeText(getActivity(), "登录成功！", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(), MainActivity.class));
                                        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                        getActivity().finish();
                                        return;
                                    }
                                    Gson gson = new Gson();
                                    UDriver uDriver = gson.fromJson(userInfo, UDriver.class);
                                    JSONArray jsonArray = new JSONArray(uDriver.getAlct());
                                    JSONArray array = new JSONArray();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        AlctEntity entity = gson.fromJson(jsonArray.getString(i), AlctEntity.class);
                                        JSONObject obj = new JSONObject();
                                        obj.put("alctkey", entity.getAlctSecret());
                                        obj.put("alctid", entity.getAlctKey());
                                        obj.put("alctcode", entity.getAlctCode());
                                        array.put(obj);
                                    }
                                    AlctManager alctManager = AlctManager.newInstance();
                                    alctManager.setOnAlctResultListener(new MyAlctListener());
                                    alctManager.alctRegister(array.toString(), identityNo);

                                } else {
                                    Toast.makeText(getActivity(), "登录失败！", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), "登录失败！", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        dialogFragment.dismissAllowingStateLoss();
                        Log.e("login onError", msg);
                        Toast.makeText(getActivity(), "登录失败！", Toast.LENGTH_SHORT).show();
                    }
                }));


    }

    public void reset() {
        mEditPsd.setText("");
        mEditTel.setText("");

    }

    class MyAlctListener implements AlctManager.OnAlctResultListener {
        @Override
        public void onSuccess(int type, UWaybill uWaybill) {
            if (type == AlctConstants.REGISTER_SUCCESS) {
                Toast.makeText(getActivity(), "登录成功！", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                getActivity().finish();
            }


        }

        @Override
        public void onError(int type, UWaybill uWaybill, String msg) {
            if (type == AlctConstants.REGISTER_ERROR) {
                Toast.makeText(getActivity(), "登录成功！", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                getActivity().finish();
            }

        }
    }

    public static AccountLoginFragment newInstance() {
        return new AccountLoginFragment();
    }
}
