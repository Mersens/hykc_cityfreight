package com.hykc.cityfreight.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.hykc.cityfreight.R;

/**
 * Created by Administrator on 2018/3/22.
 */

public class ExitDialogFragment extends DialogFragment {
    private TextView mTextOk;
    private TextView mTextCancel;
    private TextView mTextMsg;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.NoticeDialogStyle);

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return inflater.inflate(R.layout.layout_exit,container,true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mTextMsg=(TextView)view.findViewById(R.id.tv_msg);
        Bundle bundle=getArguments();
        if(bundle!=null){
            String msg=bundle.getString("msg");
            if(!TextUtils.isEmpty(msg)){
                mTextMsg.setText(msg);
            }
        }

        mTextOk=(TextView)view.findViewById(R.id.tv_ok);
        mTextCancel=(TextView)view.findViewById(R.id.tv_cancel);
        mTextOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onClickOk();

                }

            }
        });
        mTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onClickCancel();

                }

            }
        });

    }

    public static ExitDialogFragment getInstance(String msg){
        ExitDialogFragment fragment=new ExitDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putString("msg",msg);
        fragment.setArguments(bundle);
        return fragment;

    }

    public void showF(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    private OnDialogClickListener listener;
    public void setOnDialogClickListener(OnDialogClickListener listener){
        this.listener=listener;
    }

    public interface OnDialogClickListener {
        void onClickCancel();
        void onClickOk();

    }

}
