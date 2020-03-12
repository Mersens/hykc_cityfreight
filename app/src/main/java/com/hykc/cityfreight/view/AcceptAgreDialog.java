package com.hykc.cityfreight.view;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.utils.DisplayHelper;

public class AcceptAgreDialog extends DialogFragment {
    private String url="file:///android_asset/hykc_agreement.html";
    private WebView webView;
    private Button mBtnCancel;
    private Button mBtnOk;
    private OnSelectListener listener;
    private WebViewClient webViewClient=new WebViewClient(){

        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载

        }
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
        }

    };
    private WebChromeClient webChromeClient=new WebChromeClient(){

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

        }
    };



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.NoticeDialogStyle);

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        return inflater.inflate(R.layout.layout_accept_agre,container,true);
    }

    @Override
    public void onResume() {
        super.onResume();
        int screenHeight=DisplayHelper.getScreenHeight(getActivity());
        getDialog().getWindow().setLayout(DisplayHelper.getScreenWidth(getActivity()),
                (screenHeight*3)/4);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        webView=view.findViewById(R.id.webview);
        mBtnCancel=view.findViewById(R.id.tv_jj);
        mBtnOk=view.findViewById(R.id.tv_ty);

        webView.setWebChromeClient(webChromeClient);
        webView.setWebViewClient(webViewClient);

        WebSettings webSettings=webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webView.loadUrl(url);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onCancel();
                }
            }
        });
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(listener!=null){
                    listener.onSelect();
                }
            }
        });

    }

    public void showF(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
    public static AcceptAgreDialog newInstance(){
        return new AcceptAgreDialog();
    }


    public void setOnSelectListener(OnSelectListener listener){
        this.listener=listener;
    }

    public interface OnSelectListener{
        void onSelect();
        void onCancel();
    }

}
