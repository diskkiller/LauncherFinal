package com.zhongti.huacailauncher.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.zhongti.huacailauncher.ui.MainActivity;

import timber.log.Timber;

/**
 * Create by ShuHeMing on 18/6/29
 */
public class BCRUpgradeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;
        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
            Timber.d("onUpgrade: %s", intent.getAction());
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        if (intent == null || TextUtils.isEmpty(intent.getAction())) return;
//        if (TextUtils.equals(intent.getAction(), Intent.ACTION_PACKAGE_REPLACED)) {
//            Timber.d("onUpgrade: %s", intent.getAction()); 
//            Intent intent2 = new Intent(context, MainActivity.class); 
//            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); context.startActivity(intent2);
//        }
//    }
//}
