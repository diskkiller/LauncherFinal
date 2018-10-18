package com.zhongti.huacailauncher.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by SHM on 2017/02/28.
 */
public class SViewPager extends ViewPager {
    private boolean canScroll;
    private boolean enableAnim = true;

    public SViewPager(Context context) {
        super(context);
        canScroll = false;
    }

    public SViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        canScroll = false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (canScroll) {
            try {
                return super.onInterceptTouchEvent(ev);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canScroll) {
            return super.onTouchEvent(event);
        }
        return false;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, enableAnim);
    }

    public void toggleLock() {
        canScroll = !canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setEnableAnim(boolean enableAnim) {
        this.enableAnim = enableAnim;
    }
}
