package com.hykc.cityfreight.view;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.service.RegisterCodeTimerService;
import com.hykc.cityfreight.utils.RegisterCodeTimer;
import com.hykc.cityfreight.utils.RequestManager;
import com.hykc.cityfreight.utils.ResultObserver;
import com.hykc.cityfreight.utils.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class InputCodeDialog extends DialogFragment {
    private TextView mTextTel;
    private TextView mTextMoney;
    private ImageView mImgClose;
    private CodeEditText mEdit;
    private TextView mTextGetCode;
    private Button mBtn;
    private String  userid;
    private String mobile = null;
    private String chkCode = null;
    private String inputCode=null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoticeDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return inflater.inflate(R.layout.layout_input_code, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {

        mTextTel=view.findViewById(R.id.tv_mobile);
        mTextMoney=view.findViewById(R.id.tv_money);
        mImgClose=view.findViewById(R.id.img_close);
        mEdit=view.findViewById(R.id.codeEdit);
        mTextGetCode=view.findViewById(R.id.tv_getCode);
        mBtn=view.findViewById(R.id.btn_ok);
        RegisterCodeTimerService.setHandler(mCodeHandler);
        userid= SharePreferenceUtil.getInstance(getActivity()).getUserId();
        if(TextUtils.isEmpty(userid)){
            Toast.makeText(getActivity(), "司机手机号为空！请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        mTextTel.setText(userid);
        String money=getArguments().getString("money");
        mTextMoney.setText(money+" 元");
        mImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onClickCancel();
                }
            }
        });
        mEdit.setOnTextFinishListener(new CodeEditText.OnTextFinishListener() {
            @Override
            public void onTextFinish(CharSequence text, int length) {
                inputCode=text.toString().trim();
                Log.e("onTextFinish","onTextFinish==="+text);;

            }
        });
        mTextGetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doGetCode();
            }
        });
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(inputCode)){
                    Toast.makeText(getActivity(), "请输入验证码！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!inputCode.equals(chkCode)){
                    Toast.makeText(getActivity(), "验证码错误！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(listener!=null){
                    listener.onClickOk();
                }
            }
        });
    }

    private void doGetCode() {
        getActivity().startService(new Intent(getActivity(),
                RegisterCodeTimerService.class));
        mTextGetCode.setEnabled(false);
        Map<String,String> map=new HashMap<>();
        map.put("mobile",userid);
        RequestManager.getInstance()
                .mServiceStore
                .getTxSqSms(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("getSms onSuccess", "getSms=="+msg);
                        if(!TextUtils.isEmpty(msg)){
                            try {
                                JSONObject object=new JSONObject(msg);
                                chkCode=object.getString("sms");
                                mobile=object.getString("mobile");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Log.e("getSms onError", msg);
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("getSms onError", msg);
                    }
                }));


    }

    public static InputCodeDialog newInstance(String money) {
        InputCodeDialog inputCodeDialog = new InputCodeDialog();
        Bundle bundle=new Bundle();
        bundle.putString("money",money);
        inputCodeDialog.setArguments(bundle);
        return inputCodeDialog;
    }


    public void showF(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    private ExitDialogFragment.OnDialogClickListener listener;

    public void setOnDialogClickListener(ExitDialogFragment.OnDialogClickListener listener) {
        this.listener = listener;
    }

    public interface OnDialogClickListener {
        void onClickCancel();

        void onClickOk();

    }
    Handler mCodeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == RegisterCodeTimer.IN_RUNNING) {// 正在倒计时
                mTextGetCode.setText(msg.obj.toString());
                mTextGetCode.setEnabled(false);
            } else if (msg.what == RegisterCodeTimer.END_RUNNING) {// 完成倒计时
                mTextGetCode.setEnabled(true);
                mTextGetCode.setText("获取验证码");
            }
        }

        ;
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimerService();
        mCodeHandler.removeCallbacksAndMessages(null);
    }


    public  void stopTimerService(){
        mTextGetCode.setEnabled(true);
        mTextGetCode.setText("获取验证码");
        getActivity().stopService(new Intent(getActivity(),
                RegisterCodeTimerService.class));
    }
}
