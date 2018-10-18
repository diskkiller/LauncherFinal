package com.zhongti.huacailauncher.ui.web;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.jess.arms.di.component.AppComponent;
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
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.utils.code.AppUtils;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.SizeUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.widget.errcall.TransCallBack;
import com.zhongti.huacailauncher.widget.errcall.WebEmptyCallback;
import com.zhongti.huacailauncher.widget.errcall.WebErrorCallback;
import com.zhongti.huacailauncher.widget.errcall.WebLoadCallback;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by shuheming on 2018/1/18 0018.
 */

public class VideoWebActivity extends BaseSupportActivity {
    @BindView(R.id.rl_web_video_container)
    RelativeLayout flContainer;
    @BindView(R.id.fl_web_video_place)
    View placeView;
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
            getWindow().setReenterTransition(fade);
        }
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
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
        if (hasFocus)
            BarUtils.hideSystemUI(this);
    }

    @Override
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_web_video;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        BarUtils.setStatusBarVisibility(this, false);
        initRetryView();
        initWebView();
        getLoadUrl();
        if (flContainer != null) {
            flContainer.postDelayed(this::delayLoad, 400);
        }
    }

    private void enableX5FullscreenFunc() {

        if (mWebView.getX5WebViewExtension() != null) {
            Toast.makeText(this, "开启X5全屏播放模式", Toast.LENGTH_LONG).show();
            Bundle data = new Bundle();

            data.putBoolean("standardFullScreen", false);// true表示标准全屏，false表示X5全屏；不设置默认false，

            data.putBoolean("supportLiteWnd", false);// false：关闭小窗；true：开启小窗；不设置默认true，

            data.putInt("DefaultVideoScreen", 2);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1

            mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams",
                    data);
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
        loadService = loadSir.register(placeView, (Callback.OnReloadListener) v -> {
            addWebView();
            if (placeView != null) placeView.setVisibility(View.VISIBLE);
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
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) loadView.getLayoutParams();
            layoutParams.width = SizeUtils.dp2px(500);
            layoutParams.height = SizeUtils.dp2px(500);
        });
        loadService.showCallback(TransCallBack.class);
    }

    public void getLoadUrl() {
        this.mUrl = getIntent().getStringExtra(Constants.INTENT_REMOTE_WEB_URL);
        if (TextUtils.isEmpty(mUrl)) {
            if (placeView != null) placeView.setVisibility(View.VISIBLE);
            if (loadService != null) loadService.showCallback(WebErrorCallback.class);
            ToastUtils.showShort("加载地址不能为空");
            VideoWebActivity.this.finish();
        }
    }

    @SuppressLint("TimberExceptionLogging")
    private void addWebView() {
        try {
            if (mWebView == null) return;
            if (flContainer == null) return;
            if (mWebView.getParent() != null) return;
            flContainer.addView(mWebView, 0, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        } catch (Exception e) {
            Timber.e(e.getMessage());
        }
    }

    private void removeWebView() {
        if (mWebView == null) return;
        if (flContainer == null) return;
        if (mWebView.getParent() != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            parent.removeView(mWebView);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        mWebView = new WebView(this, null);
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
            hadTimeout = false;
            startTimeOut();
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            Timber.i("loadUrl: %s", url);
            webView.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedError(WebView webView, int i, String s, String s1) {
            super.onReceivedError(webView, i, s, s1);
            Timber.i("加载的远程H5地址----> 加载失败了err");
            if (mWebView != null)
                mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            removeWebView();
            if (placeView != null) placeView.setVisibility(View.VISIBLE);
            if (loadService != null) loadService.showCallback(WebErrorCallback.class);
            isPageSuccess = false;
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            Timber.i("网页加载完成 onPageFinished()");
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
            if (progress == 100 && loadService != null && !hadTimeout) {
                if (isPageSuccess) {
                    if (placeView != null) placeView.setVisibility(View.GONE);
                    loadService.showSuccess();
                    isPageSuccess = true;
                } else {
                    removeWebView();
                    if (placeView != null) placeView.setVisibility(View.VISIBLE);
                    if (loadService != null) loadService.showCallback(WebErrorCallback.class);
                    isPageSuccess = false;
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
            removeWebView();
            if (placeView != null) placeView.setVisibility(View.VISIBLE);
            loadService.showCallback(WebErrorCallback.class);
            return;
        }
        addWebView();
        if (mWebView != null) {
            mWebView.loadUrl(mUrl);
            mWebView.requestFocus();
        }
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
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            mWebView.clearCache(true);
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
        stopTimeout();
        if (handler != null) handler.removeCallbacksAndMessages(null);
        AppUtils.fixInputMethodManagerLeak(this);
        super.onDestroy();
    }

    private static final int TIME_OUT = 20000;
    private Timer timer;
    private TimerTask timerTask;
    private TimeOutHandler handler;

    public void startTimeOut() {
        if (handler == null) {
            handler = new TimeOutHandler(mWebView, loadService, placeView, flContainer, VideoWebActivity.this);
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
        private WeakReference<VideoWebActivity> mActivity;
        private WeakReference<View> mPlaceView;
        private WeakReference<RelativeLayout> mContainer;

        TimeOutHandler(WebView webView, LoadService loadService, View placeView, RelativeLayout flContainer, VideoWebActivity activity) {
            this.mWebView = new WeakReference<>(webView);
            this.mLoadService = new WeakReference<>(loadService);
            this.mPlaceView = new WeakReference<>(placeView);
            this.mContainer = new WeakReference<>(flContainer);
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mWebView != null && mWebView.get() != null && mWebView.get().getProgress() < 100) {
                if (mLoadService != null && mLoadService.get() != null) {
                    mWebView.get().stopLoading();
                    if (mPlaceView != null && mPlaceView.get() != null)
                        mPlaceView.get().setVisibility(View.VISIBLE);
                    mLoadService.get().showCallback(WebErrorCallback.class);
                    if (mContainer != null && mContainer.get() != null)
                        mContainer.get().removeView(mWebView.get());
                    Timber.i("timer 计时结束,加载err");
                    if (mActivity != null && mActivity.get() != null)
                        mActivity.get().isPageSuccess = false;
                }
            }
            if (mActivity != null && mActivity.get() != null) {
                mActivity.get().stopTimeout();
                mActivity.get().hadTimeout = true;
            }
        }
    }

    private class WebViewJavaScriptFunction {
    }
}
