package com.hykc.cityfreight.view;

import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hykc.cityfreight.R;
import com.hykc.cityfreight.activity.MyCardActivity;
import com.hykc.cityfreight.adapter.SelectMyCardAdapter;
import com.hykc.cityfreight.entity.UCardEntity;

import java.io.Serializable;
import java.util.List;


/**
 * Created by Administrator on 2018/3/30.
 */

public class TXMoneyDialog extends DialogFragment {
    private ImageView mImageClose;
    private TextView mTitle;
    private List<UCardEntity> mList;
    private ImageView mImgAdd;
    private RelativeLayout mLayoutNo;
    private ListView mListView;
    OnSelectListener listener;
    private RelativeLayout mLayoutTips;
    private TextView mTextTips;
    private boolean isShowTips=false;
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

        return inflater.inflate(R.layout.layout_txmoney, container, true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mList= (List<UCardEntity>) getArguments().getSerializable("list");
        isShowTips=getArguments().getBoolean("isShowTips");
        mTitle = view.findViewById(R.id.tv_title);
        mImageClose = view.findViewById(R.id.img_close);
        mLayoutNo=view.findViewById(R.id.layout_nocard);
        mImgAdd=view.findViewById(R.id.img_add);
        mListView=view.findViewById(R.id.listView);
        mLayoutTips=view.findViewById(R.id.layout_tips);
        mTextTips=view.findViewById(R.id.tv_tips);
        if(isShowTips){
            mLayoutTips.setVisibility(View.VISIBLE);
        }else {
            mLayoutTips.setVisibility(View.GONE);
        }
        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent=new Intent(getActivity(), MyCardActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        if(mList==null || mList.size()==0){
            mLayoutNo.setVisibility(View.VISIBLE);
        }
        initDatas();

    }

    private void initDatas() {
        SelectMyCardAdapter adapter=new SelectMyCardAdapter(getActivity(),mList);
        mListView.setAdapter(adapter);
        adapter.setOnCardItemClickListener(new SelectMyCardAdapter.OnCardItemClickListener() {
            @Override
            public void onCardItemClick(int pos, UCardEntity entity) {
                dismiss();
                if(listener!=null){
                    listener.onSelect(pos,entity);
                }

            }
        });

    }

    public static TXMoneyDialog getInstance(List<UCardEntity> list) {
        TXMoneyDialog dialog = new TXMoneyDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) list);
        bundle.putBoolean("isShowTips",false);
        dialog.setArguments(bundle);
        return dialog;
    }

    public static TXMoneyDialog getInstance(List<UCardEntity> list,boolean isShowTips) {
        TXMoneyDialog dialog = new TXMoneyDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) list);
        bundle.putBoolean("isShowTips",isShowTips);
        dialog.setArguments(bundle);
        return dialog;
    }

    public interface OnSelectListener{
        void onSelect(int pos, UCardEntity entity);
    }
    public void setOnSelectListener(OnSelectListener listener){
        this.listener=listener;

    }


}
