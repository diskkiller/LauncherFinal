package com.zhongti.huacailauncher.app.utils;


import android.os.Handler;
import android.os.Message;

import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.model.event.EventForceLogout;

import org.simple.eventbus.EventBus;

import timber.log.Timber;

/**
 * 计时
 * Created by shuheming on 2018/3/5 0005.
 */

public class NoHandleTimer {
    private static final int COUNT_TIME = 120;
    private static Handler handler = new MyHandler();
    private static int time;
    private static final int DEAD_TIME = 5 * 60;//5分钟

    private volatile static NoHandleTimer instance;

    public static NoHandleTimer getInstance() {
        if (instance == null) {
            synchronized (NoHandleTimer.class) {
                if (instance == null) {
                    instance = new NoHandleTimer();
                }
            }
        }
        return instance;
    }

    private static class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == COUNT_TIME) {
                handler.sendEmptyMessageDelayed(COUNT_TIME, 1000);
                time++;
                Timber.i("用户未操作计时: %s", time);
                if (time == DEAD_TIME) {
                    pauseTimer();
                    EventBus.getDefault().post(new EventForceLogout(), EventBusTags.EVENT_FORCE_LOGOUT_TIME_UP);
                }
            }
        }
    }

    private static boolean isRunning = false;

    public static void startTimer() {
        pauseTimer();
        if (handler == null) handler = new MyHandler();
        handler.sendEmptyMessageDelayed(COUNT_TIME, 1000);
        isRunning = true;
    }

    public static void pauseTimer() {
        if (handler != null) handler.removeCallbacksAndMessages(null);
        time = 0;
        isRunning = false;
    }

    public static boolean isIsRunning() {
        return isRunning;
    }
}
