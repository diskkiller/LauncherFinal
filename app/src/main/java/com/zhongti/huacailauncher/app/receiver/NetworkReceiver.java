package com.zhongti.huacailauncher.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.model.event.EventNetWorkChange;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;

import org.simple.eventbus.EventBus;

import timber.log.Timber;

/**
 * Create by ShuHeMing on 18/6/26
 * 网络变化的广播
 */
public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (NetworkUtils.isConnected()) {
                //连接上了
                EventBus.getDefault().post(new EventNetWorkChange(), EventBusTags.EVENT_NET_CONNECTED);
                Timber.i("网络状态: 已连接");
                ToastUtils.showShort("网络已连接");
            } else {
                //未连接上
                EventBus.getDefault().post(new EventNetWorkChange(), EventBusTags.EVENT_NET_DISCONNECTED);
                Timber.i("网络状态: 断开");
                ToastUtils.showShort("网络已断开");
            }
        }
    }
}
