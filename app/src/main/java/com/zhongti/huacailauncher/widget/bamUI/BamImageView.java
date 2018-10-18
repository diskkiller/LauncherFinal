package com.zhongti.huacailauncher.widget.bamUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zhongti.huacailauncher.R;

/**
 * Create by ShuHeMing on 18/6/4
 */
public class BamImageView extends AppCompatImageView {

    private boolean superb = false;
    private int pivot = 0;

    public BamImageView(Context context) {
        this(context, null);
        init(context, null);
    }

    public BamImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BamImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
