package com.zhongti.huacailauncher.ui.lottery.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.model.entity.LotteryFunEntity;

import java.util.List;

/**
 * Create by ShuHeMing on 18/8/24
 */
public class LotteryFunListAdapter extends BaseQuickAdapter<LotteryFunEntity, BaseViewHolder> {

    private AppComponent appComponent;
    private RequestOptions requestOptions;

    public LotteryFunListAdapter(int layoutResId, @Nullable List<LotteryFunEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LotteryFunEntity item) {
        if (appComponent == null && mContext != null) {
            appComponent = ArmsUtils.obtainAppComponentFromContext(mContext);
        }
        ImageView ivThumb = helper.getView(R.id.iv_item_lottery_fun_play_thumb);
        if (!TextUtils.isEmpty(item.getImg()) && mContext != null && appComponent != null && ivThumb != null) {
            if (item.getImg().trim().endsWith(".gif")) {
                GlideArms.with(mContext)
                        .asGif()
                        .load(item.getImg().trim())
                        .centerCrop()
                        .placeholder(R.drawable.img_place_lottery_list)
                        .error(R.drawable.img_place_lottery_list)
                        .fallback(R.drawable.img_place_lottery_list)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(ivThumb);
            } else {
                GlideArms.with(mContext)
                        .load(item.getImg().trim())
                        .centerCrop()
                        .placeholder(R.drawable.img_place_lottery_list)
                        .error(R.drawable.img_place_lottery_list)
                        .fallback(R.drawable.img_place_lottery_list)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(ivThumb);
            }
        } else {
            if (ivThumb != null)
                ivThumb.setImageResource(R.drawable.img_place_lottery_list);
        }
        helper.setText(R.id.iv_item_lottery_fun_play_num, "已有" + item.getCount() + "人玩过");
    }
}
