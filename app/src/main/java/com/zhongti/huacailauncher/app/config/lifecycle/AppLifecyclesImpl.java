package com.zhongti.huacailauncher.app.config.lifecycle;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.alivc.player.AliVcMediaPlayer;
import com.hcb.hcbsdk.manager.SDKManager;
import com.jess.arms.base.delegate.AppLifecycles;
import com.jess.arms.utils.ArmsUtils;
import com.kingja.loadsir.core.LoadSir;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.zhongti.huacailauncher.BuildConfig;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.app.utils.EnvSwitchUtils;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.app.utils.WebHelper;
import com.zhongti.huacailauncher.utils.code.AppUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhongti.huacailauncher.widget.errcall.EmptyCallback;
import com.zhongti.huacailauncher.widget.errcall.ErrorCallback;
import com.zhongti.huacailauncher.widget.errcall.LoadingCallback;
import com.zhongti.huacailauncher.widget.errcall.TransCallBack;
import com.zhy.autolayout.config.AutoLayoutConifg;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.Fragmentation;
import timber.log.Timber;

/**
 * ================================================
 * 展示 {@link AppLifecycles} 的用法
 * <p>
 * Created by shuheming on 2018/1/5
 * ================================================
 */
public class AppLifecyclesImpl implements AppLifecycles {

    @Override
    public void attachBaseContext(Context base) {
        MultiDex.install(base);  //这里比 onCreate 先执行,常用于 MultiDex 初始化,插件化框架的初始化
    }

