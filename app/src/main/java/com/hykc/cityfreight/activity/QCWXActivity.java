package com.hykc.cityfreight.activity;

import android.support.v4.app.Fragment;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.app.Constants;
import com.hykc.cityfreight.fragment.CommonFragment;

public class QCWXActivity extends SingleFragmentActivity {
    private String lon;
    private String lat;

    @Override
    public Fragment getContent() {
        lon=getIntent().getStringExtra("lon");
        lat=getIntent().getStringExtra("lat");
        return CommonFragment.getInstance(Constants.WEBSERVICE_URL+"admin/searchQCWX?lat="+lat+"&lon="+lon);
    }

    @Override
    public String setTitleText() {
        return "汽车维修";
    }

    @Override
    public void onBack() {
        finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}
