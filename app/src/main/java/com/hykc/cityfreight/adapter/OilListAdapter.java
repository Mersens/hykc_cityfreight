package com.hykc.cityfreight.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bumptech.glide.Glide;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.OilEntity;

import java.util.List;

public class OilListAdapter extends RecyclerView.Adapter<OilListAdapter.ViewHolder>  {
    private Context mContext;
    private List<OilEntity> mList;
    private LayoutInflater mInflater;
    private OnItemBtnClickListener listener;
    private String lat;
    private String lon;
    public OilListAdapter(Context mContext, List<OilEntity> mList,String lat,String lon){
        this.mContext=mContext;
        this.mList=mList;
        this.lat=lat;
        this.lon=lon;
        mInflater=LayoutInflater.from(mContext);
    }
    public void setDatas(List<OilEntity> mList,String lat,String lon){
        this.mList=mList;
        this.lat=lat;
        this.lon=lon;
        notifyItemRangeChanged(0,this.mList.size());
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v1=mInflater.inflate(R.layout.layout_oil_item,parent,false);
        return new ViewHolder(v1);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final OilEntity entity=mList.get(position);
        holder.mTextOilName.setText(entity.getStationName());
        holder.mTextOilAddress.setText(entity.getAddress());
        holder.mTextName.setText(entity.getBrand_name());
        Glide.with(mContext).load(entity.getLogo()).into(holder.mImgLogo);
        int oilStatus=entity.getIsStop();
        if(oilStatus==0){
            holder.mTextOilStatus.setText("正常营业");
            holder.mTextOilStatus.setTextColor(mContext.getResources()
                    .getColor(R.color.actionbar_color));
        }else {
            holder.mTextOilStatus.setText("停止营业");
            holder.mTextOilStatus.setTextColor(mContext.getResources()
                    .getColor(R.color.colorAccent));
        }
        if(!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lon)){
            LatLng start=new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
            LatLng end=new LatLng(Double.parseDouble(entity.getLat()),Double.parseDouble(entity.getLng()));
            double distance= DistanceUtil.getDistance(start,end);
            if(distance>1000){
                double d=distance/1000;
                holder.mTextDistance.setText(String.format("%.2f", d)+"千米");
            }else {
                holder.mTextDistance.setText(String.format("%.2f", distance)+"米");
            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(position,entity);
                }
            }
        });
        holder.mTextDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(position,entity);
                }
            }
        });
        holder.mTextMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onMapClick(position,entity);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        public TextView mTextOilName;
        public TextView mTextOilAddress;
        public TextView mTextOilStatus;
        public TextView mTextDistance;
        public TextView mTextDetail;
        public TextView mTextMap;
        public TextView mTextName;
        public ImageView mImgLogo;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardview);
            mTextOilName=itemView.findViewById(R.id.tv_oil_name);
            mTextOilAddress=itemView.findViewById(R.id.tv_oil_address);
            mTextOilStatus=itemView.findViewById(R.id.tv_oil_status);
            mTextDistance=itemView.findViewById(R.id.oil_distance);
            mTextDetail=itemView.findViewById(R.id.tv_xq);
            mTextMap=itemView.findViewById(R.id.tv_map);
            mTextName=itemView.findViewById(R.id.name);
            mImgLogo=itemView.findViewById(R.id.img_logo);
        }
    }



    public void setOnItemBtnClickListener(OnItemBtnClickListener listener){
        this.listener=listener;
    }

    public interface OnItemBtnClickListener{
        void onItemClick(int pos, OilEntity entity);
        void onMapClick(int pos, OilEntity entity);
    }

}
