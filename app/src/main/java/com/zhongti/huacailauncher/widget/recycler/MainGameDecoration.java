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
public class MainGameDecoration extends RecyclerView.ItemDecoration {
    private int space;//声明间距 //使用构造函数定义间距

    public MainGameDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //获得当前item的位置
        int position = parent.getChildAdapterPosition(view);
        //根据position确定item需要留出的位置
        if (position != 0) outRect.left = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x50);
    }
}
