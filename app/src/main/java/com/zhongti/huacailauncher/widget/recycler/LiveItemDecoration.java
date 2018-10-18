package com.zhongti.huacailauncher.widget.recycler;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.utils.code.Utils;


/**
 * Created by DaMing on 2017/08/02.
 */

public class LiveItemDecoration extends RecyclerView.ItemDecoration {

    private Paint dividerPaint;
    private int dividerHeight;
    private int dividerColor;

    public LiveItemDecoration(int color) {
        dividerColor = color;
        dividerPaint = new Paint();
        dividerPaint.setColor(dividerColor);
        dividerHeight = ArmsUtils.getDimens(Utils.getApp(), R.dimen.y1);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int pos = parent.getChildAdapterPosition(view);
        if (pos != 0)
            outRect.top = dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawLineList(c, parent);
    }

    private void drawLineList(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        for (int i = 0; i < childCount; i++) {
            if (i > 0) {
                View view = parent.getChildAt(i);
                float top = view.getTop() - dividerHeight;
                float bottom = view.getTop();
                dividerPaint.setColor(dividerColor);
                c.drawRect(left, top, right, bottom, dividerPaint);
            }
        }
    }
}
