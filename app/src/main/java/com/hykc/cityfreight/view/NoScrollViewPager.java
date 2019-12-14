package com.hykc.cityfreight.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollViewPager extends ViewPager {
    private  boolean isScroll;
    public NoScrollViewPager(@NonNull Context context) {
        super(context);
    }

    public NoScrollViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(isScroll){
            return super.onInterceptTouchEvent(ev);
        }else {
            return false;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(isScroll){
            return super.onTouchEvent(ev);
        }else {
            return false;
        }

    }

    public void setScroll(boolean scroll) {
        isScroll = scroll;
    }
}
