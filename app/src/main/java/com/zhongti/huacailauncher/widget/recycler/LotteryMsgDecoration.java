package com.zhongti.huacailauncher.widget.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.utils.code.Utils;


/**
 * 投注记录Item间距
 * Created by DaMing on 2017/08/02.
 */

public class LotteryMsgDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        if (pos != 0) {
            outRect.top = ArmsUtils.getDimens(Utils.getApp(), R.dimen.y16);
        }
        outRect.left = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x26);
        outRect.right = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x22);
    }

}
