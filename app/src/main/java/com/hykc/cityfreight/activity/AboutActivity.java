package com.hykc.cityfreight.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.utils.APKVersionCodeUtils;

public class AboutActivity extends BaseActivity {
    private ImageView mImgBack;
    private TextView mTextCode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_about);
        init();
    }

    @Override
    public void init() {
        mImgBack=findViewById(R.id.img_back);
        mTextCode=findViewById(R.id.tv_build_num);
        String apkName= APKVersionCodeUtils.getVerName(this);
        int verson=APKVersionCodeUtils.getVersionCode(this);
        mTextCode.setText("v"+apkName+"."+verson);

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }
}
