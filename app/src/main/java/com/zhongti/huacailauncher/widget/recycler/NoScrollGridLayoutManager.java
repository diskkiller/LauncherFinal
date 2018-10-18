package com.zhongti.huacailauncher.widget.recycler;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by SHM on 2017/04/20.
 */
public class NoScrollGridLayoutManager extends GridLayoutManager {

    private boolean isScrollEnabled;

    public NoScrollGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public NoScrollGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public NoScrollGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    public void setScrollEnabled(boolean flag) {
        this.isScrollEnabled = flag;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}
