package com.hykc.cityfreight.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.hykc.cityfreight.R;

import java.util.ArrayList;
import java.util.List;

public class MyMsgActivity extends BaseActivity {
    private ImageView mImgBack;
    private ListView mListView;
    private List<String> mList=new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_msg);
        init();
    }

    @Override
    public void init() {
        mImgBack=findViewById(R.id.img_back);
        mListView=findViewById(R.id.listView);
        mList.add("1");
        mListView.setAdapter(new MyAdapter());
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });
    }


    class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mList.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater=LayoutInflater.from(MyMsgActivity.this);
            View view=inflater.inflate(R.layout.layout_msg_item,null);
            return view;
        }
    }
}
