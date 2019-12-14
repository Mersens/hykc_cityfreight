package com.hykc.cityfreight.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.View;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.OrderPagerAdapter;
import com.hykc.cityfreight.view.OrderViewPager;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends BaseFragment {
    private TabLayout mTabLayout;
    private OrderViewPager mViewPager;
    private List<Fragment> fragmentList;
    private List<String> tabTitles;
    private OrderPagerAdapter mAdapter;
    @Override
    protected int getLayoutResource() {
        return R.layout.layout_order;
    }

    @Override
    protected void initView(View view) {
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mViewPager =  view.findViewById(R.id.viewPager);
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(WWCFragment.newInstance());
        fragmentList.add(YWCFragment.newInstance());
        fragmentList.add(YQXFragment.newInstance());
        tabTitles = new ArrayList<String>();
        tabTitles.add("未完成");
        tabTitles.add("已完成");
        tabTitles.add("已撤销");
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles.get(2)));
        mAdapter = new OrderPagerAdapter(getChildFragmentManager(),
                fragmentList, tabTitles);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void initData() {

    }

    public static  OrderFragment newInstance(){
        OrderFragment fragment=new OrderFragment();
        return fragment;
    }

}
