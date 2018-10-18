package com.zhongti.huacailauncher.ui.lottery.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shuheming on 2018/1/31 0031.
 */

public class LotterMsgAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<String> mData = new ArrayList<>();

    private int mListSize = 0;
    private AppComponent appComponent;
    private SimpleDateFormat dateFormat;

    public LotterMsgAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setNewData(List<String> data) {
        this.mData = data == null ? new ArrayList<>() : data;
        mListSize = mData.size();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_lottery_msg, parent, false);
        return new HomeNewsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mListSize == 0) {
            return;
        }
        if (appComponent == null && mContext != null) {
            appComponent = ArmsUtils.obtainAppComponentFromContext(mContext);
        }
        int index = position % mListSize;
        HomeNewsViewHolder newsViewHolder = (HomeNewsViewHolder) holder;
        String msg = mData.get(index);
        if (TextUtils.isEmpty(msg)) return;
        newsViewHolder.tvMsg.setText(TextUtils.isEmpty(msg) ? "" : msg);
        newsViewHolder.rootView.setOnClickListener(v -> {
        });
    }


    @Override
    public int getItemCount() {
        return mListSize > 0 ? Integer.MAX_VALUE : 0;
    }

    public List<String> getData() {
        return mData;
    }

    private class HomeNewsViewHolder extends RecyclerView.ViewHolder {

        TextView tvMsg;
        FrameLayout rootView;

        HomeNewsViewHolder(View view) {
            super(view);
            rootView = view.findViewById(R.id.fl_item_lottery_root);
            tvMsg = view.findViewById(R.id.tv_item_lottery_msg);
        }
    }
}
