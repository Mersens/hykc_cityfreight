package com.hykc.cityfreight.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.FuelsEntity;

import java.util.List;

public class FuelsListAdapter extends RecyclerView.Adapter<FuelsListAdapter.ViewHolder>  {
    private Context mContext;
    private List<FuelsEntity> mList;
    private LayoutInflater mInflater;
    public FuelsListAdapter(Context mContext, List<FuelsEntity> mList){
        this.mContext=mContext;
        this.mList=mList;
        mInflater=LayoutInflater.from(mContext);
    }
    public void setDatas(List<FuelsEntity> mList){
        this.mList=mList;
        notifyItemRangeChanged(0,this.mList.size());
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v1=mInflater.inflate(R.layout.layout_fuels_item,parent,false);
        return new ViewHolder(v1);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final FuelsEntity fuelsEntity=mList.get(position);
        holder.mTextFuelName.setText(fuelsEntity.getFuel_name());
        holder.mTextNum.setText("油品 "+(position+1));
        String strPrice=fuelsEntity.getPrice();
        if(!TextUtils.isEmpty(strPrice) && !"null".equals(strPrice)){
            double price=Double.valueOf(strPrice);
            double d1=price/100;
            holder. mTextPrice.setText(String.format("%.2f", d1)+"元");
        }
        String strGuidePrice=fuelsEntity.getGuide_price();
        if(!TextUtils.isEmpty(strGuidePrice) && !"null".equals(strGuidePrice)){
            double guideprice=Double.valueOf(strGuidePrice);
            double d2=guideprice/100;
            holder. mTextGuidePrice.setText(String.format("%.2f", d2)+"元");
        }

        String status=fuelsEntity.getStatus();
        if("0".equals(status)){
            holder.mTextFuelStatus.setText("正常");
            holder.mTextFuelStatus.setTextColor(mContext.getResources()
                    .getColor(R.color.actionbar_color));
        }else {
            holder.mTextFuelStatus.setText("停用");
            holder.mTextFuelStatus.setTextColor(mContext.getResources()
                    .getColor(R.color.colorAccent));
        }
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextFuelName;
        public TextView mTextPrice;
        public TextView mTextGuidePrice;
        public TextView mTextFuelStatus;
        public TextView mTextNum;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextFuelName=itemView.findViewById(R.id.tv_fuels_name);
            mTextPrice=itemView.findViewById(R.id.tv_price);
            mTextGuidePrice=itemView.findViewById(R.id.tv_guide_price);
            mTextFuelStatus=itemView.findViewById(R.id.tv_fuel_status);
            mTextNum=itemView.findViewById(R.id.tv_num);
        }
    }

}
