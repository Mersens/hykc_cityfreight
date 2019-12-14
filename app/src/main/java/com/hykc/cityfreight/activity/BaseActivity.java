package com.hykc.cityfreight.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.utils.StatusBarHelper;


/**
 * Created by Administrator on 2018/3/21.
 */

public abstract class BaseActivity extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    StatusBarHelper.translucent(this,getResources().getColor(R.color.actionbar_color));
  }
  public abstract void init();

}
