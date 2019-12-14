package com.hykc.cityfreight.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.view.ExitDialogFragment;

public class ContactActivity extends BaseActivity {
    private TextView mTextNum;
    private ImageView mImgBack;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_contact);
        init();
    }

    @Override
    public void init() {
        mTextNum=findViewById(R.id.tv_service_num);
        mImgBack=findViewById(R.id.img_back);
        mTextNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(ContactActivity.this,
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(ContactActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    } else {
                        callPhone();
                    }
                }else {
                    callPhone();
                }
            }
        });
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

    }

    private void callPhone() {
        confirmUserPhone("确定拨打电话？");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhone();
            } else
            {
                // Permission Denied
                Toast.makeText(ContactActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void confirmUserPhone(String msg) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getSupportFragmentManager(), "CallDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @SuppressLint("MissingPermission")
            @Override
            public void onClickOk() {
                final String num=mTextNum.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + num);
                intent.setData(data);
                startActivity(intent);
                dialog.dismiss();
            }
        });



    }



}
