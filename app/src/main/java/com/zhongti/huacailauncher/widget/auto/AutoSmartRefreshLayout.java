package com.zhongti.huacailauncher.widget.auto;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhy.autolayout.AutoLayoutInfo;
import com.zhy.autolayout.utils.AutoLayoutHelper;

public class AutoSmartRefreshLayout extends SmartRefreshLayout {
    private AutoLayoutHelper mHelper = new AutoLayoutHelper(this);

    public AutoSmartRefreshLayout(Context context) {
        super(context);
    }

    public AutoSmartRefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoSmartRefreshLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isInEditMode())
            mHelper.adjustChildren();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public SmartRefreshLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AutoSmartRefreshLayout.LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends SmartRefreshLayout.LayoutParams
            implements AutoLayoutHelper.AutoLayoutParams {
        private AutoLayoutInfo mAutoLayoutInfo;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            mAutoLayoutInfo = AutoLayoutHelper.getAutoLayoutInfo(c, attrs);
        }

        @Override
        public AutoLayoutInfo getAutoLayoutInfo() {
            return mAutoLayoutInfo;
        }


        public LayoutParams(int width, int height) {
            super(width, height);
        }


        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

    }
}