package com.hykc.cityfreight.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.OthersImgAdapter;
import com.hykc.cityfreight.entity.SuggestionEntity;
import com.hykc.cityfreight.view.ContainsEmojiEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SuggestionDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mImgBack;
    private EditText mEditMobil;
    private TextView mEditSYWT;
    private TextView mEditGNJY;
    private TextView mEditCXWT;
    private TextView mEditQTFK;
    private ContainsEmojiEditText mEditText;
    private RecyclerView mRecyclerView;
    private int selectBg;
    private int unSelectBg;
    private int selectColor;
    private int unSelectColor;
    private String []types=new String[]{
            "使用问题",
            "功能建议",
            "程序问题",
            "其他反馈"};
    private SuggestionEntity entity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_suggestin_details);
        entity=(SuggestionEntity)getIntent().getSerializableExtra("entity");
        init();
    }

    @Override
    public void init() {
        selectBg=R.drawable.suggestion_type_select_bg;
        unSelectBg=R.drawable.suggestion_type_normal_bg;
        selectColor=getResources().getColor(R.color.white);
        unSelectColor=getResources().getColor(R.color.text_color);
        initView();

    }

    private void initView() {
        mEditMobil=findViewById(R.id.editPhone);
        mEditSYWT=findViewById(R.id.tv_sywt);
        mEditGNJY=findViewById(R.id.tv_gnjy);
        mEditCXWT=findViewById(R.id.tv_cxwt);
        mEditQTFK=findViewById(R.id.tv_qtfk);
        mEditText=findViewById(R.id.editText);
        mEditText.setFocusable(false);
        mEditText.setFocusableInTouchMode(false);
        mImgBack=findViewById(R.id.img_back);
        mRecyclerView=findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        if(entity==null){
            return;
        }
        mEditMobil.setText(entity.getMobile());
        String questionType=entity.getQuestionType();
        if(questionType.equals(types[0])){
            setSelectColor(1);

        }else if(questionType.equals(types[1])){
            setSelectColor(2);
        }
        else if(questionType.equals(types[2])){
            setSelectColor(3);
        }
        else if(questionType.equals(types[3])){
            setSelectColor(4);
        }
        String msg=entity.getQuestionContent();
        mEditText.setText(msg);

        String url=entity.getImgUrl();
        if(!TextUtils.isEmpty(url)){
            String urls [];
            if(url.contains(";")){
                //多张照片
                urls=url.split(";");
            }else {
                //只有一张照片
                urls=new String[]{url};
            }
            List<String> list=new ArrayList<>(Arrays.asList(urls));
            OthersImgAdapter adapter=new OthersImgAdapter(list,SuggestionDetailsActivity.this);
            mRecyclerView.setAdapter(adapter);
        }
    }

    private void setSelectColor(int pos){
        resetColor();
        switch (pos){
            case 1:
                mEditSYWT.setBackgroundResource(selectBg);
                mEditSYWT.setTextColor(selectColor);
                break;
            case 2:
                mEditGNJY.setBackgroundResource(selectBg);
                mEditGNJY.setTextColor(selectColor);
                break;
            case 3:
                mEditCXWT.setBackgroundResource(selectBg);
                mEditCXWT.setTextColor(selectColor);
                break;
            case 4:
                mEditQTFK.setBackgroundResource(selectBg);
                mEditQTFK.setTextColor(selectColor);
                break;

        }
    }

    private void resetColor() {
        mEditSYWT.setBackgroundResource(unSelectBg);
        mEditSYWT.setTextColor(unSelectColor);

        mEditGNJY.setBackgroundResource(unSelectBg);
        mEditGNJY.setTextColor(unSelectColor);

        mEditCXWT.setBackgroundResource(unSelectBg);
        mEditCXWT.setTextColor(unSelectColor);

        mEditQTFK.setBackgroundResource(unSelectBg);
        mEditQTFK.setTextColor(unSelectColor);
    }


    @Override
    public void onClick(View v) {

    }
}
