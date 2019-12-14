package com.hykc.cityfreight.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.alct.mdp.MDPLocationCollectionManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.activity.ChatActivity;
import com.hykc.cityfreight.processprotection.PlayerMusicService;
import com.hykc.cityfreight.utils.ListenerManager;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.ChatManager;
import com.hyphenate.chat.Conversation;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.easeui.Notifier;
import com.hyphenate.helpdesk.easeui.UIProvider;
import com.hyphenate.helpdesk.easeui.util.CommonUtils;
import com.hyphenate.helpdesk.easeui.util.IntentBuilder;
import com.hyphenate.helpdesk.model.AgentInfo;
import com.hyphenate.helpdesk.model.MessageHelper;
import com.tencent.tinker.entry.ApplicationLike;
import com.tinkerpatch.sdk.TinkerPatch;
import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/21.
 */

public class App extends Application {
    private ApplicationLike tinkerApplicationLike;
    private static App sApp;
    private List<Activity> mList;
    private UIProvider _uiProvider;
    protected ChatManager.MessageListener messageListener = null;

    /**
     * ChatClient.ConnectionListener
     */
    private ChatClient.ConnectionListener connectionListener;
    public App(){
        mList=new ArrayList<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp=this;
        initALCT(this);
        CrashHandler.getInstance().init(getApplicationContext());
        addLifecycleCallbacks();
        initTinker();
        initIMKEFU(this);

    }

    private void initIMKEFU(Context context) {
        ChatClient.Options options = new ChatClient.Options();
        options.setAppkey(Constants.IMAPPKEY);//必填项，appkey获取地址：kefu.easemob.com，“管理员模式 > 渠道管理 > 手机APP”页面的关联的“AppKey”
        options.setTenantId(Constants.IMTENANTID);//必填项，tenantId获取地址：kefu.easemob.com，“管理员模式 > 设置 > 企业信息”页面的“租户ID”

        // Kefu SDK 初始化
        if (!ChatClient.getInstance().init(context, options)){
            return;
        }
        // Kefu EaseUI的初始化
        _uiProvider = UIProvider.getInstance();
        //初始化EaseUI
        _uiProvider.init(context);
        //后面可以设置其他属性
        setEaseUIProvider(context);
        setGlobalListeners();
    }

    private void initTinker() {
        tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();

        // 初始化TinkerPatch SDK, 更多配置可参照API章节中的,初始化SDK
        TinkerPatch.init(tinkerApplicationLike)
                .reflectPatchLibrary()
                .setPatchRollbackOnScreenOff(true)
                .setPatchRestartOnSrceenOff(true)
                .setFetchPatchIntervalByHours(3);
        TinkerPatch.with().reflectPatchLibrary();
        TinkerPatch.with().fetchPatchUpdate(true);
        // 每隔3个小时(通过setFetchPatchIntervalByHours设置)去访问后台时候有更新,通过handler实现轮训的效果
        TinkerPatch.with().fetchPatchUpdateAndPollWithInterval();

    }

    private void initALCT(Context context) {
        if (context.getPackageName().equals(getCurrentProcessName(context))) {
           // MDPLocationCollectionManager.initialize(context, Constants.ALCT_URL);
            com.alct.mdp.health.ALCTSDKHealth.initialize(getApplicationContext(), Constants.ALCT_URL);
            MDPLocationCollectionManager.initServiceProcessProguard(context); // 保活代码
           // 保活代码
            Intent  intent= new Intent(context, PlayerMusicService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
    }
    private static String getCurrentProcessName(Context context) {
        String currentProcessName = "";
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context .getSystemService(Context.ACTIVITY_SERVICE);

        if (mActivityManager.getRunningAppProcesses() != null && mActivityManager.getRunningAppProcesses().size() > 0) {
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    currentProcessName = appProcess.processName;
                }
            }
        }
        return currentProcessName;
    }

    public static App getInstance(){
        if(sApp==null){
            synchronized (App.class){
                if(sApp==null){
                    sApp=new App();
                }
            }
        }
        return sApp;
    }


