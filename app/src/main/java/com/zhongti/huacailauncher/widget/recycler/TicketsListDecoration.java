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

public class TicketsListDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        if (pos != 0 && pos != 1 && pos != 2) {
            outRect.bottom = ArmsUtils.getDimens(Utils.getApp(), R.dimen.y30);
        } else {
            outRect.top = ArmsUtils.getDimens(Utils.getApp(), R.dimen.y30);
            outRect.bottom = ArmsUtils.getDimens(Utils.getApp(), R.dimen.y30);
        }
        if (pos % 3 == 0) {
            outRect.left = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x50);
        } else {
            outRect.left = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x30);
        }
        if (pos % 3 == 2) {
            outRect.right = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x51);
        }
    }

}