    @Override
    public void onCreate(Application application) {
        if (LeakCanary.isInAnalyzerProcess(application)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        //初始化Utils 必须放在最前面
        Utils.init(application);
        //初始化日志
        initTimberWithLogger();
        //leakCanary内存泄露检查
        initCanary(application);
        //扩展 AppManager 的远程遥控功能
        initAppManagerFun(application);
        //初始化Fragmentation
        initFragmentation();
        //初始化x5内核
        initX5Core(application);
        //initBugly
        initBugly(application);
        //初始化加载占位库
        initPlaceHolderLib();
        //初始化hcb sdk
        initHcbSdk(application);
        //集成阿里云播放器
        initAliPlayer(application);
        //解决系统应用中首次使用webView 的安全检查
        solveSysWebViewSecurity();
        //配置autoLayout,以物理高度进行百分比化
        initAutoLayout();
    }

    /**
     * 初始化日志
     */
    private void initTimberWithLogger() {
        if (BuildConfig.LOG_DEBUG) {//Timber初始化
            //Timber 是一个日志框架容器,外部使用统一的Api,内部可以动态的切换成任何日志框架(打印策略)进行日志打印
            //并且支持添加多个日志框架(打印策略),做到外部调用一次 Api,内部却可以做到同时使用多个策略
            //比如添加三个策略,一个打印日志,一个将日志保存本地,一个将日志上传服务器
            Timber.plant(new Timber.DebugTree());
            // 如果你想将框架切换为 Logger 来打印日志,请使用下面的代码,如想切换为其他日志框架请根据下面的方式扩展
//            FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
//                    .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
//                    .methodCount(0)         // (Optional) How many method line to show. Default 2
//                    .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
//                    .logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
//                    .tag("HeCaiLauncher")   // (Optional) Global tag for every log. Default PRETTY_LOGGER
//                    .build();
//            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
//            Timber.plant(new Timber.DebugTree() {
//                @Override
//                protected void log(int priority, String tag, @NonNull String message, Throwable t) {
//                    Logger.log(priority, tag, message, t);
//                }
//            });
            ButterKnife.setDebug(true);
        }
    }

    /**
     * leakCanary内存泄露检查
     */
    private void initCanary(Application application) {
        ArmsUtils.obtainAppComponentFromContext(application).extras().put(RefWatcher.class.getName(), BuildConfig.USE_CANARY ? LeakCanary.install(application) : RefWatcher.DISABLED);
    }

    /**
     * 扩展 AppManager 的远程遥控功能
     */
    private void initAppManagerFun(Application application) {
        ArmsUtils.obtainAppComponentFromContext(application).appManager().setHandleListener((appManager, message) -> {
            //switch (message.what) {
            //case 0:
            //do something ...
            //   break;
            //}
        });
        //Usage:
        //Message msg = new Message();
        //msg.what = 0;
        //AppManager.post(msg); like EventBus
    }

//    /**
//     * 初始化下载器的配置
//     */
//    private void initDown(Application application) {
//        DownloadConfig.Builder builder = DownloadConfig.Builder.Companion.create(application)
//                .enableDb(true)
//                .enableAutoStart(true)
//                .enableService(true)
//                .enableNotification(false);
//
//        DownloadConfig.INSTANCE.init(builder);
//    }

    private boolean isInstall;

    /**
     * 初始化Fragmentation
     */
    private void initFragmentation() {
        if (isInstall) return;
        Fragmentation.builder()
                // 设置 栈视图 模式为 悬浮球模式   SHAKE: 摇一摇唤出  默认NONE：隐藏， 仅在Debug环境生效
//                .stackViewMode(BuildConfig.DEBUG ? Fragmentation.BUBBLE : Fragmentation.NONE)
                .stackViewMode(Fragmentation.NONE)
                // 开发环境：true时，遇到异常："Can not perform this action after onSaveInstanceState!"时，抛出，并Crash;
                // 生产环境：false时，不抛出，不会Crash，会捕获，可以在handleException()里监听到
                .debug(BuildConfig.DEBUG)
                // 生产环境时，捕获上述异常（避免crash），会捕获
                // 建议在回调处上传下面异常到崩溃监控服务器
                .handleException(e -> {
                    // 以Bugtags为例子: 把捕获到的 Exception 传到 Bugtags 后台。
                    // Bugtags.sendException(e);
                })
                .install();
        isInstall = true;
    }

    /**
     * 初始化X5 内核
     */
    private void initX5Core(Application application) {
        QbSdk.initX5Environment(application, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了,有可能特殊情况下x5内核加载失败，切换到系统内核
                Timber.e("onCoreInitFinished : X5内核加载完成");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                //x5内核初始化完成回调接口，可通过参数判断是否加载起来了x5内核
                //isX5Core:isX5Core为true表示x5内核加载成功；false表示加载失败，此时会自动切换到系统内核。
                //如果在此回调前创建webview会导致使用系统内核
                Timber.e("onViewInitFinished: %s", (b ? "X5内核加载完成,可使用X5 WebView" : "未加载成功X5内核,使用系统WebView"));
            }
        });
    }

    /**
     * 初始化hcb sdk
     */
    private void initHcbSdk(Application application) {
        SDKManager.getInstance().init(application, UserUtils.getSNNum(), EnvSwitchUtils.isDebug);
    }

    /**
     * 初始化bugly
     */
    private void initBugly(Application application) {
        CrashReport.initCrashReport(application, Constants.BUGLY_APP_ID, false);
        // 获取当前包名
        String packageName = application.getPackageName();
        // 获取当前进程名
        String processName = AppUtils.getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(application);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
        CrashReport.initCrashReport(application, Constants.BUGLY_APP_ID, BuildConfig.CRASH_DEBUG, strategy);
        //设置是否是测试设备
        CrashReport.setIsDevelopmentDevice(application, BuildConfig.CRASH_DEBUG);
        //设置用户ID
        CrashReport.setUserId(UserUtils.getSNNum());
    }

    /**
     * 初始化loadSir
     */
    private void initPlaceHolderLib() {
        LoadSir.beginBuilder()
                .addCallback(new ErrorCallback())
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .addCallback(new TransCallBack())
                .setDefaultCallback(LoadingCallback.class)
                .commit();
    }

    @Override
    public void onTerminate(Application application) {

    }

    /**
     * 初始化阿里云播放器
     */
    private void initAliPlayer(Application application) {
        //初始化播放器（只需调用一次即可，建议在application中初始化）
        AliVcMediaPlayer.init(application);
    }

    /**
     * 解决系统应用中首次使用webView 的安全检查
     */
    private void solveSysWebViewSecurity() {
        WebHelper.hookWebView();
    }

    /**
     * 配置autoLayout,以物理高度进行百分比化
     */
    private void initAutoLayout() {
        AutoLayoutConifg.getInstance().useDeviceSize();
    }
}
