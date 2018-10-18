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
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxLifecycleUtils;
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
import com.zhongti.huacailauncher.app.http.exception.ApiException;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObservable;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObserver;
import com.zhongti.huacailauncher.app.utils.IntentDataHolder;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.model.api.service.LotteryService;
import com.zhongti.huacailauncher.model.entity.LottiOrLeftEntity;
import com.zhongti.huacailauncher.model.entity.LottiOrListEntity;
import com.zhongti.huacailauncher.model.event.EventDjFinish;
import com.zhongti.huacailauncher.model.event.EventDjPageClose;
import com.zhongti.huacailauncher.model.event.EventLH5PageClose;
import com.zhongti.huacailauncher.model.event.EventUpdateUser;
import com.zhongti.huacailauncher.ui.lottery.activity.ScratchDJActivity;
import com.zhongti.huacailauncher.utils.code.AppUtils;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.KeyboardUtils;
import com.zhongti.huacailauncher.utils.code.LogUtils;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.widget.errcall.TransCallBack;
import com.zhongti.huacailauncher.widget.errcall.WebEmptyCallback;
import com.zhongti.huacailauncher.widget.errcall.WebErrorCallback;
import com.zhongti.huacailauncher.widget.errcall.WebLoadCallback;
import com.zhongti.huacailauncher.widget.progress.ProgressReqDialog;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Created by shuheming on 2018/1/18 0018.
 */

