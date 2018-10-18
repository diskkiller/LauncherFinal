package com.zhongti.huacailauncher.ui.personal.activity;

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
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.utils.code.BarUtils;

import butterknife.OnClick;

/**
 * Create by ShuHeMing on 18/6/8
 */
public class ExitLoginActivity extends BaseSupportActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.slide);
            Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.fade);
            slide.setInterpolator(new OvershootInterpolator(1.5f));
            slide.setDuration(500);
            getWindow().setEnterTransition(slide);
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
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_exit_login;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @OnClick({R.id.fl_exit_back, R.id.btn_exit_login_cancel, R.id.btn_exit_login_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_exit_back:
            case R.id.btn_exit_login_cancel:
                finish();
                break;
            case R.id.btn_exit_login_sure:
                UserUtils.logOut();
                break;
        }
    }
}
