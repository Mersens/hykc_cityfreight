package com.hykc.cityfreight.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.ZDriver;

import java.util.List;


public class OrderMoneyAdapter extends RecyclerView.Adapter<OrderMoneyAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private OnItemBtnClickListener listener;
    private List<ZDriver> list;
    public OrderMoneyAdapter(Context mContext, List<ZDriver> list) {
        this.mContext = mContext;
        this.list = list;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setDatas(List<ZDriver> list) {
        this.list = list;
        notifyItemRangeChanged(0, this.list.size());
    }


    @NonNull
    @Override
    public OrderMoneyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.layout_money_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderMoneyAdapter.ViewHolder holder, final int i) {
        final ZDriver zDriver = list.get(i);

        holder.mTextTarget.setText(zDriver.getTarget());
        holder.mTextTime.setText(zDriver.getCreateTime());
        holder.mTextMoney.setText(zDriver.getSurplusPrice()+" 元");
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(i,zDriver);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout cardView;
        public TextView mTextTarget;
        public TextView mTextTime;
        public TextView mTextMoney;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.layout_item);
            mTextTarget = itemView.findViewById(R.id.tv_target);
            mTextTime = itemView.findViewById(R.id.tv_time);
            mTextMoney = itemView.findViewById(R.id.tv_money);

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemBtnClickListener(OnItemBtnClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemBtnClickListener {
        void onItemClick(int pos, ZDriver entity);

    }

}