public class LHWebActivity extends BaseSupportActivity {
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
        if (hasFocus) BarUtils.hideSystemUI(LHWebActivity.this);
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
        registerKeyboardListen();
        if (flContainer != null) {
            //show加载动画
            flContainer.postDelayed(this::delayLoad, 400);
        }
    }

    private void openHard() {
        try {
            if (Integer.parseInt(Build.VERSION.SDK) >= 11) {
                getWindow()
                        .setFlags(
                                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
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
            LHWebActivity.this.finish();
        }
    }

    private void registerKeyboardListen() {
        KeyboardUtils.registerSoftInputChangedListener(this, height -> {
            Timber.e("键盘弹起/收起,,,height: %s", height);
            BarUtils.hideSystemUI(LHWebActivity.this);
        });
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

        CrashReport.setJavascriptMonitor(new MyBuglyCrashReport(mWebView), true);
    }

    private static class MyBuglyCrashReport implements CrashReport.WebViewInterface {

        private WeakReference<WebView> bWebView;

        MyBuglyCrashReport(WebView webView) {
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

        /**
         * 全屏播放配置
         */
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
        EventBus.getDefault().post(new EventLH5PageClose(), EventBusTags.EVENT_ON_LOTTERY_PAGE_CLOSE);
        super.onDestroy();
    }

    private static final int TIME_OUT = 20000;
    private Timer timer;
    private TimerTask timerTask;
    private TimeOutHandler handler;

    public void startTimeOut() {
        if (handler == null) {
            handler = new TimeOutHandler(mWebView, loadService, LHWebActivity.this);
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
        private WeakReference<LHWebActivity> mActivity;

        TimeOutHandler(WebView webView, LoadService loadService, LHWebActivity activity) {
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
            LHWebActivity.this.finish();
        }

        @JavascriptInterface
        public void openPayPage(long orId, String wxUrl) {
            Timber.i("打开支付页面,,orId = " + orId + "wxUrl = " + wxUrl);
            startPayPage(orId, wxUrl);
        }

        @JavascriptInterface
        public void openDjPage(long orId) {
            obtainLotteryInfo(orId);
            Timber.i("打开兑奖页面 , 订单id: %s", orId);
        }

        @JavascriptInterface
        public void hideSysUI() {
            hideBottomNav();
            Timber.i("隐藏底部导航栏");
        }
    }

    private void hideBottomNav() {
        if (mWebView != null) mWebView.post(() -> {
            BarUtils.hideSystemUI(LHWebActivity.this);
            KeyboardUtils.hideSoftInput(this);
        });
    }

    private void startPayPage(long orId, String weiXinUrl) {
        UserUtils.openPayPage(orId, weiXinUrl);
    }

    private void obtainLotteryInfo(long orId) {
        AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(this);
        ProgressReqDialog progressReqDialog = new ProgressReqDialog(this);
        progressReqDialog.show();
        progressReqDialog.setTextVisible(View.VISIBLE);
        progressReqDialog.setContentText("正在加载...");
        HttpRxObservable.getObservable(appComponent.repositoryManager().obtainRetrofitService(LotteryService.class).getLottiOrDetailLeft(UserUtils.getToken(), orId))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(LHWebActivity.this))
                .subscribe(new HttpRxObserver<LottiOrLeftEntity>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        progressReqDialog.dismiss();
                        ToastUtils.showShort(e.getMsg());
                    }

                    @Override
                    protected void onSuccess(LottiOrLeftEntity response) {
                        if (response == null || TextUtils.isEmpty(response.getPrice()) || TextUtils.equals(response.getPrice(), "0")) {
                            ToastUtils.showShort("彩票信息有误,请重试!");
                            return;
                        }
                        obtainDjData(appComponent, orId, progressReqDialog, response.getPrice());
                    }
                });
    }

    private void obtainDjData(AppComponent appComponent, long orId, ProgressReqDialog progressReqDialog, String price) {
        HttpRxObservable.getObservable(appComponent.repositoryManager().obtainRetrofitService(LotteryService.class).getUserLottiOrByOrId(UserUtils.getToken(), orId))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(this))
                .subscribe(new HttpRxObserver<List<LottiOrListEntity>>(progressReqDialog) {
                    @Override
                    protected void onStart(Disposable d) {
                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort("获取失败,请重试!");
                    }

                    @Override
                    protected void onSuccess(List<LottiOrListEntity> response) {
                        if (response == null || response.size() == 0) {
                            ToastUtils.showShort("订单数据有误,请重试!");
                            return;
                        }
                        Timber.i("打开兑奖页面");
                        IntentDataHolder.getInstance().setHolderData(response);
                        Intent intent = new Intent(LHWebActivity.this, ScratchDJActivity.class);
                        intent.putExtra(Constants.INTENT_TO_DJ_DETAIL_POS, 0);
                        intent.putExtra(Constants.INTENT_TO_DJ_DETAIL_PRICE, price);
                        intent.putExtra(Constants.INTENT_TO_DJ_DETAIL_ORID, orId);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(LHWebActivity.this);
                            startActivity(intent, optionsCompat.toBundle());
                        } else {
                            startActivity(intent);
                        }
                    }
                });
    }

    @Subscriber(tag = EventBusTags.EVENT_PAY_SUCCESS, mode = ThreadMode.MAIN)
    private void onPaySuccess(EventUpdateUser updateUser) {
        if (mWebView != null) mWebView.loadUrl("javascript:onPaySuccess()");
        Timber.i("H5-彩票,支付成功!");
    }

    @Subscriber(tag = EventBusTags.EVENT_PAY_CANCEL, mode = ThreadMode.MAIN)
    private void onPayCancel(EventUpdateUser updateUser) {
        if (mWebView != null) mWebView.loadUrl("javascript:onPayCancel()");
        Timber.i("H5-彩票,支付取消!");
    }

    @Subscriber(mode = ThreadMode.MAIN, tag = EventBusTags.EVENT_DJ_SUCCESS_FOR_H5)
    private void onDjSuccess(EventDjFinish finish) {
        if (finish != null && finish.getOrId() != 0 && mWebView != null)
            mWebView.loadUrl("javascript:onDjSuccess('" + finish.getOrId() + "')");
        Timber.i("H5-彩票,兑奖成功!");
    }

    @Subscriber(mode = ThreadMode.MAIN, tag = EventBusTags.EVENT_ON_DJ_PAGE_CLOSE)
    private void onDJPageClose(EventDjPageClose pageClose) {
        if (mWebView != null) mWebView.loadUrl("javascript:onDjPageClosed()");
        Timber.i("H5-彩票,兑奖页面关闭!");
    }

}
