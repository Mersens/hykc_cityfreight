package com.hykc.cityfreight.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykc.cityfreight.R;


/**
 * Created by Administrator on 2018/3/30.
 */

public class ImageExampleDialog extends DialogFragment {
    private ImageView mImageClose;
    private ImageView mImg;
    private TextView mTextTitle;
    private TextView mTextMsg;
    private int type = -1;
    private Button mBtn;
    private OnButtonClickListener listener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.NoticeDialogStyle);

    }
    public static ImageExampleDialog getInstance(int type) {
        ImageExampleDialog fragment = new ImageExampleDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return inflater.inflate(R.layout.layout_img_example, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        type = getArguments().getInt("type");
        mImageClose = view.findViewById(R.id.img_close);
        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mImg = view.findViewById(R.id.img_example);
        mTextTitle = view.findViewById(R.id.tv_title);
        mTextMsg = view.findViewById(R.id.tv_tips);
        mBtn = view.findViewById(R.id.btn_ok);
        if (type == 1) {
            mTextTitle.setText("卸货照");
            mTextMsg.setText(R.string.xhz_tips);
            mImg.setImageResource(R.mipmap.img_unload);

        } else if (type == 2) {
            mTextTitle.setText("回单照");
            mTextMsg.setText(R.string.hdz_tips);
            mImg.setImageResource(R.mipmap.img_hdz);
        }
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onClick();
                }

            }
        });
    }

    public void showF(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public void setOnButtonClickListener(OnButtonClickListener listener) {
        this.listener = listener;

    }

    public interface OnButtonClickListener {
        void onClick();
    }
}
