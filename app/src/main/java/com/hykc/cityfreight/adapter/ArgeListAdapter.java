package com.hykc.cityfreight.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.BestSignAgreementEntity;

import java.util.List;


public class ArgeListAdapter extends RecyclerView.Adapter<ArgeListAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private OnItemBtnClickListener listener;
    private List<BestSignAgreementEntity> list;

    public ArgeListAdapter(Context mContext, List<BestSignAgreementEntity> list){
        this.mContext=mContext;
        this.list=list;
        mInflater=LayoutInflater.from(mContext);

    }

    public void setDatas(List<BestSignAgreementEntity> list){
        this.list=list;
        notifyItemRangeChanged(0,this.list.size());
    }


    @NonNull
    @Override
    public ArgeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=mInflater.inflate(R.layout.layout_arge_list_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArgeListAdapter.ViewHolder holder, final int i) {
        final BestSignAgreementEntity entity=list.get(i);
        holder.mTextBH.setText(entity.getContractId());
        holder.mTextSJ.setText(entity.getSignTime());
        holder.mTextStatu.setText(entity.getSignMsg());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onDetailsClick(i,entity);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private View itemView;
        private TextView mTextBH;
        private TextView mTextSJ;
        private TextView mTextStatu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
            mTextBH=itemView.findViewById(R.id.tv_bh);
            mTextSJ=itemView.findViewById(R.id.tv_sj);
            mTextStatu=itemView.findViewById(R.id.tv_statu);
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

        void onDetailsClick(int pos, BestSignAgreementEntity entity);

    }

}
