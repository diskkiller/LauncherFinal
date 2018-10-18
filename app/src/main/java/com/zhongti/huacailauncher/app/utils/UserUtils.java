package com.zhongti.huacailauncher.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;

import com.hcb.hcbsdk.manager.SDKManager;
import com.hcb.hcbsdk.service.msgBean.LoginReslut;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.mvp.IView;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxLifecycleUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.http.exception.ApiException;
import com.zhongti.huacailauncher.app.http.fun.HttpResultFunction;
import com.zhongti.huacailauncher.app.http.fun.ServerResultFunction;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObserver;
import com.zhongti.huacailauncher.model.api.service.UserService;
import com.zhongti.huacailauncher.ui.home.activity.DeviceBindActivity;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.SPUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhongti.huacailauncher.widget.progress.ProgressReqFrameDialog;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 用户相关Util
 * Created by shuheming on 2018/2/1 0001.
 */

public class UserUtils {
    private static final String SERIAL_SAVE = "serial_save";

    /**
     * 获取设备SN号
     *
     * @return SN 号(改为获取mac地址)
     */
    @SuppressLint("HardwareIds")
    public static String getSNNum() {
        //A8A198FFD48C335
        String sNum = SPUtils.getInstance().getString(SERIAL_SAVE);
        if (TextUtils.isEmpty(sNum)) {
            sNum = Build.SERIAL;
            SPUtils.getInstance().put(SERIAL_SAVE, sNum);
        }
        if (!TextUtils.isEmpty(Build.SERIAL) && !TextUtils.equals(Build.SERIAL, SPUtils.getInstance().getString(SERIAL_SAVE))) {
            sNum = Build.SERIAL;
            SPUtils.getInstance().put(SERIAL_SAVE, sNum);
        }
//        return "A8A198FFD48C335";
        return sNum;
//        String macAddress = SPUtils.getInstance().getString(DEVICE_NO);
//        if (TextUtils.isEmpty(macAddress)) {
//            macAddress = DeviceUtils.getMacAddress().replaceAll(":", "");
//            SPUtils.getInstance().put(DEVICE_NO, macAddress);
//        }
//        return macAddress;
    }

    public static boolean isLogin() {
        if (SDKManager.getInstance() != null && SDKManager.getInstance().getUser() != null) {
            return true;
        }
        login();
        return false;
    }

    public static boolean isLoginOnly() {
        return SDKManager.getInstance() != null && SDKManager.getInstance().getUser() != null;
    }

    /**
     * 打开登录页面
     */
    public static void login() {
        WebHelper.hookWebView();
        SDKManager.getInstance().startLoginPage();
    }

    /**
     * 获取token
     */
    public static String getToken() {
        if (SDKManager.getInstance() != null && SDKManager.getInstance().getUser() != null) {
            return SDKManager.getInstance().getUser().getToken();
        }
        return "";
    }

    /**
     * 获取用户对象
     */
    public static LoginReslut.User getUser() {
        if (SDKManager.getInstance() != null) {
            return SDKManager.getInstance().getUser();
        }
        return null;
    }

    /**
     * 退出登录
     */
    public static void logOut() {
        if (SDKManager.getInstance() != null) {
            SDKManager.getInstance().logOut();
        }
    }

    /**
     * 打开支付页面
     *
     * @param orderId 订单号
     * @param url     微信支付链接
     */
    public static void openPayPage(long orderId, String url) {
        SDKManager.getInstance().startPayPage(String.valueOf(orderId), url, 1, String.valueOf(0));
    }

    public static void openPayPage(long orderId, String url, String orNo, int type) {
        SDKManager.getInstance().startPayPage(String.valueOf(orderId), url, 1, String.valueOf(0), orNo, type);
    }

    /**
     * 游戏消耗
     */
    public static void gameUseGold(String appId, int goldNo) {
        SDKManager.getInstance().startGamePayPage(appId, goldNo);
    }

    /**
     * 打开游戏中的重置页面
     */
    public static void openGameChargePage(Activity activity, String appId) {
        SDKManager.getInstance().startRechargeGoldPage(activity, appId);
    }

    /**
     * 生成彩票二维码
     */
    public static Bitmap createLottErCode(String code, int width, int height) {
        if (SDKManager.getInstance() == null) return null;
        return SDKManager.getInstance().createDMBarcode(code, width, height);
    }

    /**
     * 打开服务协议页面
     */
    public static void startProtoPage(Activity context) {
        if (SDKManager.getInstance() == null) return;
        SDKManager.getInstance().startAboutPage(context);
    }

    /**
     * @param appId appid
     * @param num   金豆数量
     * @param type  类型 1,增加 2,减少
     */
    public static void goldCoinAddOrLess(String appId, String num, String type) {
        if (SDKManager.getInstance() == null) return;
        SDKManager.getInstance().goldCoinAddOrLess(appId, num, type);
    }

    /**
     * 检查Sdk service是否关闭
     */
    public static void checkSdkServiceIsNice() {
        if (SDKManager.getInstance() != null)
            SDKManager.getInstance().checkRebuildService(Utils.getApp(), UserUtils.getSNNum(), EnvSwitchUtils.isDebug);
    }

    /**
     * 给H5链接添加参数
     */
    public static String getDealH5Url(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        } else if (!TextUtils.isEmpty(url) && url.contains("?")) {
            return url.endsWith("?")
                    ? url.substring(0, url.length() - 1) + "?token=" + getToken() + "&deviceNo=" + getSNNum()
                    : url + "&token=" + getToken() + "&deviceNo=" + getSNNum();
        } else {
            return url + "?token=" + getToken() + "&deviceNo=" + getSNNum();
        }
    }

    /**
     * 判断是否绑定/登录
     */
    public static void isBindAndLogin(BindDeviceCallback callback, IView iView, Activity context) {
        WeakReference<Activity> activity = new WeakReference<>(context);
        //登录前判断是否绑定
        ProgressReqFrameDialog dialog = new ProgressReqFrameDialog(context);
        dialog.setCancelable(false);
        judgeIsBind(callback, iView, dialog, activity);
    }

    private static void judgeIsBind(BindDeviceCallback callback, IView iView, ProgressReqFrameDialog dialog, WeakReference<Activity> activity) {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.net_unavailable);
            return;
        }
        AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(Utils.getApp());
        appComponent.repositoryManager().obtainRetrofitService(UserService.class).checkIsActive(getSNNum())
                .delay(500, TimeUnit.MILLISECONDS)
                .map(new ServerResultFunction<>())
                .onErrorResumeNext(new HttpResultFunction<>())
                .subscribeOn(Schedulers.io())
                .compose(RxLifecycleUtils.bindToLifecycle(iView))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<Integer>(dialog) {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort(e.getMsg());
                    }

                    @Override
                    protected void onSuccess(Integer response) {
                        if (response == 0) {
                            launchBindPage(activity);
                        } else if (response == 1) {
                            if (callback != null) callback.onSuccess();
                        }
                    }
                });
    }

    /**
     * 打开设备绑定页
     */
    private static void launchBindPage(WeakReference<Activity> activity) {
        if (activity == null) return;
        if (activity.get() == null) return;
        Intent intent = new Intent(Utils.getApp(), DeviceBindActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(activity.get());
            activity.get().startActivity(intent, optionsCompat.toBundle());
        } else {
            activity.get().startActivity(intent);
        }
    }

    public interface BindDeviceCallback {
        void onSuccess();
    }
}
