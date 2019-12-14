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
import com.hykc.cityfreight.utils.WaybillStatus;

import java.util.List;


public class YWCAdapter extends RecyclerView.Adapter<YWCAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private OnItemBtnClickListener listener;
    private List<UWaybill> list;

    public YWCAdapter(Context mContext, List<UWaybill> list){
        this.mContext=mContext;
        this.list=list;
        mInflater=LayoutInflater.from(mContext);

    }

    public void setDatas(List<UWaybill> list){
        this.list=list;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public YWCAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=mInflater.inflate(R.layout.layout_dfh_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(YWCAdapter.ViewHolder holder, final int i) {
        final UWaybill uWaybill=list.get(i);
        holder.mTextFromCity.setText(uWaybill.getFromCity());
        holder.mTextToCity.setText(uWaybill.getToCity());
        holder.mTextCreatTime.setText(uWaybill.getCreateTime());
        //按钮显示
        holder.mTextGoodsName.setText(uWaybill.getGoodsName());
        int isDriverShowPrice=uWaybill.getIsDriverShowPrice();

        if(isDriverShowPrice==0){
            holder.mTextFhr.setText(uWaybill.getDriverPrice()+"元");

        }else {
            holder.mTextFhr.setText("****元");
        }
        holder.mTextZl.setText(uWaybill.getUcreditId()+"");
        holder.mTextShrName.setText(uWaybill.getConsigneeName());
        holder.mTextShrTel.setText(uWaybill.getConsigneePhone());
        holder.mTextFhrName.setText(uWaybill.getShipperName());
        holder.mTextFhrTel.setText(uWaybill.getShipperPhone());
        holder.mTextBz.setText(uWaybill.getRemarks());
       // setBtnData(holder,uWaybill,i);
        holder.mTextStatu.setVisibility(View.VISIBLE);
        holder.mTextStatu.setText("已完成");
        holder.mImgStutuType.setImageResource(R.mipmap.icon_statu_ywc);
        holder.mTextCancel.setVisibility(View.GONE);
        holder.mTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onCancelClick(i,uWaybill);
                }
            }
        });
        holder.mTextDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onDetailsClick(i,uWaybill);
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
        }
    }


    private void setBtnData(ViewHolder holder, final UWaybill uWaybill, final int pos){
        int status=uWaybill.getStatus();
        switch (status){
            case WaybillStatus.CJYD_STATUS://运单创建成功
                //接单
                holder.mTextCancel.setVisibility(View.VISIBLE);
                holder.mImgStutuType.setImageResource(R.mipmap.icon_statu_jd);
                holder.mImgStutuType.setVisibility(View.VISIBLE);
                holder.mImgStutuType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener!=null){
                            listener.onJDClick(pos,uWaybill);
                        }
                    }
                });
                holder.mTextStatu.setVisibility(View.VISIBLE);
                holder.mTextStatu.setText("未接单");
                break;
            case WaybillStatus.YJD_STATUS://已接单
                //配送
                holder.mTextCancel.setVisibility(View.GONE);
                holder.mImgStutuType.setImageResource(R.mipmap.icon_statu_ps);
                holder.mImgStutuType.setVisibility(View.VISIBLE);
                holder.mImgStutuType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener!=null){
                            listener.onPSClick(pos,uWaybill);
                        }
                    }
                });
                holder.mTextStatu.setVisibility(View.VISIBLE);
                holder.mTextStatu.setText("已接单");
                break;
            case WaybillStatus.KSPS_STATUS://开始配送
                //送达
                holder.mTextCancel.setVisibility(View.GONE);
                holder.mImgStutuType.setImageResource(R.mipmap.icon_statu_sd);
                holder.mImgStutuType.setVisibility(View.VISIBLE);
                holder.mImgStutuType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener!=null){
                            listener.onSDClick(pos,uWaybill);
                        }
                    }
                });
                holder.mTextStatu.setVisibility(View.VISIBLE);
                holder.mTextStatu.setText("开始配送");
                break;
            case WaybillStatus.YSD_STATUS://已送达
                //拍照想
                holder.mTextCancel.setVisibility(View.GONE);
                holder.mImgStutuType.setImageResource(R.mipmap.icon_statu_pz);
                holder.mImgStutuType.setVisibility(View.VISIBLE);
                holder.mImgStutuType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener!=null){
                            listener.onPZClick(pos,uWaybill);
                        }
                    }
                });
                holder.mTextStatu.setVisibility(View.VISIBLE);
                holder.mTextStatu.setText("已送达");
                break;
            case WaybillStatus.YQS_STATUS://已签收
                //已签收
                holder.mTextCancel.setVisibility(View.GONE);
                holder.mImgStutuType.setImageResource(R.mipmap.icon_statu_pz);
                holder.mImgStutuType.setVisibility(View.VISIBLE);
                holder.mImgStutuType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener!=null){
                            listener.onPZClick(pos,uWaybill);
                        }
                    }
                });
                holder.mTextStatu.setVisibility(View.VISIBLE);
                holder.mTextStatu.setText("已签收");
                break;
            case WaybillStatus.YJS_STATUS://已结算
                //已结算
                holder.mTextCancel.setVisibility(View.GONE);
                holder.mImgStutuType.setImageResource(R.mipmap.icon_statu_pz);
                holder.mImgStutuType.setVisibility(View.VISIBLE);
                holder.mImgStutuType.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(listener!=null){
                            listener.onPZClick(pos,uWaybill);
                        }
                    }
                });
                holder.mTextStatu.setVisibility(View.VISIBLE);
                holder.mTextStatu.setText("已结算");
                break;
                default:
                    holder.mTextCancel.setVisibility(View.GONE);
                    holder.mImgStutuType.setVisibility(View.GONE);
                    holder.mTextStatu.setVisibility(View.GONE);
                    break;
        }


    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemBtnClickListener(OnItemBtnClickListener listener){
        this.listener=listener;
    }

    public interface OnItemBtnClickListener{
        void onJDClick(int pos, UWaybill uWaybill);
        void onPSClick(int pos, UWaybill uWaybill);
        void onSDClick(int pos, UWaybill uWaybill);
        void onCancelClick(int pos, UWaybill uWaybill);
        void onDetailsClick(int pos, UWaybill uWaybill);
        void onItemClick(int pos, UWaybill uWaybill);
        void onPZClick(int pos, UWaybill uWaybill);
    }

}
