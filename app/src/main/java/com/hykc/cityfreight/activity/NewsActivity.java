package com.hykc.cityfreight.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.NewsPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewsActivity extends BaseActivity {
    private ImageView mImgBack;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<String> tabTitles;
    private NewsPagerAdapter adapter;
    private final String titles[]=new String[]{"头条",
            "社会","国内","国际","娱乐","体育","军事",
            "科技","财经","时尚"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_news);
        init();
    }

    @Override
    public void init() {
        initViews();
        initEvent();
    }

    private void initViews() {
        mImgBack=findViewById(R.id.img_back);
        mTabLayout =  findViewById(R.id.tabLayout);
        mViewPager =  findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(titles.length);
        tabTitles = new ArrayList<String>(Arrays.asList(titles));
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        for (String title:tabTitles) {
            mTabLayout.addTab(mTabLayout.newTab().setText(title));
        }
        adapter=new NewsPagerAdapter(getSupportFragmentManager(),tabTitles);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void initEvent() {
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }
}
