package com.zhongti.huacailauncher.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.hcb.hcbsdk.service.msgBean.LoginReslut;
import com.hcb.hcbsdk.socketio.listener.IConstants;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.utils.NoHandleTimer;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.model.event.EventGame;
import com.zhongti.huacailauncher.model.event.EventUpdateUser;

import org.simple.eventbus.EventBus;

import timber.log.Timber;

/**
 * 接收hcb sdk 广播
 * Created by shuheming on 2018/2/7 0007.
 */

public class SdkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        if (TextUtils.isEmpty(intent.getAction())) return;
        switch (intent.getAction()) {
            case IConstants.EVENT_CONNECT://sdk连接
                onSdkConnect();
                break;
            case IConstants.LOGIN:
                onLoginSuccess();
                break;
            case IConstants.PAY_SUCCESS:
                onPaySuccess(intent);
                break;
            case IConstants.LOGIN_OUT:
                onLogout();
                break;
            case IConstants.ORDER_CANCEL:
                onOrCancel();
                break;
            case IConstants.PAY_FAIL:
                onPayFail(intent);
                break;
            case IConstants.CHESS_GOLD_SUCCESS:
                onGoldAddLessSuccess();
                break;
            case IConstants.CHESS_GOLD_FAIL:
                onGoldAddLessFail();
                break;
        }
    }

    /**
     * sdk 连接的事件
     */
    private void onSdkConnect() {
        LoginReslut.User user = UserUtils.getUser();
        if (user == null) return;
        EventBus.getDefault().post(new EventUpdateUser(user), EventBusTags.EVENT_SOCKET_CONNECTED);
        Timber.i("SDK连接了");
    }

    /**
     * i
     * 登录成功
     */
    private void onLoginSuccess() {
        //开始计时---强制退出登录,,防止登录后未操作
        if (!NoHandleTimer.isIsRunning()) NoHandleTimer.startTimer();
        //通知登录成功
        LoginReslut.User user = UserUtils.getUser();
        if (user == null) return;
        EventBus.getDefault().post(new EventUpdateUser(user), EventBusTags.EVENT_LOGIN_SUCCESS);
        Timber.i("登录成功: %s", user.toString());
    }

    /**
     * 支付成功(金豆消耗/充值成功,,)
     */
    private void onPaySuccess(Intent intent) {
        String stringExtra = intent.getStringExtra(IConstants.EXTRA_MESSAGE);
        if (TextUtils.isEmpty(stringExtra)) {
            EventBus.getDefault().post(new EventUpdateUser(null), EventBusTags.EVENT_PAY_SUCCESS);
        } else {
            switch (stringExtra) {
                case "2": // 支付
                    EventBus.getDefault().post(new EventGame(EventGame.CHARGE), EventBusTags.EVENT_PAY_SUCCESS);
                    break;
                case "3": //消耗
                    EventBus.getDefault().post(new EventGame(EventGame.USE), EventBusTags.EVENT_PAY_SUCCESS);
                    break;
            }
        }
    }

    /**
     * 退出登录
     */
    private void onLogout() {
        EventBus.getDefault().post(new EventUpdateUser(null), EventBusTags.EVENT_LOGIN_OUT);
    }

    /**
     * 支付取消
     */
    private void onOrCancel() {
        EventBus.getDefault().post(new EventUpdateUser(null), EventBusTags.EVENT_PAY_CANCEL);
    }

    /**
     * 支付(充值/消耗金豆) 失败
     */
    private void onPayFail(Intent intent) {
        String stringExtra = intent.getStringExtra(IConstants.EXTRA_MESSAGE);
        if (TextUtils.isEmpty(stringExtra)) {
            EventBus.getDefault().post(new EventUpdateUser(null), EventBusTags.EVENT_PAY_FAIL);
        } else {
            switch (stringExtra) {
                case "2": // 支付
                    EventBus.getDefault().post(new EventGame(EventGame.CHARGE), EventBusTags.EVENT_PAY_FAIL);
                    break;
                case "3": //消耗
                    EventBus.getDefault().post(new EventGame(EventGame.USE), EventBusTags.EVENT_PAY_FAIL);
                    break;
            }
        }
    }

    /**
     * 金豆增加/减少成功
     */
    private void onGoldAddLessSuccess() {
        EventBus.getDefault().post(new EventGame(EventGame.ADD_LESS), EventBusTags.EVENT_ADD_LESS_SUCCESS);
    }

    /**
     * 金豆增加/减少失败
     */
    private void onGoldAddLessFail() {
        EventBus.getDefault().post(new EventGame(EventGame.ADD_LESS), EventBusTags.EVENT_ADD_LESS_FAIL);
    }

//
//    /**
//     * 支付成功
//     */
//    private void onPaySuccess() {
//        //通知支付成功
//        LoginReslut.User user = UserUtils.getUser();
//        if (user == null) return;
//        EventBus.getDefault().post(new OnUpdateUser(user), EventBusTags.EVENT_PAY_SUCCESS);
//        Timber.e("支付成功: %s", user.toString());
//    }
//
//    /**
//     * 奖金金额变化
//     */
//    private void onBonusChange(Intent intent) {
//        if (intent.getExtras() == null) return;
//        String message = intent.getExtras().getString(IConstants.EXTRA_MESSAGE);
////            Timber.i("COUNT_BOUNS: launcher");
//        if (TextUtils.isEmpty(message)) return;
//        try {
//            JSONObject jsonObject = new JSONObject(message);
//            Object status = jsonObject.get("status");
//            if (status instanceof Integer && (Integer) status == 10000) {
//                String data = jsonObject.get("data").toString();
//                if (TextUtils.isEmpty(data)) return;
//                if (data.contains(".")) {
//                    String[] split = data.split("\\.");
//                    data = split[0];
//                }
//                if (!NumUtils.isInteger(data)) return;
//                EventBus.getDefault().post(new OnReceiveHomeAward(data), EventBusTags.EVENT_ON_RECEIVE_HOME_AWRD_NUM);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 赢得奖金
//     */
//    private void onWinReward() {
//        Timber.e("奖金到账");
//        LoginReslut.User user = UserUtils.getUser();
//        if (user == null) return;
//        EventBus.getDefault().post(new OnWinner(user), EventBusTags.EVENT_ON_GET_REWARD);
//    }
//
//    /**
//     * 用户信息发生变化
//     */
//    private void onUserInfoChange() {
//        LoginReslut.User user = UserUtils.getUser();
//        Timber.e("用户改变了");
//        EventBus.getDefault().post(new OnUpdateUser(user), EventBusTags.EVENT_USER_INFO_CHANGED);
//    }
//
//    /**
//     * 彩票退款
//     *
//     * @param intent
//     */
//    private void onRefuse(Intent intent) {
//        if (intent == null || intent.getExtras() == null) return;
//        String msg = intent.getExtras().getString(IConstants.EXTRA_MESSAGE);
//        if (TextUtils.isEmpty(msg)) return;
//        EventBus.getDefault().post(new OnLotteryRefused(msg), EventBusTags.EVENT_LOTTERY_REFUSED);
//    }
}
