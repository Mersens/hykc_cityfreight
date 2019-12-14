package com.hykc.cityfreight.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.hykc.cityfreight.R;
import com.hyphenate.chat.ChatClient;
import com.hyphenate.chat.Message;
import com.hyphenate.helpdesk.easeui.provider.CustomChatRowProvider;
import com.hyphenate.helpdesk.easeui.recorder.MediaManager;
import com.hyphenate.helpdesk.easeui.ui.ChatFragment;
import com.hyphenate.helpdesk.easeui.widget.AlertDialogFragment;
import com.hyphenate.helpdesk.easeui.widget.MessageList;
import com.hyphenate.helpdesk.easeui.widget.chatrow.ChatRow;
import com.hyphenate.helpdesk.model.MessageHelper;

public class CustomChatFragment extends ChatFragment implements ChatFragment.EaseChatFragmentListener {

    //避免和基类定义的常量可能发生冲突,常量从11开始定义
    private static final int ITEM_MAP = 11;
    private static final int ITEM_LEAVE_MSG = 12;//ITEM_SHORTCUT = 12;
    private static final int ITEM_VIDEO = 13;
    private static final int ITEM_EVALUATION = 14;

    private static final int REQUEST_CODE_SELECT_MAP = 11;
    private static final int REQUEST_CODE_SHORTCUT = 12;

    public static final int REQUEST_CODE_CONTEXT_MENU = 13;

    //message type 需要从1开始
    public static final int MESSAGE_TYPE_SENT_MAP = 1;
    public static final int MESSAGE_TYPE_RECV_MAP = 2;
    public static final int MESSAGE_TYPE_SENT_ORDER = 3;
    public static final int MESSAGE_TYPE_RECV_ORDER = 4;
    public static final int MESSAGE_TYPE_SENT_EVAL = 5;
    public static final int MESSAGE_TYPE_RECV_EVAL = 6;
    public static final int MESSAGE_TYPE_SENT_TRACK = 7;
    public static final int MESSAGE_TYPE_RECV_TRACK = 8;
    public static final int MESSAGE_TYPE_SENT_FORM = 9;
    public static final int MESSAGE_TYPE_RECV_FORM = 10;


    //message type 最大值
    public static final int MESSAGE_TYPE_COUNT = 13;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void setUpView() {
        //这是新添加的扩展点击事件
        setChatFragmentListener(this);
        super.setUpView();
        //可以在此处设置titleBar(标题栏)的属性
        titleBar.setBackgroundColor(getResources().getColor(R.color.actionbar_color));
        titleBar.setLeftImageResource(R.mipmap.icon_action_back);
        titleBar.setLeftLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
        titleBar.setTitle("在线客服");
        titleBar.setRightImageResource(R.drawable.hd_chat_delete_icon);
        titleBar.setRightLayoutClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
//        ((Button)inputMenu.getButtonSend()).setBackgroundResource(R.color.top_bar_normal_bg);
        showUserNick=false;

    }



