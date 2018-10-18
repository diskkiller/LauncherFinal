package com.zhongti.huacailauncher.ui.lottery.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;

import com.jess.arms.di.component.AppComponent;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.contract.LiveContract;
import com.zhongti.huacailauncher.di.component.DaggerLiveComponent;
import com.zhongti.huacailauncher.di.module.LiveModule;
import com.zhongti.huacailauncher.presenter.LivePresenter;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.widget.bamUI.BamButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by ShuHeMing on 18/6/9
 */
public class CancelCheckTicketActivity extends BaseSupportActivity<LivePresenter> implements LiveContract.View {

    @BindView(R.id.btn_cancel_check_ticket_sure)
    BamButton btnSure;

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
        DaggerLiveComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .liveModule(new LiveModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle sadInstanceState) {
        return R.layout.activity_cancel_ticket_live;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @OnClick({R.id.btn_cancel_check_ticket_cancel, R.id.btn_cancel_check_ticket_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_check_ticket_cancel:
                killMyself();
                break;
            case R.id.btn_cancel_check_ticket_sure:
                if (mPresenter != null) mPresenter.exitCheckTicket(this, btnSure);
                break;
        }
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
        finish();
    }

    @Override
    public void onCancelCTSuccess() {
        killMyself();
    }
}
