package com.hykc.cityfreight.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.SuggestionEntity;

import java.util.List;


public class MySuggAdapter extends RecyclerView.Adapter<MySuggAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private OnItemBtnClickListener listener;
    private List<SuggestionEntity> list;
    private String []types=new String[]{
            "使用问题",
            "功能建议",
            "程序问题",
            "其他反馈"};

    public MySuggAdapter(Context mContext, List<SuggestionEntity> list){
        this.mContext=mContext;
        this.list=list;
        mInflater=LayoutInflater.from(mContext);

    }

    public void setDatas(List<SuggestionEntity> list){
        this.list=list;
        notifyItemRangeChanged(0,this.list.size());
    }


    @NonNull
    @Override
    public MySuggAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=mInflater.inflate(R.layout.layout_msg_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MySuggAdapter.ViewHolder holder, final int i) {
        final SuggestionEntity entity=list.get(i);
        String questionType=entity.getQuestionType();
        if(questionType.equals(types[0])){
            holder.mTextTitle.setText(types[0]+"反馈");
        }else if(questionType.equals(types[1])){
            holder.mTextTitle.setText(types[1]+"反馈");
        }
        else if(questionType.equals(types[2])){
            holder.mTextTitle.setText(types[2]+"反馈");
        }
        else if(questionType.equals(types[3])){
            holder.mTextTitle.setText(types[3]+"反馈");
        }
        holder.mTextQuestionContent.setText(entity.getQuestionContent());
        String dealTime=entity.getDealTime();
        if(!TextUtils.isEmpty(dealTime)){
            holder.mTextDealTime.setText("处理时间："+dealTime);
        }else {
            holder.mTextDealTime.setText("");
        }

        holder.mTextDealMsg.setText(entity.getDealMsg());
        int statu=entity.getStatu();
        switch (statu){
            case 1:
                holder.mTextStatu.setText("已处理");
                holder.mTextStatu.setTextColor(mContext.getResources().getColor(R.color.actionbar_color));
                break;
            case 2:
                holder.mTextStatu.setText("未处理");
                holder.mTextStatu.setTextColor(mContext.getResources().getColor(R.color.colorAccent));
                break;

        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(i,entity);
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
        private TextView mTextQuestionContent;
        private TextView mTextDealTime;
        private TextView mTextDealMsg;
        private TextView mTextStatu;
        private TextView mTextTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardview);
            mTextQuestionContent=itemView.findViewById(R.id.tv_question_content);
            mTextDealTime=itemView.findViewById(R.id.tv_deal_time);
            mTextDealMsg=itemView.findViewById(R.id.tv_deal_msg);
            mTextStatu=itemView.findViewById(R.id.tv_statu);
            mTextTitle=itemView.findViewById(R.id.tv_title);
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

        void onItemClick(int pos, SuggestionEntity entity);

    }

}
