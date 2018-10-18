package com.zhongti.huacailauncher.ui.lottery.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.model.entity.CLiveCodeListEntity;
import com.zhongti.huacailauncher.utils.code.StringUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Create by ShuHeMing on 18/7/10
 */
public class CheckTicketLiveAdapter extends BaseQuickAdapter<CLiveCodeListEntity.TicketListBean, BaseViewHolder> {

    private int colorR;
    private int colorY;
    private int colorB;

    public CheckTicketLiveAdapter(int layoutResId, @Nullable List<CLiveCodeListEntity.TicketListBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected BaseViewHolder createBaseViewHolder(View view) {
        AutoUtils.auto(view);
        return super.createBaseViewHolder(view);
    }

    @Override
    protected void convert(BaseViewHolder helper, CLiveCodeListEntity.TicketListBean item) {
        if (colorR == 0) {
            colorR = ArmsUtils.getColor(Utils.getApp(), R.color.live_red);
            colorY = ArmsUtils.getColor(Utils.getApp(), R.color.main_yellow);
            colorB = ArmsUtils.getColor(Utils.getApp(), R.color.black3);
        }
        TextView tvIndex = helper.getView(R.id.tv_item_cLive_ticket_index);
        TextView tvCode = helper.getView(R.id.tv_item_cLive_ticket_code);
        TextView tvStatus = helper.getView(R.id.tv_item_cLive_ticket_status);
        if (helper.getLayoutPosition() == 0) {
            tvIndex.setTextColor(colorB);
            tvCode.setTextColor(colorB);
            tvStatus.setTextColor(colorY);
            tvStatus.setText("进行中");
        } else {
            if (!TextUtils.isEmpty(item.getFlag())) {
                if (item.getFlag().equals("0")) {
                    tvIndex.setTextColor(colorB);
                    tvCode.setTextColor(colorB);
                    tvStatus.setTextColor(colorB);
                } else if (item.getFlag().equals("1")) {
                    tvIndex.setTextColor(colorR);
                    tvCode.setTextColor(colorR);
                    tvStatus.setTextColor(colorR);
                } else {
                    tvIndex.setTextColor(colorB);
                    tvCode.setTextColor(colorB);
                    tvStatus.setTextColor(colorB);
                }
            } else {
                tvIndex.setTextColor(colorB);
                tvCode.setTextColor(colorB);
                tvStatus.setTextColor(colorB);
            }
            tvStatus.setText("待验票");
        }
        tvIndex.setText(String.valueOf(item.getIndex()));
        tvCode.setText(StringUtils.getTickSingleCode(item.getCode(), 13));
    }
}
