package com.hykc.cityfreight.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.UCardEntity;

import java.util.List;

public class MyCardAdapter extends BaseAdapter {
    private Context context;
    private List<UCardEntity> mList;
    private LayoutInflater mInflater;
    OnCardItemClickListener listener;

    public MyCardAdapter(Context context, List<UCardEntity> list){
        this.context=context;
        this.mList=list;

        mInflater=LayoutInflater.from(context);

    }

    public void setList(List<UCardEntity> list){
        this.mList=list;

        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mList==null?0:mList.size();
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
        ViewHolder holder=null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=mInflater.inflate(R.layout.mycard_item,null);
            holder.itemView=convertView.findViewById(R.id.cardview);
            holder.mTextAccount=convertView.findViewById(R.id.tv_account);
            holder.mTextType=convertView.findViewById(R.id.tv_type);
            holder.mTextName=convertView.findViewById(R.id.tv_name);
            holder.mImgDel=convertView.findViewById(R.id.img_del);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }

        final UCardEntity entity=mList.get(position);
        holder.mImgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onCardDelClick(position,entity);
                }
            }
        });

        String account=entity.getAccount();
        if(account.length()>4){
            holder.mTextAccount.setText(account.substring(account.length()-4,account.length()));

        }else {
            holder.mTextAccount.setText(account);
        }
        holder.mTextType.setText(entity.getName());
        int type=entity.getCardType();
        if(type==3){
            holder.mTextName.setText(entity.getBank());
        }else {
            holder.mTextName.setText(entity.getAccount());
        }

        if(!TextUtils.isEmpty(entity.getAddress())&& !TextUtils.isEmpty(entity.getBank())){
            holder.mTextName.setText(entity.getBank());
        }else {
            holder.mTextName.setText(entity.getName());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onCardItemClick(position,entity);
                }
            }
        });
        return convertView;
    }


    public static class ViewHolder{
        public CardView itemView;
        public TextView mTextName;
        public TextView mTextType;
        public TextView mTextAccount;
        public ImageView mImgDel;
    }

    public void setOnCardItemClickListener(OnCardItemClickListener listener){
        this.listener=listener;
    }

    public interface OnCardItemClickListener{
        void onCardItemClick(int pos, UCardEntity entity);
        void onCardDelClick(int pos, UCardEntity entity);
    }


}
