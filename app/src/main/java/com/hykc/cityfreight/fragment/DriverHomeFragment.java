package com.hykc.cityfreight.fragment;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.activity.ArgeListActivity;
import com.hykc.cityfreight.activity.CYActivity;
import com.hykc.cityfreight.activity.JYZActivity;
import com.hykc.cityfreight.activity.MainActivity;
import com.hykc.cityfreight.activity.NewsActivity;
import com.hykc.cityfreight.activity.OilActivity;
import com.hykc.cityfreight.activity.QCWXActivity;
import com.hykc.cityfreight.activity.SearchNearActivity;
import com.hykc.cityfreight.activity.ShopActivity;
import com.hykc.cityfreight.activity.TCCActivity;
import com.hykc.cityfreight.activity.ZSActivity;

import java.lang.ref.WeakReference;

public class DriverHomeFragment extends BaseFragment implements View.OnClickListener {
    private RelativeLayout mLayoutYK;
    private RelativeLayout mLayoutXW;
    private RelativeLayout mLayoutZB;
    private RelativeLayout mLayoutHT;
    private RelativeLayout mLayoutCZ;
    private RelativeLayout mLayoutSC;
    private RelativeLayout mLayoutJYZ;
    private RelativeLayout mLayoutTCC;
    private RelativeLayout mLayoutCY;
    private RelativeLayout mLayoutZS;
    private RelativeLayout mLayoutQCWX;
    private String lat;
    private String lon;

    WeakReference<MainActivity> mActivityReference;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityReference = new WeakReference<>((MainActivity) context);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_driver_home;
    }

    @Override
    protected void initView(View view) {
        mLayoutYK = view.findViewById(R.id.layout_yk);
        mLayoutXW = view.findViewById(R.id.layout_xw);
        mLayoutZB = view.findViewById(R.id.layout_zb);
        mLayoutHT = view.findViewById(R.id.layout_ht);
        mLayoutCZ = view.findViewById(R.id.layout_cz);
        mLayoutSC = view.findViewById(R.id.layout_sc);

        mLayoutJYZ = view.findViewById(R.id.layout_jyz);
        mLayoutTCC = view.findViewById(R.id.layout_tcc);
        mLayoutCY = view.findViewById(R.id.layout_cy);
        mLayoutZS = view.findViewById(R.id.layout_zs);
        mLayoutQCWX = view.findViewById(R.id.layout_qcwx);
        mLayoutJYZ.setOnClickListener(this);
        mLayoutTCC.setOnClickListener(this);
        mLayoutCY.setOnClickListener(this);
        mLayoutZS.setOnClickListener(this);
        mLayoutQCWX.setOnClickListener(this);


        mLayoutYK.setOnClickListener(this);
        mLayoutXW.setOnClickListener(this);
        mLayoutZB.setOnClickListener(this);
        mLayoutHT.setOnClickListener(this);
        mLayoutCZ.setOnClickListener(this);
        mLayoutSC.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        Log.e("DriverHomeFragment", "DriverHomeFragment initData");

    }

    public static DriverHomeFragment newInstance() {
        DriverHomeFragment fragment = new DriverHomeFragment();
        return fragment;
    }

    @Override
    public void onClick(View v) {
        MainActivity activity=mActivityReference.get();
        if(activity!=null){
            lat=activity.lat;
            lon=activity.lon;
        }

        Intent intent = null;
        switch (v.getId()) {
            case R.id.layout_yk:
                intent = new Intent(getActivity(), OilActivity.class);
                break;
            case R.id.layout_xw:
                intent = new Intent(getActivity(), NewsActivity.class);
                break;
            case R.id.layout_zb:
                intent = new Intent(getActivity(), SearchNearActivity.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lon",lon);
                Log.e("layout_zb","layout_zb=="+lat+";"+lon);
                break;
            case R.id.layout_ht:
                intent = new Intent(getActivity(), ArgeListActivity.class);
                break;
            case R.id.layout_cz:
                Toast.makeText(getActivity(), "该功能暂未开放,敬请期待！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layout_sc:
                intent = new Intent(getActivity(), ShopActivity.class);
                break;
            case R.id.layout_jyz:
                //加油站
                intent = new Intent(getActivity(), JYZActivity.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lon",lon);
                Log.e("layout_jyz","layout_jyz=="+lat+";"+lon);

                break;
            case R.id.layout_tcc:
                //停车场
                intent = new Intent(getActivity(), TCCActivity.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lon",lon);
                break;
            case R.id.layout_cy:
                //餐饮
                intent = new Intent(getActivity(), CYActivity.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lon",lon);
                break;
            case R.id.layout_zs:
                //住宿
                intent = new Intent(getActivity(), ZSActivity.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lon",lon);
                break;
            case R.id.layout_qcwx:
                //汽车维修
                intent = new Intent(getActivity(), QCWXActivity.class);
                intent.putExtra("lat",lat);
                intent.putExtra("lon",lon);
                break;
        }
        if (intent != null) {
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }

    }
}
