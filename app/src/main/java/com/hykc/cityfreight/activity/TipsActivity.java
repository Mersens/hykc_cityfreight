package com.hykc.cityfreight.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.utils.APKVersionCodeUtils;
import com.hykc.cityfreight.view.SelectDialog;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.util.ArrayList;
import java.util.List;

public class TipsActivity extends BaseActivity {
    private ImageView mImgBack;
    private TextView mTextTips;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    String mobile="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tips);
        init();
    }

    @Override
    public void init() {
        mImgBack=findViewById(R.id.img_back);
        mTextTips=findViewById(R.id.tv_tips);
        String content=getResources().getString(R.string.yq_tips);
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        ForegroundColorSpan buleSpan1 = new ForegroundColorSpan(Color.parseColor("#2182d1"));
        ForegroundColorSpan buleSpan2 = new ForegroundColorSpan(Color.parseColor("#2182d1"));

        builder.setSpan(buleSpan1, 91, 103, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(buleSpan2, 104, 115, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTextTips.setText(builder);
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        mTextTips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectMobileView();
            }
        });
    }

    private void showSelectMobileView() {
        List<String> names = new ArrayList<>();
        names.add("400-996-2611");
        names.add("18530802683");
        showDialog(new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        String tel1="4009962611";
                        checkPermiss(tel1);
                        break;
                    case 1:
                        String tel2="18530802683";
                        checkPermiss(tel2);
                        break;
                }
            }
        }, names);
    }


    private void checkPermiss(String tel){
        mobile=tel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(TipsActivity.this,
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(TipsActivity.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            } else {
                callPhone(tel);
            }
        }else {
            callPhone(tel);
        }
    }

    private void callPhone(String tel) {

        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + tel);
        intent.setData(data);
        startActivity(intent);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhone(mobile);
            } else
            {
                // Permission Denied
                Toast.makeText(TipsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style
                .transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }
}
