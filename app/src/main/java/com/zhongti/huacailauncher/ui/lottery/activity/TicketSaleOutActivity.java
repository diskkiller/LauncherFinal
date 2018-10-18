package com.zhongti.huacailauncher.ui.lottery.activity;

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
import com.zhongti.huacailauncher.utils.code.BarUtils;

import butterknife.OnClick;

/**
 * Create by ShuHeMing on 18/6/9
 */
public class TicketSaleOutActivity extends BaseSupportActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //设置转场动画
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
        return R.layout.activity_ticket_sale_out;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @OnClick({R.id.fl_ticket_sale_out_back, R.id.btn_ticket_sale_out_cancel, R.id.btn_ticket_sale_out_go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_ticket_sale_out_back:
            case R.id.btn_ticket_sale_out_cancel:
                finish();
                break;
            case R.id.btn_ticket_sale_out_go:

                break;
        }
    }
}
