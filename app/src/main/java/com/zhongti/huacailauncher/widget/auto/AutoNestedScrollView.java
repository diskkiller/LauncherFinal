package com.zhongti.huacailauncher.widget.auto;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.zhy.autolayout.AutoLayoutInfo;
import com.zhy.autolayout.utils.AutoLayoutHelper;

public class AutoNestedScrollView extends NestedScrollView {
    private AutoLayoutHelper mHelper = new AutoLayoutHelper(this);

    public AutoNestedScrollView(Context context) {
        super(context);
    }

    public AutoNestedScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoNestedScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new AutoNestedScrollView.LayoutParams(getContext(), attrs);
    }


    public static class LayoutParams extends NestedScrollView.LayoutParams
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