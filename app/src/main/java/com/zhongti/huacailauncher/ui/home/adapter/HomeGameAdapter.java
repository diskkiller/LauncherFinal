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
import com.zhongti.huacailauncher.model.entity.HomeGameEntity;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Create by ShuHeMing on 18/5/25
 */
public class HomeGameAdapter extends BaseQuickAdapter<HomeGameEntity, BaseViewHolder> {

    private AppComponent appComponent;

    public HomeGameAdapter(int layoutResId, @Nullable List<HomeGameEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected BaseViewHolder createBaseViewHolder(View view) {
        AutoUtils.auto(view);
        return super.createBaseViewHolder(view);
    }

    @Override
    protected void convert(BaseViewHolder helper, HomeGameEntity item) {
        if (appComponent == null && mContext != null) {
            appComponent = ArmsUtils.obtainAppComponentFromContext(mContext);
        }
        ImageView ivThumb = helper.getView(R.id.iv_main_item_game_thumb);
        if (!TextUtils.isEmpty(item.getImgUrl()) && mContext != null && appComponent != null && ivThumb != null) {
            int radius = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x20);
            appComponent.imageLoader().loadImage(mContext,
                    CustomImgConfigImpl
                            .builder()
                            .url(item.getImgUrl())
                            .loadShape(CustomImgConfigImpl.ROUNDED)
                            .placeholder(R.drawable.img_place_game_list)
                            .errorPic(R.drawable.img_place_game_list)
                            .fallback(R.drawable.img_place_game_list)
                            .rounded(new RoundD(radius, radius, radius, radius))
                            .imageView(ivThumb)
                            .build()
            );
        } else {
            if (ivThumb != null) ivThumb.setImageResource(R.drawable.img_place_game_list);
        }
    }
}