    private void showAlertDialog() {
        FragmentTransaction mFragTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        String fragmentTag = "dialogFragment";
        Fragment fragment =  getActivity().getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if(fragment!=null){
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mFragTransaction.remove(fragment);
        }
        final AlertDialogFragment dialogFragment = new AlertDialogFragment();
        dialogFragment.setTitleText(getString(R.string.prompt));
        dialogFragment.setContentText(getString(R.string.Whether_to_empty_all_chats));
        dialogFragment.setupLeftButton(null, null);
        dialogFragment.setupRightBtn(null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatClient.getInstance().chatManager().clearConversation(toChatUsername);
                messageList.refresh();
                dialogFragment.dismiss();
                MediaManager.release();
            }
        });
        dialogFragment.show(mFragTransaction, fragmentTag);
    }

    @Override
    protected void sendTextMessage(String content) {
        if (content != null && content.length() > 1500){
            Toast.makeText(getContext(), com.hyphenate.helpdesk.R.string.message_content_beyond_limit, Toast.LENGTH_SHORT).show();
            return;
        }
        Message message = Message.createTxtSendMessage(content, toChatUsername);
        message.setAttribute("avatar","http");
        ChatClient.getInstance().chatManager().sendMessage(message);
        messageList.refreshSelectLast();

    }

    @Override
    public void onAvatarClick(String username) {
        //头像点击事情
//        startActivity(new Intent(getActivity(), ...class));
    }

    @Override
    public boolean onMessageBubbleClick(Message message) {
        //消息框点击事件,return true

        return false;
    }

    @Override
    public void onMessageBubbleLongClick(Message message) {
        //消息框长按
    }



    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {

        //不覆盖已有的点击事件
        return false;
    }

    @Override
    public void onMessageItemClick(Message message, MessageList.ItemAction action) {

    }


    private void startVideoCall(){
        inputMenu.hideExtendMenuContainer();

        Message message = Message.createVideoInviteSendMessage(getString(R.string.em_chat_invite_video_call), toChatUsername);
        ChatClient.getInstance().chatManager().sendMessage(message);
    }



    @Override
    public CustomChatRowProvider onSetCustomChatRowProvider() {
        return new DemoCustomChatRowProvider();
    }

    @Override
    protected void registerExtendMenuItem() {
        //demo 这里不覆盖基类已经注册的item, item点击listener沿用基类的
        //super.registerExtendMenuItem();
        //增加扩展的item
        inputMenu.registerExtendMenuItem("拍照", R.mipmap.icon_pic_tack, ITEM_TAKE_PICTURE, R.id.chat_menu_take_pic, extendMenuItemClickListener);
        inputMenu.registerExtendMenuItem("相册", R.mipmap.icon_pic, ITEM_PICTURE, R.id.chat_menu_pic, extendMenuItemClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTEXT_MENU) {

        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_MAP) {
                double latitude = data.getDoubleExtra("latitude", 0);
                double longitude = data.getDoubleExtra("longitude", 0);
                String locationAddress = data.getStringExtra("address");
                if (locationAddress != null && !locationAddress.equals("")) {
                    sendLocationMessage(latitude, longitude, locationAddress, toChatUsername);
                } else {
                }
            } else if (requestCode == REQUEST_CODE_SHORTCUT) {
                String content = data.getStringExtra("content");
                if (!TextUtils.isEmpty(content)) {
                    inputMenu.setInputMessage(content);
                }
            } else if (requestCode == REQUEST_CODE_EVAL) {
                messageList.refresh();
            }
        }

    }

    @Override
    public void onMessageSent() {
        messageList.refreshSelectLast();
    }

    /**
     * chat row provider
     */
    private final class DemoCustomChatRowProvider implements CustomChatRowProvider {

        @Override
        public int getCustomChatRowTypeCount() {
            //地图 和 满意度 发送接收 共4种
            //订单 和 轨迹 发送接收共4种
            // form 发送接收2种
            return MESSAGE_TYPE_COUNT;
        }

        @Override
        public int getCustomChatRowType(Message message) {
            //此处内部有用到,必须写否则可能会出现错位
            if (message.getType() == Message.Type.LOCATION){
                return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_MAP : MESSAGE_TYPE_SENT_MAP;
            }else if (message.getType() == Message.Type.TXT){
                switch (MessageHelper.getMessageExtType(message)) {
                    case EvaluationMsg:
                        return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EVAL : MESSAGE_TYPE_SENT_EVAL;
                    case OrderMsg:
                        return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_ORDER : MESSAGE_TYPE_SENT_ORDER;
                    case TrackMsg:
                        return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TRACK : MESSAGE_TYPE_SENT_TRACK;
                    case FormMsg:
                        return message.direct() == Message.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FORM : MESSAGE_TYPE_SENT_FORM;
                }
            }

            return -1;
        }

        @Override
        public ChatRow getCustomChatRow(Message message, int position, BaseAdapter adapter) {


            return null;
        }
    }

}
