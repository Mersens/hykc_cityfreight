package com.hykc.cityfreight.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.MessageEntity;

import java.text.SimpleDateFormat;

import butterknife.BindView;


public class ReceiveTextHolder extends BaseViewHolder {
    @BindView(R.id.iv_avatar)
    protected ImageView iv_avatar;

    @BindView(R.id.tv_time)
    protected TextView tv_time;

    @BindView(R.id.tv_message)
    protected TextView tv_message;

    public ReceiveTextHolder(Context context, ViewGroup root, OnRecyclerViewListener onRecyclerViewListener) {
        super(context, root, R.layout.item_chat_received_message,onRecyclerViewListener);
    }

    public void onAvatarClick(View view) {

    }

    @Override
    public void bindData(Object o) {
        final MessageEntity message = (MessageEntity) o;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String time = message.getMsg_time();
        tv_time.setText(time);
        tv_message.setText(message.getMsg());
        tv_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onRecyclerViewListener!=null){
                    onRecyclerViewListener.onItemClick(getAdapterPosition());
                }
            }
        });

        tv_message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onRecyclerViewListener != null) {
                    onRecyclerViewListener.onItemLongClick(getAdapterPosition());
                }
                return true;
            }
        });
    }

    public void showTime(boolean isShow) {
        tv_time.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

}
