package com.zhongti.huacailauncher.widget.recycler;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.utils.code.Utils;

/**
 * Created by SHM on 2017/04/24.
 * 定义GridlayoutManager的recyclerView 两列情况下设置顶部和左右的间距
 */
public class GridLottiDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //获得当前item的位置
//        int position = parent.getChildAdapterPosition(view);
//        if (position % 3 == 0) {
//            outRect.left = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x60);
//        }
        outRect.right = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x40);
        outRect.bottom = ArmsUtils.getDimens(Utils.getApp(), R.dimen.y40);
    }
}
