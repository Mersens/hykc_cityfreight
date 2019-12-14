package com.hykc.cityfreight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.ZDriver;

import java.util.List;

/**
 * Created by Administrator on 2018/3/27.
 */

public class BalanceDetailAdapter extends BaseAdapter {
    private Context mContext;
    private List<ZDriver> mList;
    private LayoutInflater mInflater;

    public BalanceDetailAdapter(Context context, List<ZDriver> list) {
        this.mContext = context;
        this.mList = list;
        mInflater = LayoutInflater.from(mContext);

    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if(view==null){
            holder=new ViewHolder();
            view=mInflater.inflate(R.layout.layout_balance_detial_item,null);
            holder.mTextType=view.findViewById(R.id.tv_type);
            holder.mTextType_Value=view.findViewById(R.id.tv_zc);
            holder.mTextTime=view.findViewById(R.id.tv_time);
            view.setTag(holder);

        }else {
            holder=(ViewHolder)view.getTag();
        }
        ZDriver entity=mList.get(i);
        int type=entity.getType();
        if(type==1){
            holder.mTextType.setText("运费收款");
        }else if(type==2){
            holder.mTextType.setText("充值");
        }else if(type==3){
            holder.mTextType.setText("提现");
        }else {
            holder.mTextType.setText("其他");
        }
        holder.mTextTime.setText(entity.getCreateTime());
        holder.mTextType_Value.setText(entity.getPrice()+"");
        return view;
    }

    static class ViewHolder {
        public TextView mTextType;
        public TextView mTextType_Value;
        public TextView mTextTime;
    }

}
