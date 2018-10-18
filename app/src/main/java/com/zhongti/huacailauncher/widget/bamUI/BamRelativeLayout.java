package com.zhongti.huacailauncher.widget.bamUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zhongti.huacailauncher.R;
import com.zhy.autolayout.AutoRelativeLayout;

/**
 * 自定义 LinearLayout 支持点击缩小、松开放大的功能
 * <p>
 * 由于是点击效果，所以使用这些效果的控件必须要设置OnClick事件！谨记！
 *
 * @author Bamboy
 */
public class BamRelativeLayout extends AutoRelativeLayout {

    /**
     * 动画模式【true：华丽效果——缩放加方向】【false：只缩放】
     * <p>
     * 华丽效果：
     * 即点击控件的 上、下、左、右、中间时的效果都不一样
     * <p>
     * 普通效果：
     * 即点击控件的任意部位，都只是缩放效果，与 华丽效果模式下 点击控件中间时的动画一样
     **/
    private boolean superb = false;

    /**
     * 顶点判断【0：中间】【1：上】【2：右】【3：下】【4：左】
     **/
    private int pivot = 0;

    public BamRelativeLayout(Context context) {
        this(context, null);
        init(context, null);
    }

    public BamRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BamRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs) {
        if (context == null || attrs == null) return;
        @SuppressLint("CustomViewStyleable") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.bam_view);
        superb = typedArray.getBoolean(R.styleable.bam_view_open_super, false);
        typedArray.recycle();
    }

    /**
     * 打开华丽效果
     */
    public void openSuperb() {
        superb = true;
    }

    /**
     * 关闭华丽效果
     */
    public void closeSuperb() {
        superb = false;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            // 手指按下
            case MotionEvent.ACTION_DOWN:
                pivot = BamUI.startAnimDown(this, superb, event.getX(), event.getY());
                break;

            // 触摸动作取消
            case MotionEvent.ACTION_CANCEL:
                // 手指抬起
            case MotionEvent.ACTION_UP:
                BamUI.startAnimUp(this, pivot);
                break;

            default:
                break;
        }

        return super.onTouchEvent(event);
    }
}
