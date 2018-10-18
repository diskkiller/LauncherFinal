package com.zhongti.huacailauncher.ui.lottery.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.model.entity.LottiListEntity;
import com.zhongti.huacailauncher.utils.code.StringUtils;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Create by ShuHeMing on 18/6/4
 */
public class ScratchTicketListAdapter extends BaseQuickAdapter<LottiListEntity, BaseViewHolder> {
    private int colorS;
    private int colorN1;
    private int colorN2;

    public ScratchTicketListAdapter(int layoutResId, @Nullable List<LottiListEntity> data) {
        super(layoutResId, data);
    }

    @Override
    protected BaseViewHolder createBaseViewHolder(View view) {
        AutoUtils.auto(view);
        return super.createBaseViewHolder(view);
    }

    @Override
    protected void convert(BaseViewHolder helper, LottiListEntity item) {
        if (mContext != null && colorS == 0) {
            colorN1 = ArmsUtils.getColor(mContext, R.color.main_yellow);
            colorN2 = ArmsUtils.getColor(mContext, R.color.scratch_detail_rule_btn_bg);
            colorS = ArmsUtils.getColor(mContext, R.color.scratch_detail_ticket_checked);
        }
        ImageView ivCheck = helper.getView(R.id.iv_scratch_detail_check);
        TextView tvTicket = helper.getView(R.id.tv_scratch_detail_ticketNum);
        if (item.isCheck()) {
            ivCheck.setVisibility(View.VISIBLE);
            tvTicket.setTextColor(colorS);
        } else {
            ivCheck.setVisibility(View.GONE);
            if (mode == 1) {
                tvTicket.setTextColor(colorN2);
            } else if (mode == 2) {
                tvTicket.setTextColor(colorN1);
            }

        }
        String code;
        if (mode == 1) {
            code = item.getCode();
        } else if (mode == 2) {
            code = StringUtils.getTickSingleCode(item.getCode(), 13);
        } else {
            code = item.getCode();
        }
        helper.setText(R.id.tv_scratch_detail_ticketNum, TextUtils.isEmpty(code) ? "" : code);
    }

    private int mode;

    public void setMode(int mode) {
        this.mode = mode;
    }
}
