package com.zhongti.huacailauncher.app.utils;


import com.zhongti.huacailauncher.model.event.EventLadderTimeUp;

import org.simple.eventbus.EventBus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * 天梯列表轮询帮助类
 * Created by shuheming on 2018/3/8 0008.
 */

public class DataReqHelper {
    private static final int poolSize = 3;
    /**
     * 初始化延迟时间
     */
    private static final int initDelay = 5;
    private static final int delay = 5;

    private static ScheduledExecutorService pool;

    /**
     * 开始任务
     */
    public static void startTask(String type) {
        Timber.e("开始轮询");
        pauseTask();
        pool = Executors.newScheduledThreadPool(poolSize);
        pool.scheduleWithFixedDelay(() -> {
            EventBus.getDefault().post(new EventLadderTimeUp(), type);
        }, initDelay, delay, TimeUnit.SECONDS);
    }

    public static void pauseTask() {
        Timber.e("停止轮询");
        if (pool != null && !pool.isShutdown()) {
            pool.shutdown();
            pool = null;
        }
    }
}
