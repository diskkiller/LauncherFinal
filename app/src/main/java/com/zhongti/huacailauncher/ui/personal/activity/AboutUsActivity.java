package com.zhongti.huacailauncher.ui.personal.activity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.utils.code.AppUtils;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.SpanUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by ShuHeMing on 18/6/8
 */
public class AboutUsActivity extends BaseSupportActivity {

    @BindView(R.id.tv_about_us_problem)
    TextView tvProblem;
    @BindView(R.id.tv_about_us_contact)
    TextView tvContact;
    @BindView(R.id.tv_about_us_gov)
    TextView tvGov;
    @BindView(R.id.tv_about_us_version)
    TextView tvVersion;

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
        return R.layout.activity_about_us;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        int colorBlue = ArmsUtils.getColor(this, R.color.private_about_us);
        int colorBlack = ArmsUtils.getColor(this, R.color.black2);
        tvProblem.setText(new SpanUtils().append("产品使用问题,联系  ").setForegroundColor(colorBlack)
                .append("400-008-1027").setForegroundColor(colorBlue).create());
        tvContact.setText(new SpanUtils().append("商务合作意向,联系  ").setForegroundColor(colorBlack)
                .append("400-008-1027").setForegroundColor(colorBlue).create());
        tvVersion.setText("华彩宝 V" + AppUtils.getAppVersionName());
    }

    @OnClick(R.id.fl_about_us_back)
    public void onViewClicked() {
        finish();
    }
}
