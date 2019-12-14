package com.hykc.cityfreight.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.NewsEntity;

import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private OnItemBtnClickListener listener;
    private List<NewsEntity> list;
    public NewsAdapter(Context mContext, List<NewsEntity> list) {
        this.mContext = mContext;
        this.list = list;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setDatas(List<NewsEntity> list) {
        this.list = list;
        notifyItemRangeChanged(0, this.list.size());
    }


    @NonNull
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.layout_news_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsAdapter.ViewHolder holder, final int i) {
        final NewsEntity newsEntity = list.get(i);
        Glide.with(mContext)
                .load(newsEntity.getThumbnail_pic_s())
                .into(holder.mImg);
        holder.mTextAuthor.setText(newsEntity.getAuthor_name());
        holder.mTextTime.setText(newsEntity.getDate());
        holder.mTextTitle.setText(newsEntity.getTitle());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(i, newsEntity);
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
        public ImageView mImg;
        public TextView mTextTitle;
        public TextView mTextTime;
        public TextView mTextAuthor;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.layout_item);
            mImg = itemView.findViewById(R.id.img);
            mTextTitle = itemView.findViewById(R.id.tv_title);
            mTextTime = itemView.findViewById(R.id.time);
            mTextAuthor = itemView.findViewById(R.id.author);
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
        void onItemClick(int pos, NewsEntity newsEntity);

    }

}
