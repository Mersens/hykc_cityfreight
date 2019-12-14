package com.hykc.cityfreight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.UCardEntity;

import java.util.List;

public class SelectMyCardAdapter extends BaseAdapter {
    private Context context;
    private List<UCardEntity> mList;
    private LayoutInflater mInflater;
    OnCardItemClickListener listener;

    public SelectMyCardAdapter(Context context, List<UCardEntity> list) {
        this.context = context;
        this.mList = list;
        mInflater = LayoutInflater.from(context);

    }

    public void setList(List<UCardEntity> list) {
        this.mList = list;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_select_card_item, null);
            holder.itemView = convertView;
            holder.mCheckBox = convertView.findViewById(R.id.checkBox);
            holder.mImgType = convertView.findViewById(R.id.img_type);
            holder.mTextType = convertView.findViewById(R.id.tv_type);
            holder.mTextAccount = convertView.findViewById(R.id.tv_zh);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final UCardEntity entity = mList.get(position);
        final ViewHolder myHolder = holder;
        int type=entity.getCardType();
        if(type==1){
            holder.mImgType.setImageResource(R.mipmap.icon_zfb);
            holder.mTextType.setText(entity.getName());
            holder.mTextAccount.setText(entity.getAccount());
        }else if(type==2){
            holder.mImgType.setImageResource(R.mipmap.icon_wx);
            holder.mTextType.setText(entity.getName());
            holder.mTextAccount.setText(entity.getAccount());
        }else if(type==3){
            holder.mImgType.setImageResource(R.mipmap.icon_yhk);
            holder.mTextType.setText(entity.getName());
            holder.mTextAccount.setText(entity.getAccount());
        }
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    myHolder.mCheckBox.setChecked(true);
                    myHolder.mCheckBox.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCardItemClick(position, entity);
                        }
                    }, 300);

                }
            }
        });

        return convertView;
    }


    public static class ViewHolder {
        private View itemView;
        private ImageView mImgType;
        private TextView mTextType;
        private TextView mTextAccount;
        private CheckBox mCheckBox;

    }

    public void setOnCardItemClickListener(OnCardItemClickListener listener) {
        this.listener = listener;

    }


    public interface OnCardItemClickListener {
        void onCardItemClick(int pos, UCardEntity entity);
    }



}
