package com.zhongti.huacailauncher.ui.lottery.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.model.entity.LottiOrListEntity;
import com.zhongti.huacailauncher.utils.code.StringUtils;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Create by ShuHeMing on 18/6/5
 */
public class HadPayListAdapter extends BaseQuickAdapter<LottiOrListEntity, BaseViewHolder> {
    public HadPayListAdapter(@Nullable List<LottiOrListEntity> data) {
        super(data);
        setMultiTypeDelegate(new MultiTypeDelegate<LottiOrListEntity>() {
            @Override
            protected int getItemType(LottiOrListEntity entity) {
                return entity.getStatus();
            }
        });
        getMultiTypeDelegate()
                .registerItemType(LottiOrListEntity.UNOPEN, R.layout.item_had_pay_unopen)
                .registerItemType(LottiOrListEntity.OPENED, R.layout.item_had_pay_open);
    }

    @Override
    protected BaseViewHolder createBaseViewHolder(View view) {
        AutoUtils.auto(view);
        return super.createBaseViewHolder(view);
    }

    @Override
    protected void convert(BaseViewHolder helper, LottiOrListEntity item) {
        switch (helper.getItemViewType()) {
            case LottiOrListEntity.UNOPEN:
                helper.setText(R.id.tv_had_pay_unopen_code, StringUtils.getTickSingleCode(item.getCode(), 13));
                break;
            case LottiOrListEntity.OPENED:
                helper.setText(R.id.tv_had_pay_open_code, StringUtils.getTickSingleCode(item.getCode(), 13));
                break;
        }
    }
}
