package com.hykc.cityfreight.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
    private View rootView;
    private boolean isInitData = false;
    private boolean isVisibleToUser = false;
    private boolean isPrepareView = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       if(rootView==null){
           rootView=inflater.inflate(getLayoutResource(),container,false);
       }
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isPrepareView = true;
        initView(view);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        this.isVisibleToUser = isVisibleToUser;
        lazyInitData();
    }

    private void lazyInitData(){
        if (!isInitData && isVisibleToUser && isPrepareView) {
            isInitData = true;
            initData();
        }
    }

    protected abstract int  getLayoutResource();


    protected abstract void initView(View view);

    protected abstract void initData();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lazyInitData();
    }
}
