package com.hykc.cityfreight.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.OrderPagerAdapter;
import com.hykc.cityfreight.app.App;
import com.hykc.cityfreight.fragment.AccountLoginFragment;
import com.hykc.cityfreight.fragment.CodeLoginFragment;
import com.hykc.cityfreight.utils.StatusBarHelper;
import com.hykc.cityfreight.view.ExitDialogFragment;
import com.hykc.cityfreight.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private NoScrollViewPager mViewPager;
    private List<Fragment> fragmentList;
    private List<String> tabTitles;
    private OrderPagerAdapter mAdapter;
    private TextView mTips;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarHelper.translucent(this,getResources().getColor(R.color.login_actionbar_color));
        setContentView(R.layout.layout_login);
        init();
    }

    @Override
    public void init() {
        mTips=findViewById(R.id.tv_tips);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        mViewPager.setScroll(false);
        mViewPager.setOffscreenPageLimit(1);
        mTips.setSelected(true);
        initDatas();

    }
    private void initDatas() {
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(CodeLoginFragment.newInstance());
        fragmentList.add(AccountLoginFragment.newInstance());
        tabTitles = new ArrayList<String>();
        tabTitles.add("快捷登录");
        tabTitles.add("账号登录");
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles.get(1)));
        mAdapter = new OrderPagerAdapter(getSupportFragmentManager(), fragmentList, tabTitles);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos=tab.getPosition();
                if(pos==1){
                    CodeLoginFragment codeLoginFragment= (CodeLoginFragment)fragmentList.get(0);
                    codeLoginFragment.stopTimerService();
                }else if(pos==0){
                    AccountLoginFragment accountLoginFragment=(AccountLoginFragment)fragmentList.get(1);
                    accountLoginFragment.reset();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            confirmExit();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    private void confirmExit() {
        //退出操作
        final ExitDialogFragment dialog = new ExitDialogFragment();
        dialog.show(getSupportFragmentManager(), "ExitDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                App.getInstance().exit();

            }
        });
    }

}