    private void addLifecycleCallbacks(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Log.e("onActivityCreated","onActivityCreated==="+activity.getClass().getName());
                mList.add(activity);
            }
            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                //mList.remove(activity);
                Log.e("onActivityDestroyed","onActivityDestroyed==="+activity.getClass().getName());
            }
        });

    }
    public void exit() {
        try {
            for (int i=0;i<mList.size();i++) {
                Activity activity=mList.get(i);
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }
    private void setEaseUIProvider(final Context context) {
        //设置头像和昵称 某些控件可能没有头像和昵称，需要注意
        UIProvider.getInstance().setUserProfileProvider(new UIProvider.UserProfileProvider() {
            @Override
            public void setNickAndAvatar(Context context, Message message, ImageView userAvatarView, TextView usernickView) {
                if (message.direct() == Message.Direct.RECEIVE) {
                    //设置接收方的昵称和头像
//                    UserUtil.setAgentNickAndAvatar(context, message, userAvatarView, usernickView);
                    AgentInfo agentInfo = MessageHelper.getAgentInfo(message);
                    if (usernickView != null) {
                        usernickView.setText(message.from());
                        if (agentInfo != null) {
                            if (!TextUtils.isEmpty(agentInfo.getNickname())) {
                                usernickView.setText(agentInfo.getNickname());
                            }
                        }
                    }
                    if (userAvatarView != null) {
                        if (agentInfo != null) {
                            if (!TextUtils.isEmpty(agentInfo.getAvatar())) {
                                String strUrl = agentInfo.getAvatar();
                                // 设置客服头像
                                if (!TextUtils.isEmpty(strUrl)) {
                                    if (!strUrl.startsWith("http")) {
                                        strUrl = "http:" + strUrl;
                                    }
                                    //正常的string路径
                                    Glide.with(context).load(strUrl).apply(RequestOptions.placeholderOf(R.mipmap.ic_driver_circle).diskCacheStrategy(DiskCacheStrategy.ALL).circleCrop()).into(userAvatarView);
//                                    Glide.with(context).load(strUrl).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(com.hyphenate.helpdesk.R.drawable.hd_default_avatar).transform(new GlideCircleTransform(context)).into(userAvatarView);
                                    return;
                                }
                            }
                        }
                        userAvatarView.setImageResource(R.mipmap.ic_driver_circle);
                    }
                } else {
                    //此处设置当前登录用户的头像，
                    if (userAvatarView != null) {
                        userAvatarView.setImageResource(R.mipmap.icon_user_tx1);
//                        Glide.with(context).load("http://oev49clxj.bkt.clouddn.com/7a8aed7bjw1f32d0cumhkj20ey0mitbx.png").diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.hd_default_avatar).into(userAvatarView);
//                        如果用圆角，可以采用此方案：http://blog.csdn.net/weidongjian/article/details/47144549
                    }else {
                        Log.e("userAvatarView","userAvatarView====null");
                    }
                }
            }
        });

        //设置通知栏样式
        _uiProvider.getNotifier().setNotificationInfoProvider(new Notifier.NotificationInfoProvider() {
            @Override
            public String getTitle(Message message) {
                //修改标题,这里使用默认
                return "客服消息";
            }

            @Override
            public int getSmallIcon(Message message) {
                //设置小图标，这里为默认
                return R.mipmap.ic_driver_circle;
            }

            @Override
            public String getDisplayedText(Message message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = CommonUtils.getMessageDigest(message, context);
                if (message.getType() == Message.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", context.getString(R.string.noti_text_expression));
                }
                return message.from() + ": " + ticker;
            }

            @Override
            public String getLatestText(Message message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "contacts send " + messageNum + "messages to you";
            }

            @Override
            public Intent getLaunchIntent(Message message) {
                Intent intent;
                    //设置点击通知栏跳转事件
                    Conversation conversation = ChatClient.getInstance().chatManager().getConversation(message.from());
                    String titleName = null;
                    if (conversation.officialAccount() != null){
                        titleName = conversation.officialAccount().getName();
                    }
                    intent = new IntentBuilder(context)
                            .setTargetClass(ChatActivity.class)
                            .setServiceIMNumber(conversation.conversationId())
                            .setTitleName(titleName)
                            .setShowUserNick(false)
                            .build();

                return intent;
            }
        });

    }

    private void setGlobalListeners(){

        //注册消息事件监听
        registerEventListener();

    }
    protected void registerEventListener(){
        messageListener = new ChatManager.MessageListener(){

            @Override
            public void onMessage(List<Message> msgs) {
                for (Message message : msgs){
//
                    //这里全局监听通知类消息,通知类消息是通过普通消息的扩展实现
                    if (MessageHelper.isNotificationMessage(message)){
                        // 检测是否为留言的通知消息
                        String eventName = getEventNameByNotification(message);
                        if (!TextUtils.isEmpty(eventName)){
                            if (eventName.equals("TicketStatusChangedEvent") || eventName.equals("CommentCreatedEvent")){
                                // 检测为留言部分的通知类消息,刷新留言列表
                                JSONObject jsonTicket = null;
                                try{
                                    jsonTicket = message.getJSONObjectAttribute("weichat").getJSONObject("event").getJSONObject("ticket");
                                }catch (Exception ignored){}
                                ListenerManager.getInstance().sendBroadCast(eventName, jsonTicket);
                            }
                        }
                    }

                }
            }

            @Override
            public void onCmdMessage(List<Message> msgs) {
                for (Message message : msgs){
                    //获取消息body
                    EMCmdMessageBody cmdMessageBody = (EMCmdMessageBody) message.body();
                    String action = cmdMessageBody.action(); //获取自定义action
                }
            }

            @Override
            public void onMessageStatusUpdate() {

            }

            @Override
            public void onMessageSent() {

            }
        };

        ChatClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    public String getEventNameByNotification(Message message){

        try {
            JSONObject weichatJson = message.getJSONObjectAttribute("weichat");
            if (weichatJson != null && weichatJson.has("event")) {
                JSONObject eventJson = weichatJson.getJSONObject("event");
                if (eventJson != null && eventJson.has("eventName")){
                    return eventJson.getString("eventName");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




}
