package com.zhongti.huacailauncher.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hcb.hcbsdk.manager.SDKManager;
import com.zhongti.huacailauncher.app.utils.NoHandleTimer;
import com.zhongti.huacailauncher.app.utils.UserUtils;

import timber.log.Timber;

/**
 * 接收开关机广播
 * Created by shuheming on 2018/2/22 0022.
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (TextUtils.isEmpty(intent.getAction())) return;
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) && SDKManager.getInstance() != null) {
//            UserUtils.logOut();
            NoHandleTimer.pauseTimer();
        } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN) && SDKManager.getInstance() != null) {
            try {
                UserUtils.logOut();
            } catch (Exception e) {
                Timber.e(e.getMessage());
            }
            NoHandleTimer.pauseTimer();
        }
    }

}
