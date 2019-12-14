package com.hykc.cityfreight.activity;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Window;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.fragment.CustomChatFragment;
import com.hykc.cityfreight.utils.StatusBarHelper;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.helpdesk.callback.Callback;
import com.hyphenate.helpdesk.easeui.recorder.MediaManager;
import com.hyphenate.helpdesk.easeui.ui.BaseActivity;
import com.hyphenate.helpdesk.easeui.ui.ChatFragment;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.util.Config;

public class ChatActivity extends BaseActivity {
    public static ChatActivity instance = null;
    private ChatFragment chatFragment;
    String toChatUsername;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        StatusBarHelper.translucent(this,getResources().getColor(R.color.actionbar_color));
        setContentView(R.layout.layout_chat);
        instance = this;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            //IM服务号
            toChatUsername = bundle.getString(Config.EXTRA_SERVICE_IM_NUMBER);
        //可以直接new ChatFragment使用
        String chatFragmentTAG = "chatFragment";
        chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(chatFragmentTAG);
        if (chatFragment == null){
            chatFragment = new CustomChatFragment();
            //传入参数
            bundle.putBoolean(Config.EXTRA_SHOW_NICK,false);
            chatFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment, chatFragmentTAG).commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaManager.release();
        instance = null;
//        ChatClient.getInstance().chatManager().cancelVideoConferences(toChatUsername, null);
        ChatClient.getInstance().logout(true, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String username = intent.getStringExtra(Config.EXTRA_SERVICE_IM_NUMBER);
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        if (chatFragment != null) {
            chatFragment.onBackPressed();
        }
        if (CommonUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
