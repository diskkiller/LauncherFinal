package com.zhongti.huacailauncher.ui.home.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.config.net.img.CustomImgConfigImpl;
import com.zhongti.huacailauncher.app.config.net.img.RoundD;
import com.zhongti.huacailauncher.model.entity.HomeLottiEntity;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Create by ShuHeMing on 18/5/25
 */
public class HomeLotteryAdapter extends BaseQuickAdapter<HomeLottiEntity, BaseViewHolder> {

    private AppComponent appComponent;

    public HomeLotteryAdapter(int layoutResId, @Nullable List<HomeLottiEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected BaseViewHolder createBaseViewHolder(View view) {
        AutoUtils.auto(view);
        return super.createBaseViewHolder(view);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeLottiEntity item) {
        if (appComponent == null && mContext != null) {
            appComponent = ArmsUtils.obtainAppComponentFromContext(mContext);
        }
        ImageView ivThumb = helper.getView(R.id.iv_item_main_lottery_bg);
        if (!TextUtils.isEmpty(item.getHomeImg()) && mContext != null && appComponent != null && ivThumb != null) {
            int radius = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x10);
            appComponent.imageLoader().loadImage(mContext,
                    CustomImgConfigImpl
                            .builder()
                            .url(item.getHomeImg())
                            .errorPic(R.drawable.img_place_lottery_list)
                            .placeholder(R.drawable.img_place_lottery_list)
                            .loadShape(CustomImgConfigImpl.ROUNDED)
                            .rounded(new RoundD(radius, radius, radius, radius))
                            .imageView(ivThumb)
                            .build()
            );
        } else {
            if (ivThumb != null) ivThumb.setImageResource(R.drawable.img_place_lottery_list);
        }
        helper.setText(R.id.tv_item_main_lottery_award_t, "最高奖金")
                .setText(R.id.tv_item_main_lottery_award, String.valueOf(item.getReward() / 10000))
                .setText(R.id.tv_item_main_lottery_award_unit, "万元")
                .setText(R.id.tv_item_main_lottery_price, "面值")
                .setText(R.id.tv_item_main_lottery_price_num, TextUtils.isEmpty(item.getPrice()) ? "0" : item.getPrice())
                .setText(R.id.tv_item_main_lottery_price_unit, "元")
                .setText(R.id.tv_item_main_lottery_sale, "销量 " + (TextUtils.isEmpty(item.getSales()) ? "0" : item.getSales()));
    }
}
