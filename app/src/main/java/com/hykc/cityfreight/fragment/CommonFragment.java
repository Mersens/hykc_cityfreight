package com.hykc.cityfreight.fragment;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hykc.cityfreight.R;


public class CommonFragment extends BaseFragment {
    private String url;
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url=getArguments().getString("url");
    }

    private WebViewClient webViewClient=new WebViewClient(){

        @Override
        public void onPageFinished(WebView view, String url) {//页面加载完成
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//页面开始加载
            progressBar.setVisibility(View.VISIBLE);
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
            if (newProgress < 100) {
                progressBar.setProgress(newProgress);
            } else {
                return;
            }
        }
    };

    public static CommonFragment getInstance(String url){
        CommonFragment fragment=new CommonFragment();
        Bundle bundle=new Bundle();
        bundle.putString("url",url);
        fragment.setArguments(bundle);
        return fragment;

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.layout_web;
    }



    @Override
    protected void initData() {

    }

    @Override
    protected void initView(View v) {
        url=getArguments().getString("url");
        Log.e("getArguments","getArguments==="+url);
        webView=v.findViewById(R.id.webview);
        progressBar=v.findViewById(R.id.progressbar);
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
    }

    @Override
    public void onDestroy() {
        if (webView != null) {
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
