package com.zhongti.huacailauncher.ui.personal.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.AppManager;
import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.ui.MainActivity;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.Utils;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by shuheming on 2018/2/8 0008.
 */

public class ForceExitLoginActivity extends BaseSupportActivity {

    @BindView(R.id.tv_force_exit_time)
    TextView tvTime;
    private MyTimer mTimer;

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

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
    public int initView(Bundle savedInstanceState) {
        return R.layout.activity_force_exit_login;
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mTimer = new MyTimer(11000, 1000, tvTime, this);
        mTimer.start();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
            BarUtils.hideSystemUI(this);
    }


    private static class MyTimer extends CountDownTimer {
        private WeakReference<TextView> mTvTime;
        private WeakReference<ForceExitLoginActivity> mActivity;

        MyTimer(long millisInFuture, long countDownInterval, TextView tvTime, ForceExitLoginActivity activity) {
            super(millisInFuture, countDownInterval);
            this.mTvTime = new WeakReference<>(tvTime);
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void onTick(long remain) {
            TextView tv = mTvTime.get();
            if (tv != null) {
                tv.setText(TextUtils.concat(String.valueOf(remain / 1000 - 1), "s"));
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onFinish() {
            AppManager appManager = ArmsUtils.obtainAppComponentFromContext(Utils.getApp()).appManager();
            if (mActivity.get() != null) {
                mActivity.get().finish();
            } else {
                appManager.killActivity(ForceExitLoginActivity.class);
            }
            appManager.killAll(MainActivity.class);
            Timber.e("强制退出登录成功-----------------------");
        }
    }

    @OnClick({R.id.btn_force_exit_got, R.id.btn_force_exit_reLogin})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_force_exit_got:
                finish();
                break;
            case R.id.btn_force_exit_reLogin:
                AppManager appManager = ArmsUtils.obtainAppComponentFromContext(Utils.getApp()).appManager();
                appManager.killAll(MainActivity.class);
                UserUtils.login();
                break;
            case R.id.fl_force_exit_back:
                finish();
                break;
        }
    }

    @Override
    public void onBackPressedSupport() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
