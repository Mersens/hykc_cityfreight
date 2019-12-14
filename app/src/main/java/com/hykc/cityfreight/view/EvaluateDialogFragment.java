package com.hykc.cityfreight.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.cb.ratingbar.CBRatingBar;
import com.hykc.cityfreight.R;


public class EvaluateDialogFragment extends DialogFragment {
    private ImageView mImageClose;
    private Button mBtnOk;
    private OnReasonDialogListener listener;
    private CBRatingBar rating_bar1;
    private CBRatingBar rating_bar2;
    private int adminpingjia=5;//平台评分
    private int huozhupingjia=5;//货主评分

    public static EvaluateDialogFragment getInstance(){
        EvaluateDialogFragment fragment=new EvaluateDialogFragment();
        return  fragment;

    }
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

        return inflater.inflate(R.layout.layout_evaluate,container,true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mImageClose=view.findViewById(R.id.img_close);
        mBtnOk=view.findViewById(R.id.btn_ok);
        rating_bar1=view.findViewById(R.id.rating_bar1);
        rating_bar2=view.findViewById(R.id.rating_bar2);
        rating_bar1.setOnStarTouchListener(new CBRatingBar.OnStarTouchListener() {
            @Override
            public void onStarTouch(int touchCount) {
                adminpingjia=touchCount;

            }
        });
        rating_bar2.setOnStarTouchListener(new CBRatingBar.OnStarTouchListener() {
            @Override
            public void onStarTouch(int touchCount) {
                huozhupingjia=touchCount;

            }
        });
        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onCloseListener();
                }
            }
        });
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){

                    listener.onComplateListener(adminpingjia,huozhupingjia);
                }
            }
        });
    }

    public void setOnReasonDialogListener(OnReasonDialogListener listener){
        this.listener=listener;
    }

    public interface  OnReasonDialogListener{
        void onCloseListener();
        void onComplateListener(int adminpingjia, int huozhupingjia);
    }

}
