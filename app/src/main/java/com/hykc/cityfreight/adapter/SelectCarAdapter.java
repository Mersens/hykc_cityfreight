package com.hykc.cityfreight.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.entity.UCarEntity;

import java.util.List;

public class SelectCarAdapter extends BaseAdapter {
    private Context context;
    private List<UCarEntity> mList;
    private LayoutInflater mInflater;
    OnCarItemClickListener listener;

    public SelectCarAdapter(Context context, List<UCarEntity> list) {
        this.context = context;
        this.mList = list;
        mInflater = LayoutInflater.from(context);

    }

    public void setList(List<UCarEntity> list) {
        this.mList = list;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_select_car_item, null);
            holder.itemView = convertView;
            holder.mCheckBox = convertView.findViewById(R.id.checkBox);
            holder.mTextCPH = convertView.findViewById(R.id.tv_cph);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final UCarEntity entity = mList.get(position);
        final ViewHolder myHolder = holder;
        holder.mTextCPH.setText(entity.getLicensePlateNo());
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    myHolder.mCheckBox.setChecked(true);
                    myHolder.mCheckBox.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCarItemClick(position, entity);
                        }
                    }, 300);

                }
            }
        });

        return convertView;
    }


    public static class ViewHolder {
        private View itemView;
        private TextView mTextCPH;
        private CheckBox mCheckBox;
    }

    public void setOnCarItemClickListener(OnCarItemClickListener listener) {
        this.listener = listener;

    }


    public interface OnCarItemClickListener {
        void onCarItemClick(int pos, UCarEntity entity);
    }



}
