package com.hykc.cityfreight.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykc.cityfreight.R;


public abstract class SingleFragmentActivity extends BaseActivity {
    private ImageView mImgBack;
    private TextView mTextTitle;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_single_fragment);
        String lon=getIntent().getStringExtra("lon");
        String lat=getIntent().getStringExtra("lat");
        Log.e("SingleFragmentActivity","SingleFragmentActivity=="+lat+";"+lon);
        init();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_content, getContent()).commitAllowingStateLoss();
    }


    public abstract Fragment getContent();
    public abstract String setTitleText();
    public abstract void onBack();

    @Override
    public void init() {
        mTextTitle=findViewById(R.id.tv_title);
        mImgBack=findViewById(R.id.img_back);
        mTextTitle.setText(setTitleText());
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBack();
            }
        });
    }
}
