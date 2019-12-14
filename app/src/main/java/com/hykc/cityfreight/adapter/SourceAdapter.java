package com.hykc.cityfreight.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.UWaybill;

import java.util.List;


public class SourceAdapter extends RecyclerView.Adapter<SourceAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private OnItemBtnClickListener listener;
    private List<UWaybill> list;

    public SourceAdapter(Context mContext, List<UWaybill> list){
        this.mContext=mContext;
        this.list=list;
        mInflater=LayoutInflater.from(mContext);

    }

    public void setDatas(List<UWaybill> list){
        this.list=list;
        notifyItemRangeChanged(0,this.list.size());
    }


    @NonNull
    @Override
    public SourceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=mInflater.inflate(R.layout.layout_source_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SourceAdapter.ViewHolder holder, final int i) {
        final UWaybill uWaybill=list.get(i);
        holder.mTextFromCity.setText(uWaybill.getFromCity());
        holder.mTextToCity.setText(uWaybill.getToCity());
        holder.mTextCreatTime.setText(uWaybill.getCreateTime());
        //按钮显示
        holder.mTextGoodsName.setText(uWaybill.getGoodsName());
        holder.mTextFhr.setText(uWaybill.getShipperName());
        holder.mTextZl.setText(uWaybill.getUcreditId()+"");
        holder.mTextShrName.setText(uWaybill.getConsigneeName());
        holder.mTextShrTel.setText(uWaybill.getConsigneePhone());
        holder.mTextFhrName.setText(uWaybill.getShipperName());
        holder.mTextFhrTel.setText(uWaybill.getShipperPhone());
        holder.mTextBz.setText(uWaybill.getRemarks());
        holder.mTextCC.setText(uWaybill.getCarLength()+" 米");
        holder.mTextZZ.setText(uWaybill.getCarLoad()+" 吨");
        setBtnData(holder,uWaybill,i);
        holder.mTextDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(i,uWaybill);
                }
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(i,uWaybill);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private CardView cardView;
        private TextView mTextFromCity;
        private TextView mTextToCity;
        private TextView mTextCreatTime;
        private ImageView mImgStutuType;
        private TextView mTextGoodsName;
        private TextView mTextFhr;
        private TextView mTextZl;
        private TextView mTextShrName;
        private TextView mTextShrTel;
        private TextView mTextFhrName;
        private TextView mTextFhrTel;
        private TextView mTextBz;
        private TextView mTextDetails;
        private TextView mTextCancel;
        private TextView mTextStatu;
        private TextView mTextCC;
        private TextView mTextZZ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardview);
            mTextFromCity=itemView.findViewById(R.id.tv_start_addr);
            mTextToCity=itemView.findViewById(R.id.tv_end_addr);
            mTextCreatTime=itemView.findViewById(R.id.tv_creatTime);
            mImgStutuType =itemView.findViewById(R.id.img_statu_type);
            mTextGoodsName=itemView.findViewById(R.id.tv_hwmc);
            mTextFhr=itemView.findViewById(R.id.tv_fhr);
            mTextZl=itemView.findViewById(R.id.tv_zl);
            mTextShrName=itemView.findViewById(R.id.tv_shrname);
            mTextShrTel =itemView.findViewById(R.id.tv_shrdh);
            mTextFhrName=itemView.findViewById(R.id.tv_fhrname);
            mTextFhrTel=itemView.findViewById(R.id.tv_fhrdh);
            mTextBz=itemView.findViewById(R.id.tv_bz);
            mTextDetails=itemView.findViewById(R.id.tv_xq);
            mTextCancel=itemView.findViewById(R.id.tv_jj);
            mTextStatu=itemView.findViewById(R.id.tv_status);
            mTextCC=itemView.findViewById(R.id.tv_yqcc);
            mTextZZ=itemView.findViewById(R.id.tv_yqzz);
        }
    }


    private void setBtnData(ViewHolder holder, final UWaybill uWaybill, final int pos){
        //接单
        holder.mTextCancel.setVisibility(View.GONE);
        holder.mImgStutuType.setImageResource(R.mipmap.icon_statu_qd);
        holder.mImgStutuType.setVisibility(View.VISIBLE);
        holder.mImgStutuType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onQDClick(pos,uWaybill);
                }
            }
        });
        holder.mTextStatu.setVisibility(View.VISIBLE);
        holder.mTextStatu.setText("未抢单");


    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemBtnClickListener(OnItemBtnClickListener listener){
        this.listener=listener;
    }

    public interface OnItemBtnClickListener{
        void onItemClick(int pos, UWaybill uWaybill);

        void onQDClick(int pos, UWaybill uWaybill);
    }

}
