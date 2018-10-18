package com.zhongti.huacailauncher.ui.lottery.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxLifecycleUtils;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.app.http.exception.ApiException;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObservable;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObserver;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.model.api.service.LotteryService;
import com.zhongti.huacailauncher.model.api.service.UserService;
import com.zhongti.huacailauncher.model.entity.CheckTicketEntity;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.SizeUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhongti.huacailauncher.widget.errcall.EmptyCallback;
import com.zhongti.huacailauncher.widget.errcall.ErrorCallback;
import com.zhongti.huacailauncher.widget.errcall.LoadingSmallCallback;
import com.zhongti.huacailauncher.widget.errcall.TransCallBack;
import com.zhongti.huacailauncher.widget.progress.ProgressReqFrameDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;

/**
 * Create by ShuHeMing on 18/6/9
 */
public class CheckTicketSureActivity extends BaseSupportActivity {

    @BindView(R.id.tv_check_ticket_sure_tips)
    TextView tvTips;
    @BindView(R.id.btn_check_ticket_sure_go)
    Button btnSure;
    @BindView(R.id.fl_check_ticket_sure_content)
    FrameLayout flContent;
    private LoadService loadService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition explode = TransitionInflater.from(this).inflateTransition(R.transition.slide);
            Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.fade);
            explode.setInterpolator(new OvershootInterpolator(1.5f));
            explode.setDuration(500);
            getWindow().setEnterTransition(explode);
            getWindow().setReenterTransition(fade);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BarUtils.hideSystemUI(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            BarUtils.hideSystemUI(this);
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle sadInstanceState) {
        return R.layout.activity_check_ticket_sure;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initRetryView();
        isNeedDelay = 1;
        enableBtn(false);
        reqData();
    }

    private void initRetryView() {
        LoadSir loadSir = new LoadSir.Builder()
                .addCallback(new LoadingSmallCallback())
                .addCallback(new EmptyCallback())
                .addCallback(new ErrorCallback())
                .addCallback(new TransCallBack())
                .build();
        loadService = loadSir.register(flContent, (Callback.OnReloadListener) v -> {
            if (!NetworkUtils.isConnected()) {
                onLoadErr(1);
                return;
            }
            //重新加载数据的逻辑
            loadService.showCallback(LoadingSmallCallback.class);
            isNeedDelay = 3;
            reqData();
        });

        loadService.setCallBack(LoadingSmallCallback.class, (context, view) -> {
            LottieAnimationView lottieAnimationView = view.findViewById(R.id.place_holder_loading);
            lottieAnimationView.setAnimation("LottieAnim/lottery_loading.json");
            TextView tvLoad = view.findViewById(R.id.tv_place_holder_loading);
            tvLoad.setTextSize(24);
            tvLoad.setVisibility(View.VISIBLE);
            tvLoad.setText("正在检查,请稍后...");
        });
        loadService.showCallback(TransCallBack.class);
    }

    private int isNeedDelay = 1;

    private void reqData() {
        switch (isNeedDelay) {
            case 1:
                if (flContent != null) flContent.postDelayed(() -> {
                    if (loadService != null) loadService.showCallback(LoadingSmallCallback.class);
                    startReq();
                }, 400);
                break;
            case 2:
                if (loadService != null) loadService.showCallback(LoadingSmallCallback.class);
                if (flContent != null) flContent.postDelayed(this::startReq, 400);
                break;
            case 3:
                startReq();
                break;
            default:
                startReq();
                break;
        }
    }

    private boolean hadData;

    private int currentState;

    private String thumb;

    private void startReq() {
        if (this.isFinishing()) {
            return;
        }
        AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(this);
        if (appComponent == null) {
            isNeedDelay = 2;
            onLoadErr(1);
            enableBtn(false);
            return;
        }
        if (!NetworkUtils.isConnected()) {
            isNeedDelay = 2;
            onLoadErr(1);
            enableBtn(false);
            return;
        }
        String code = getIntent().getStringExtra(Constants.INTENT_CHECK_TICKET_SURE_CODE);
        Map<String, String> params = new HashMap<>();
        params.put("token", UserUtils.getToken());
        params.put("code", code);
        HttpRxObservable.getObservable(appComponent.repositoryManager().obtainRetrofitService(UserService.class)
                .checkTicket(params), 800)
                .retryWhen(new RetryWithDelay(3, 2))
                .compose(RxLifecycleUtils.bindToLifecycle(CheckTicketSureActivity.this))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<CheckTicketEntity>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        onLoadErr(2);
                        enableBtn(false);
                    }

                    @Override
                    protected void onSuccess(CheckTicketEntity response) {
                        hadData = true;
                        if (TextUtils.isEmpty(response.getUrl())) {
                            currentState = 1;
                            if (btnSure != null) btnSure.setText("前往");
                            if (tvTips != null) tvTips.setText("确认前往验票？");
                        } else {
                            currentState = 2;
                            if (btnSure != null) btnSure.setText("查看");
                            if (tvTips != null) tvTips.setText("彩票已被验过，点击查看彩票照片。");
                            thumb = response.getUrl();
                        }
                        if (loadService != null) loadService.showSuccess();
                        enableBtn(true);
                    }
                });


    }

    private void enableBtn(boolean enable) {
        if (btnSure == null) return;
        if (enable) {
            btnSure.setBackgroundResource(R.drawable.bg_widow_btn_yellow);
        } else {
            btnSure.setText("请等待");
            btnSure.setBackgroundResource(R.drawable.bg_widow_btn_gray);
        }
    }

    private void onLoadErr(int type) {
        if (loadService == null) return;
        switch (type) {
            case 1:
                loadService.setCallBack(ErrorCallback.class, (context, view) -> {
                    ImageView ivErr = view.findViewById(R.id.iv_err_call_text);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ivErr.getLayoutParams();
                    layoutParams.width = SizeUtils.dp2px(240);
                    layoutParams.height = SizeUtils.dp2px(240);
                    TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                    tvTips.setTextSize(24);
                    tvTips.setText(R.string.net_unavailable);
                    TextView tvRetry = view.findViewById(R.id.tv_err_call_retry);
                    tvRetry.setVisibility(View.INVISIBLE);
                });
                break;
            case 2:
                loadService.setCallBack(ErrorCallback.class, (context, view) -> {
                    ImageView ivErr = view.findViewById(R.id.iv_err_call_text);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) ivErr.getLayoutParams();
                    layoutParams.width = SizeUtils.dp2px(240);
                    layoutParams.height = SizeUtils.dp2px(240);
                    TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                    tvTips.setTextSize(24);
                    tvTips.setText("验证失败,点击再次验证");
                    TextView tvRetry = view.findViewById(R.id.tv_err_call_retry);
                    tvRetry.setVisibility(View.INVISIBLE);
                    tvRetry.setTextSize(16);
                });
                break;
        }
        loadService.showCallback(ErrorCallback.class);
    }

    @OnClick({R.id.fl_check_ticket_sure_back, R.id.btn_check_ticket_sure_cancel, R.id.btn_check_ticket_sure_go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_check_ticket_sure_back:
            case R.id.btn_check_ticket_sure_cancel:
                finish();
                break;
            case R.id.btn_check_ticket_sure_go:
                if (!hadData) {
                    ToastUtils.showShort("未检查到彩票的验证信息,请先验证!");
                    return;
                }
                doNext();
                break;
        }
    }

    private void doNext() {
        switch (currentState) {
            case 1:
//                openVerifyPage();
                startCheckTicket();
                break;
            case 2:
                openImgPage();
                break;
            default:
                ToastUtils.showShort("未检查到彩票的验证信息,请先验证!");
                break;
        }
    }

    private void startCheckTicket() {
        if (this.isFinishing()) {
            return;
        }
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort("未连接网络,请检查您的网络设置");
            return;
        }
        if (btnSure != null) btnSure.setClickable(false);
        AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(Utils.getApp());
        ProgressReqFrameDialog reqDialog = new ProgressReqFrameDialog(this);
        String code = getIntent().getStringExtra(Constants.INTENT_CHECK_TICKET_SURE_CODE);
        HttpRxObservable.getObservable(appComponent.repositoryManager().obtainRetrofitService(LotteryService.class)
                .startCheckTicket(code, UserUtils.getToken()), 300)
                .compose(RxLifecycleUtils.bindToLifecycle(CheckTicketSureActivity.this))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<Object>(reqDialog) {
                    @Override
                    protected void onStart(Disposable d) {
                        reqDialog.setTextVisible(View.VISIBLE);
                        reqDialog.setContentText("申请中...");
                    }

                    @Override
                    protected void onError(ApiException e) {
                        if (btnSure != null) btnSure.setClickable(true);
                        ToastUtils.showShort(e.getMsg());
                    }

                    @Override
                    protected void onSuccess(Object response) {
                        if (btnSure != null) btnSure.setClickable(true);
                        openVerifyPage();
                    }
                });
    }

    private void openVerifyPage() {
        Intent intent = new Intent(this, CheckTicketLiveActivity.class);
        String code = getIntent().getStringExtra(Constants.INTENT_CHECK_TICKET_SURE_CODE);
        intent.putExtra(Constants.INTENT_CHECK_TICKET_SURE_CODE, code);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
        finish();
    }

    private void openImgPage() {
        ArrayList<String> bigImg = new ArrayList<>();
        bigImg.add(thumb);
        Intent intent = new Intent(this, BigLottiImgActivity.class);
        intent.putExtra("big_imgs_pos", 0);
        intent.putExtra("big_imgs", bigImg);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this, btnSure, "big_img");
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
        finish();
    }

    private String getCheckDealH5Url(String url, String code) {
        if (TextUtils.isEmpty(url)) {
            return "";
        } else if (!TextUtils.isEmpty(url) && url.contains("?")) {
            return url.endsWith("?")
                    ? url.substring(0, url.length() - 1) + "?token=" + UserUtils.getToken() + "&deviceNo=" + UserUtils.getSNNum() + "&code=" + code
                    : url + "&token=" + UserUtils.getToken() + "&deviceNo=" + UserUtils.getSNNum() + "&code=" + code;
        } else {
            return url + "?token=" + UserUtils.getToken() + "&deviceNo=" + UserUtils.getSNNum() + "&code=" + code;
        }
    }
}
