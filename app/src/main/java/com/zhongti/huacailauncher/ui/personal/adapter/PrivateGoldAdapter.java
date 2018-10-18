package com.zhongti.huacailauncher.ui.personal.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.config.net.img.CustomImgConfigImpl;
import com.zhongti.huacailauncher.app.utils.NumUtils;
import com.zhongti.huacailauncher.model.entity.GoldRecordEntity;
import com.zhongti.huacailauncher.utils.code.TimeUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhy.autolayout.utils.AutoUtils;

import java.text.SimpleDateFormat;
import java.util.List;

public class PrivateGoldAdapter extends BaseQuickAdapter<GoldRecordEntity, BaseViewHolder> {

    private AppComponent appComponent;
    private SimpleDateFormat dateFormat;

    public PrivateGoldAdapter(int layoutResId, @Nullable List<GoldRecordEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected BaseViewHolder createBaseViewHolder(View view) {
        AutoUtils.auto(view);
        return super.createBaseViewHolder(view);
    }

    @SuppressLint({"SimpleDateFormat", "SetTextI18n"})
    @Override
    protected void convert(BaseViewHolder helper, GoldRecordEntity item) {
        if (appComponent == null && mContext != null) {
            appComponent = ArmsUtils.obtainAppComponentFromContext(mContext);
        }
        ImageView ivThumb = helper.getView(R.id.iv_private_item_gold_or_type);
        //加载图片
        if (!TextUtils.isEmpty(item.getUrl()) && mContext != null && appComponent != null && ivThumb != null) {
            appComponent.imageLoader().loadImage(mContext,
                    CustomImgConfigImpl
                            .builder()
                            .url(item.getUrl())
                            .placeholder(R.drawable.img_place_square)
                            .errorPic(R.drawable.img_place_square)
                            .loadShape(CustomImgConfigImpl.CIRCLE)
                            .imageView(ivThumb)
                            .build()
            );
        } else {
            if (ivThumb != null)
                ivThumb.setImageResource(R.drawable.img_place_square);
        }
        if (dateFormat == null) {
            dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
        }
        helper.setText(R.id.tv_private_item_gold_or_type, TextUtils.isEmpty(item.getName()) ? "" : item.getName())
                .setText(R.id.tv_private_item_gold_or_time, item.getCreateTime() == 0 ? "" : TimeUtils.millis2String(item.getCreateTime(), dateFormat))
                .setText(R.id.tv_private_item_gold_or_price, "¥" + NumUtils.twoDouble(String.valueOf(item.getPrice())));
        TextView tvNum = helper.getView(R.id.tv_private_item_gold_or_num);
        switch (item.getType()) {
            case 2:
                tvNum.setTextColor(ArmsUtils.getColor(Utils.getApp(), R.color.main_yellow));
                tvNum.setText("+" + item.getCount() + "个");
                break;
            default:
                tvNum.setTextColor(ArmsUtils.getColor(Utils.getApp(), R.color.private_gold_wast));
                tvNum.setText("-" + item.getCount() + "个");
                break;
        }
    }
}
