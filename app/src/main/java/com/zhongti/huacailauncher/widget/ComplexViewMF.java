package com.zhongti.huacailauncher.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gongwen.marqueen.MarqueeFactory;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.model.entity.HomeTipsEntity;
import com.zhy.autolayout.AutoRelativeLayout;

public class ComplexViewMF extends MarqueeFactory<RelativeLayout, HomeTipsEntity> {
    private LayoutInflater inflater;

    public ComplexViewMF(Context mContext) {
        super(mContext);
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    protected RelativeLayout generateMarqueeItemView(HomeTipsEntity data) {
        AutoRelativeLayout mView = (AutoRelativeLayout) inflater.inflate(R.layout.home_maquee_layout, null);
        TextView tvText = mView.findViewById(R.id.tv_home_marquee_text);
        tvText.setText(data.getContent());
        return mView;
    }

}