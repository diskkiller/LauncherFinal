package com.zhongti.huacailauncher.ui.personal.adapter;

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
import com.zhongti.huacailauncher.app.config.net.img.RoundD;
import com.zhongti.huacailauncher.app.utils.NumUtils;
import com.zhongti.huacailauncher.model.entity.PrivateLottiListEntity;
import com.zhongti.huacailauncher.utils.code.TimeUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Create by ShuHeMing on 18/6/6
 */
public class PrivateLotti1Adapter extends BaseQuickAdapter<PrivateLottiListEntity, BaseViewHolder> {

    private AppComponent appComponent;
    private int colorR;
    private int colorB;

    public PrivateLotti1Adapter(int layoutResId, @Nullable List<PrivateLottiListEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected BaseViewHolder createBaseViewHolder(View view) {
        AutoUtils.auto(view);
        return super.createBaseViewHolder(view);
    }

    @Override
    protected void convert(BaseViewHolder helper, PrivateLottiListEntity item) {
        if (appComponent == null && mContext != null) {
            appComponent = ArmsUtils.obtainAppComponentFromContext(mContext);
        }
        ImageView ivThumb = helper.getView(R.id.iv_item_private_lotti_buy);
        if (!TextUtils.isEmpty(item.getThumbnail()) && mContext != null && appComponent != null && ivThumb != null) {
            int radius = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x10);
            appComponent.imageLoader().loadImage(mContext,
                    CustomImgConfigImpl
                            .builder()
                            .url(item.getThumbnail())
                            .placeholder(R.drawable.img_place_square)
                            .errorPic(R.drawable.img_place_square)
                            .loadShape(CustomImgConfigImpl.ROUNDED)
                            .rounded(new RoundD(radius, radius, radius, radius))
                            .imageView(ivThumb)
                            .build()
            );
        } else {
            //临时
            if (ivThumb != null)
                ivThumb.setImageResource(R.drawable.img_place_square);
        }
        TextView tvProgress = helper.getView(R.id.tv_item_private_lotti_progress);
        if (mContext != null && colorR == 0) {
            colorR = ArmsUtils.getColor(mContext, R.color.private_lotti_item1_red);
            colorB = ArmsUtils.getColor(mContext, R.color.black3);
        }
        if (item.getCashed() == item.getCount()) {
            tvProgress.setTextColor(colorB);
        } else {
            tvProgress.setTextColor(colorR);
        }
        helper.setText(R.id.tv_item_private_buy_orId, "订单号:" + (TextUtils.isEmpty(item.getCode()) ? "" : item.getCode()))
                .setText(R.id.tv_item_private_lotti_name, TextUtils.isEmpty(item.getName()) ? "" : item.getName())
                .setText(R.id.tv_item_private_lotti_price, "面值：￥" + (TextUtils.isEmpty(item.getPrice()) ? "0" : item.getPrice()))
                .setText(R.id.tv_item_private_lotti_progress, "进度：" + item.getCashed() + "/" + item.getCount())
                .setText(R.id.tv_item_private_lotti_time, item.getCreateTime() == 0 ? "" : TimeUtils.millis2String(item.getCreateTime()))
                .setText(R.id.tv_item_private_lotti_spend, getSpend(item.getType(), item.getMoney()))
                .setText(R.id.tv_item_private_lotti_count, "x" + item.getCount())
                .addOnClickListener(R.id.btn_item_private_buy_del)
                .addOnClickListener(R.id.btn_item_private_buy_pw);
    }

    private String getSpend(int type, int money) {
        switch (type) {
            case 1:
                return "￥" + NumUtils.getTwoDoubleNum(money);
            case 5:
                return TextUtils.concat(String.valueOf(money), " 金豆").toString();
            default:
                return "￥" + NumUtils.getTwoDoubleNum(money);
        }
    }
}
