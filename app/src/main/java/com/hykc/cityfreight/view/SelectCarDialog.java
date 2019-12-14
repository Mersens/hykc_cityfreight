package com.hykc.cityfreight.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.adapter.SelectCarAdapter;
import com.hykc.cityfreight.entity.UCarEntity;

import java.io.Serializable;
import java.util.List;


/**
 * Created by Administrator on 2018/3/30.
 */

public class SelectCarDialog extends DialogFragment {
    private ImageView mImageClose;
    private List<UCarEntity> mList;
    private ListView mListView;
    OnSelectListener listener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL,R.style.NoticeDialogStyle);

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return inflater.inflate(R.layout.layout_select_car_list, container, true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mList= (List<UCarEntity>) getArguments().getSerializable("list");
        mImageClose = view.findViewById(R.id.img_close);
        mListView=view.findViewById(R.id.listView);
        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        initDatas();

    }

    private void initDatas() {
        SelectCarAdapter adapter=new SelectCarAdapter(getActivity(),mList);
        mListView.setAdapter(adapter);
        adapter.setOnCarItemClickListener(new SelectCarAdapter.OnCarItemClickListener() {
            @Override
            public void onCarItemClick(int pos, UCarEntity entity) {
                if(listener!=null){
                    dismiss();
                    listener.onSelect(pos,entity);
                }
            }
        });



    }

    public static SelectCarDialog getInstance(List<UCarEntity> list) {
        SelectCarDialog dialog = new SelectCarDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) list);
        dialog.setArguments(bundle);
        return dialog;
    }


    public interface OnSelectListener{
        void onSelect(int pos, UCarEntity entity);
    }
    public void setOnSelectListener(OnSelectListener listener){
        this.listener=listener;

    }


}
