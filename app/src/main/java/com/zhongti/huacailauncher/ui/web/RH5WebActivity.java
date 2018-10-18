package com.zhongti.huacailauncher.ui.web;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.mvp.IView;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.bugly.crashreport.crash.h5.H5JavaScriptInterface;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.zhongti.huacailauncher.BuildConfig;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.model.event.EventGame;
import com.zhongti.huacailauncher.ui.lottery.activity.ScratchDetailActivity;
import com.zhongti.huacailauncher.utils.code.AppUtils;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.LogUtils;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.RegexUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.widget.errcall.TransCallBack;
import com.zhongti.huacailauncher.widget.errcall.WebEmptyCallback;
import com.zhongti.huacailauncher.widget.errcall.WebErrorCallback;
import com.zhongti.huacailauncher.widget.errcall.WebLoadCallback;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by shuheming on 2018/1/18 0018.
 */

public class RH5WebActivity extends BaseSupportActivity implements IView {
    @BindView(R.id.rl_web_app_container)
    RelativeLayout flContainer;
    private String mUrl;
    private WebView mWebView;
    private LoadService loadService;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.slide);
            Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.fade);
            getWindow().setEnterTransition(slide);
            getWindow().setReenterTransition(slide);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BarUtils.hideSystemUI(this);
        super.onCreate(savedInstanceState);
        isPageSuccess = true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) BarUtils.hideSystemUI(RH5WebActivity.this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_web_app;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        openHard();
        initRetryView();
        initWebView();
        addWebView();
        getLoadUrl();
        if (flContainer != null) {
            flContainer.postDelayed(this::delayLoad, 400);
        }
    }

    private void openHard() {
        try {
            if (Integer.parseInt(android.os.Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                android.view.WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
            }
        } catch (Exception e) {
            Timber.e("硬件加速开启失败");
        }
    }

    private void delayLoad() {
        if (loadService != null) loadService.showCallback(WebLoadCallback.class);
        if (flContainer != null) flContainer.postDelayed(this::loadUrl, 600);
    }

    private void initRetryView() {
        LoadSir loadSir = new LoadSir.Builder()
                .addCallback(new WebLoadCallback())
                .addCallback(new WebEmptyCallback())
                .addCallback(new TransCallBack())
                .addCallback(new WebErrorCallback())
                .build();
        loadService = loadSir.register(flContainer, (Callback.OnReloadListener) v -> {
            // 重新加载逻辑
            if (loadService != null) loadService.showCallback(WebLoadCallback.class);
            if (flContainer != null) flContainer.postDelayed(this::loadUrl, 500);
        });
        loadService.setCallBack(WebLoadCallback.class, (context, view) -> {
            LottieAnimationView loadView = view.findViewById(R.id.place_holder_loading);
            int type = getIntent().getIntExtra(Constants.INTENT_REMOTE_WEB_LOAD, 1);
            switch (type) {
                case 1:
                    loadView.setAnimation("LottieAnim/lottery_loading.json");
                    break;
                case 2:
                    loadView.setAnimation("LottieAnim/lottery_loading.json");
                    break;
            }
        });
        loadService.showCallback(TransCallBack.class);
    }

    public void getLoadUrl() {
        this.mUrl = getIntent().getStringExtra(Constants.INTENT_REMOTE_WEB_URL);
        if (TextUtils.isEmpty(mUrl)) {
            if (loadService != null) loadService.showCallback(WebErrorCallback.class);
            ToastUtils.showShort("加载地址不能为空");
            RH5WebActivity.this.finish();
        }
    }

    @SuppressLint("TimberExceptionLogging")
    private void addWebView() {
        try {
            if (mWebView == null) return;
            if (flContainer == null) return;
            if (mWebView.getParent() != null) return;
            flContainer.addView(mWebView, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            Timber.i("添加WebView");
            LogUtils.file(TAG, "添加WebView");
        } catch (Exception e) {
            Timber.e(e.getMessage());
            LogUtils.file(TAG, e.getMessage());
        }
    }

    private void removeWebView() {
        if (mWebView == null) return;
        if (flContainer == null) return;
        if (mWebView.getParent() != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            parent.removeView(mWebView);
            Timber.i("移除WebView");
            LogUtils.file(TAG, "移除WebView");
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebView.setWebContentsDebuggingEnabled(BuildConfig.LOG_DEBUG);
        mWebView = new WebView(this, null);
        mWebView.setBackgroundResource(R.drawable.bg_scatch_detail1);
        mWebView.getView().setClickable(true);
        mWebView.setOnLongClickListener(v -> true);
        mWebView.addJavascriptInterface(new WebViewJavaScriptFunction(), "hcbWeb");
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setDownloadListener((s, s1, s2, s3, l) -> {

        });
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setAllowUniversalAccessFromFileURLs(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();

        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        CrashReport.setJavascriptMonitor(new MyBCReport(mWebView), true);
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {

    }

    @Override
    public void launchActivity(@NonNull Intent intent) {

    }

    @Override
    public void killMyself() {

    }

    private static class MyBCReport implements CrashReport.WebViewInterface {

        private WeakReference<WebView> bWebView;

        MyBCReport(WebView webView) {
            this.bWebView = new WeakReference<>(webView);
        }

        @Override
        public String getUrl() {
            return bWebView.get() == null ? "" : bWebView.get().getUrl();
        }

        @Override
        public void setJavaScriptEnabled(boolean flag) {
            if (bWebView.get() != null) {
                WebSettings webSettings = bWebView.get().getSettings();
                webSettings.setJavaScriptEnabled(flag);
            }
        }

        @Override
        public void loadUrl(String url) {
            if (bWebView.get() != null) bWebView.get().loadUrl(url);
        }

        @Override
        public void addJavascriptInterface(H5JavaScriptInterface jsInterface, String name) {
            if (bWebView.get() != null) bWebView.get().addJavascriptInterface(jsInterface, name);
        }

        @Override
        public CharSequence getContentDescription() {
            return bWebView.get() == null ? "" : bWebView.get().getContentDescription();
        }
    }

    private boolean hadTimeout;
    private boolean isPageSuccess = true;

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);
            Timber.i("开始加载网页: onPageStarted()");
            LogUtils.file(TAG, "开始加载网页: onPageStarted()");
            hadTimeout = false;
            startTimeOut();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            Timber.i("loadUrl: %s", url);
            LogUtils.file(TAG, "loadUrl: " + url);
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView webView, int i, String s, String s1) {
            super.onReceivedError(webView, i, s, s1);
            Timber.i("加载的远程H5地址----> 加载失败了err");
            LogUtils.file(TAG, "loadUrl: 加载的远程H5地址----> 加载失败了err");
            if (mWebView != null)
                mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            if (loadService != null) loadService.showCallback(WebErrorCallback.class);
            isPageSuccess = false;
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            Timber.i("网页加载完成 onPageFinished()");
            LogUtils.file(TAG, "网页加载完成 onPageFinished()");
            stopTimeout();
        }

        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
            return super.onJsConfirm(webView, s, s1, jsResult);
        }

        @Override
        public void onShowCustomView(View view, IX5WebChromeClient.CustomViewCallback customViewCallback) {
            super.onShowCustomView(view, customViewCallback);
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
        }

        @Override
        public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
            return super.onJsAlert(webView, s, s1, jsResult);
        }

        @Override
        public void onProgressChanged(WebView webView, int progress) {
            Timber.i("网页加载中: currentProgress: ---> %s", progress);
            LogUtils.file(TAG, "网页加载中: currentProgress: ---> " + progress);
            if (progress == 100 && loadService != null && !hadTimeout) {
                if (isPageSuccess) {
                    loadService.showSuccess();
                    isPageSuccess = true;
                    Timber.i("进度走完,, 最终成功");
                    LogUtils.file(TAG, "进度走完,, 最终成功");
                } else {
                    loadService.showCallback(WebErrorCallback.class);
                    isPageSuccess = false;
                    Timber.i("进度走完,, 最终err");
                    LogUtils.file(TAG, "进度走完,, 最终err");
                }
                stopTimeout();
            }
            super.onProgressChanged(webView, progress);
        }
    }

    private void loadUrl() {
        isPageSuccess = true;
        Timber.i("加载的远程H5地址----> %s", TextUtils.isEmpty(mUrl) ? "" : mUrl);
        if (!NetworkUtils.isConnected()) {
            if (loadService != null) loadService.showCallback(WebErrorCallback.class);
            return;
        }
        if (mWebView != null) {
            mWebView.loadUrl(mUrl);
            mWebView.requestFocus();
        }
        LogUtils.file(TAG, "加载的远程H5地址----> " + (TextUtils.isEmpty(mUrl) ? "" : mUrl));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent == null || mWebView == null || intent.getData() == null) return;
        mWebView.loadUrl(intent.getData().toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mWebView != null) mWebView.onResume();
    }

    @Override
    protected void onPause() {
        if (mWebView != null) mWebView.onPause();
        if (loadService != null) {
            loadService.showSuccess();
            if (mWebView != null) mWebView.requestFocus();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopTimeout();
        if (handler != null) handler.removeCallbacksAndMessages(null);
        AppUtils.fixInputMethodManagerLeak(this);
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            mWebView.clearCache(true);
            removeWebView();
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    private static final int TIME_OUT = 20000;
    private Timer timer;
    private TimerTask timerTask;
    private TimeOutHandler handler;

    public void startTimeOut() {
        if (handler == null) {
            handler = new TimeOutHandler(mWebView, loadService, RH5WebActivity.this);
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(TIME_OUT);
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, TIME_OUT);
    }

    private void stopTimeout() {
        if (timer != null)
            timer.cancel();
        timer = null;
        if (timerTask != null)
            timerTask.cancel();
        timerTask = null;
        if (handler != null) handler.removeCallbacksAndMessages(null);
        hadTimeout = false;
    }

    static class TimeOutHandler extends Handler {
        private WeakReference<WebView> mWebView;
        private WeakReference<LoadService> mLoadService;
        private WeakReference<RH5WebActivity> mActivity;

        TimeOutHandler(WebView webView, LoadService loadService, RH5WebActivity activity) {
            this.mWebView = new WeakReference<>(webView);
            this.mLoadService = new WeakReference<>(loadService);
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWebView != null && mWebView.get() != null && mWebView.get().getProgress() < 100) {
                if (mLoadService != null && mLoadService.get() != null) {
                    mWebView.get().stopLoading();
                    mLoadService.get().showCallback(WebErrorCallback.class);
                    if (mActivity != null && mActivity.get() != null) {
                        mActivity.get().isPageSuccess = false;
                    }
                    Timber.i("timer 计时结束,加载err");
                    LogUtils.file("RH5WebActivity: " + "timer 计时结束,加载err");
                }
            }
            if (mActivity != null && mActivity.get() != null) {
                mActivity.get().stopTimeout();
                mActivity.get().hadTimeout = true;
            }
        }
    }

    private class WebViewJavaScriptFunction {
        @JavascriptInterface
        public void close() {
            RH5WebActivity.this.finish();
        }

        @JavascriptInterface
        public void openGameChargePage(String appId) {
            Timber.i("Game打开充值页面, appId: %s", appId);
            UserUtils.openGameChargePage(RH5WebActivity.this, appId);
        }

        @JavascriptInterface
        public void gameUseGold(int num, String appId) {
            Timber.i("消耗金豆: num: " + num + ",, appId: " + appId);
            UserUtils.gameUseGold(appId, num);
        }

        @JavascriptInterface
        public void goldCoinAddOrLess(String appId, String num, String type) {
            if (TextUtils.equals(type, "1")) {
                Timber.i("金豆--增加: " + num + "  appId = " + appId);
            } else if (TextUtils.equals(type, "2")) {
                Timber.i("金豆--减少: " + num + "  appId = " + appId);
            } else {
                Timber.i("金豆--未知操作: " + num + "  appId = " + appId);
            }
            UserUtils.goldCoinAddOrLess(appId, num, type);
        }

        @JavascriptInterface
        public void openLotteryDetail(long id) {
            Timber.i("打开彩票详情,,, id = %s", id);
            bannerTurn2LotteryPage(id);
        }

        @JavascriptInterface
        public void openAH5Page(String url) {
            Timber.i("打开普通H5页面,,, url = %s", url);
            bannerTurn2H5Page(url);
        }

        @JavascriptInterface
        public void openGamePage(String url) {
            Timber.i("打开游戏页面,,, url = %s", url);
            bannerTurn2GamePage(url);
        }

    }

    public void bannerTurn2H5Page(String redirectUrl) {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.net_unavailable);
            return;
        }
        if (TextUtils.isEmpty(redirectUrl)) {
            return;
        }
        if (!RegexUtils.isURL(redirectUrl)) {
            return;
        }
        Intent intent = new Intent(this, RH5WebActivity.class);
        intent.putExtra(Constants.INTENT_REMOTE_WEB_URL, UserUtils.getDealH5Url(redirectUrl));
        intent.putExtra(Constants.INTENT_REMOTE_WEB_LOAD, 1);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public void bannerTurn2LotteryPage(long id) {
        try {
            openLottiDetailPage(id);
        } catch (Exception e) {
            Timber.e("banner打开彩票详情失败");
        }
    }

    private void openLottiDetailPage(long id) {
        if (id == 0 || id == -1) return;
        if (flContainer != null) {
            flContainer.postDelayed(() -> {
                Intent intent = new Intent(this, ScratchDetailActivity.class);
                intent.putExtra(Constants.INTENT_TO_LOTTI_DETAIL, id);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                        && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
                    startActivity(intent, optionsCompat.toBundle());
                } else {
                    startActivity(intent);
                }
            }, 100);
        }
    }

    public void bannerTurn2GamePage(String gameUrl) {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.net_unavailable);
            return;
        }
        if (TextUtils.isEmpty(gameUrl)) {
            return;
        }
        if (!RegexUtils.isURL(gameUrl)) {
            return;
        }
        if (UserUtils.isLoginOnly()) {
            openGamePage(gameUrl);
        } else {
            UserUtils.isBindAndLogin(UserUtils::isLogin, this, this);
        }

    }

    private void openGamePage(String gameUrl) {
        Intent intent = new Intent(this, RH5WebActivity.class);
        intent.putExtra(Constants.INTENT_REMOTE_WEB_URL, UserUtils.getDealH5Url(gameUrl));
        intent.putExtra(Constants.INTENT_REMOTE_WEB_LOAD, 1);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Subscriber(tag = EventBusTags.EVENT_PAY_SUCCESS, mode = ThreadMode.MAIN)
    private void onPaySuccess(EventGame eventGame) {
        switch (eventGame.getWitch()) {
            case EventGame.CHARGE:
                Timber.i("金豆充值成功");
                if (mWebView != null) mWebView.loadUrl("javascript:chargeSuccess()");
                break;
            case EventGame.USE:
                Timber.i("金豆消耗成功");
                if (mWebView != null) mWebView.loadUrl("javascript:useGoldSuccess()");
                break;
        }
    }

    @Subscriber(tag = EventBusTags.EVENT_PAY_FAIL, mode = ThreadMode.MAIN)
    private void onPayFail(EventGame eventGame) {
        Timber.i("onPayFail");
        switch (eventGame.getWitch()) {
            case EventGame.CHARGE:
                Timber.i("金豆充值失败");
                if (mWebView != null) mWebView.loadUrl("javascript:chargeFail()");
                break;
            case EventGame.USE:
                Timber.i("金豆消耗失败");
                if (mWebView != null) mWebView.loadUrl("javascript:useGoldFail()");
                break;
        }
    }

    @Subscriber(tag = EventBusTags.EVENT_ADD_LESS_SUCCESS, mode = ThreadMode.MAIN)
    private void onAddLessGoldSuccess(EventGame game) {
        if (game.getWitch() == EventGame.ADD_LESS) {
            Timber.i("金豆 增加/减少 成功");
            if (mWebView != null) mWebView.loadUrl("javascript:addLessGoldSuccess()");
        }
    }

    @Subscriber(tag = EventBusTags.EVENT_ADD_LESS_FAIL, mode = ThreadMode.MAIN)
    private void onAddLessGoldFail(EventGame game) {
        if (game.getWitch() == EventGame.ADD_LESS) {
            Timber.i("金豆 增加/减少 失败");
            if (mWebView != null) mWebView.loadUrl("javascript:addLessGoldFail()");
        }
    }
}
