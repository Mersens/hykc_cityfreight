package com.hykc.cityfreight.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hykc.cityfreight.R;

public class SelectCardTypeFragment extends DialogFragment implements View.OnClickListener {
    private ImageView mImgOk1;
    private ImageView mImgOk2;
    private ImageView mImgOk3;

    private RelativeLayout mLayoutzfb;
    private RelativeLayout mLayoutwx;
    private RelativeLayout mLayoutyhk;

    private ImageView mImgClose;
    private Button btn;
    private int type=3;

    private onItemSelectListener listener;


    public static SelectCardTypeFragment newInstance(){
        return new SelectCardTypeFragment();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.NoticeDialogStyle);

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return inflater.inflate(R.layout.layout_select_card_type,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mImgClose=view.findViewById(R.id.img_close);

        mImgOk1=view.findViewById(R.id.img_ok1);
        mImgOk2=view.findViewById(R.id.img_ok2);
        mImgOk3=view.findViewById(R.id.img_ok3);

        mLayoutzfb=view.findViewById(R.id.layout_zfb);
        mLayoutwx=view.findViewById(R.id.layout_wx);
        mLayoutyhk=view.findViewById(R.id.layout_yhk);

        btn=view.findViewById(R.id.btn);

        mLayoutzfb.setOnClickListener(this);
        mLayoutwx.setOnClickListener(this);
        mLayoutyhk.setOnClickListener(this);
        btn.setOnClickListener(this);
        mImgClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_close:
                dismiss();
                break;
            case R.id.btn:
                if(listener!=null){
                    dismiss();
                    listener.onItemSelect(type);
                }
                break;
            case R.id.layout_zfb:
                type=1;
                setSelect(type);
                break;
            case R.id.layout_wx:
                type=2;
                setSelect(type);
                break;
            case R.id.layout_yhk:
                type=3;
                setSelect(type);
                break;
        }
    }


    private void setSelect(int index){
        resetSelect();
        switch (index){
            case 1:
                mImgOk1.setVisibility(View.VISIBLE);
                break;
            case 2:
                mImgOk2.setVisibility(View.VISIBLE);
                break;
            case 3:
                mImgOk3.setVisibility(View.VISIBLE);
                break;
        }

    }

    private void resetSelect(){
        mImgOk1.setVisibility(View.GONE);
        mImgOk2.setVisibility(View.GONE);
        mImgOk3.setVisibility(View.GONE);
    }


    public void setOnItemSelectListener(onItemSelectListener listener){

        this.listener=listener;
    }

    public interface onItemSelectListener{
        void onItemSelect(int type);
    }

}
