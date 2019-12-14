package com.hykc.cityfreight.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hykc.cityfreight.fragment.NewsFragment;

import java.util.List;

public class NewsPagerAdapter extends FragmentStatePagerAdapter {
    private List<String> tabTitles;
    public NewsPagerAdapter(FragmentManager fm, List<String> tabTitles) {
        super(fm);
        this.tabTitles=tabTitles;
    }

    @Override
    public Fragment getItem(int i) {
        return NewsFragment.newInstance(tabTitles.get(i));
    }

    @Override
    public int getCount() {
        return tabTitles.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {

        return tabTitles.get(position % tabTitles.size());
    }
}
