package com.zhongti.huacailauncher.widget.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zhongti.huacailauncher.utils.code.ScreenUtils;

import java.lang.ref.WeakReference;

/**
 * Created by hardy.xiang on 2017/3/20.
 * 自动滚动的RecyclerView
 */
public class AutoPollRecyclerView extends RecyclerView {
    public static final String TAG = AutoPollRecyclerView.class.getSimpleName();

    private Context mContext;
    //滚动速度
    private static final long TIME_AUTO_POLL = 16;//16;
    AutoPollTask autoPollTask;
    private boolean running; //标示是否正在自动轮询

    private boolean mCanRun = false;//标示是否可以自动轮询

    private int mPagerCount = 0;

    public AutoPollRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        autoPollTask = new AutoPollTask(this);
    }

    public void setPagerCount(int pagerCount) {
        mPagerCount = pagerCount;
    }

    public void setCanRun(boolean canRun) {
        mCanRun = canRun;
    }

    //开启:如果正在运行,先停止->再开启
    public void start() {
        start(0);
    }

    /**
     * 延时启动滚动事件
     *
     * @param timeDelay
     */
    public void start(int timeDelay) {
//        TLog.d(TAG, "start --- timeDelay = " + timeDelay);
        if (running) {
            stop();
        }

        if (mContext != null && !ScreenUtils.isScreenOn(mContext)) {
//            TLog.d(TAG, "start --- is ScreenOn is false, return!");
            return;
        }

        if (mPagerCount == 0) {
//            TLog.d(TAG, "start --- mPagerCount is 0, return!");
            return;
        }

        if (mCanRun == true) {
            running = true;
            postDelayed(autoPollTask, TIME_AUTO_POLL + timeDelay);
        } else {
//            TLog.d(TAG, "start --- mCanRun is false, not run!");
        }
    }

    public void stop() {
//        TLog.d(TAG, "--- stop ---");

        running = false;
        removeCallbacks(autoPollTask);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
//        TLog.e(TAG, "--- onInterceptTouchEvent ---");
        //由于item有点击事件，监听不到 MotionEvent.ACTION_DOWN，只好在这里出发暂停

        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean isDone = super.dispatchTouchEvent(ev);
//        TLog.e(TAG, "--- dispatchTouchEvent --- isDone =" + isDone);

        if (running) {
            stop();
        }
        //延时1秒启动，解决触摸事件没有监听到，导致滚动停止问题
        start(1000);
        return isDone;

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
//        TLog.d(TAG, "onTouchEvent --- e.getAction() = " + e.getAction());
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (running) {
                    stop();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (mCanRun)
                    start();
                break;
        }
        return super.onTouchEvent(e);
    }

    static class AutoPollTask implements Runnable {
        private final WeakReference<AutoPollRecyclerView> mReference;

        //使用弱引用持有外部类引用->防止内存泄漏
        public AutoPollTask(AutoPollRecyclerView reference) {
            this.mReference = new WeakReference<AutoPollRecyclerView>(reference);
        }

        @Override
        public void run() {
            AutoPollRecyclerView recyclerView = mReference.get();
            if (recyclerView != null && recyclerView.running && recyclerView.mCanRun) {
//                TLog.d(TAG, "run --- scrollBy(1, 1) ---");
                recyclerView.scrollBy(1, 1);
                recyclerView.postDelayed(recyclerView.autoPollTask, recyclerView.TIME_AUTO_POLL);
            }
        }
    }


}
