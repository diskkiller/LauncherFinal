package com.zhongti.huacailauncher.ui.lottery.activity;

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
import android.widget.EditText;

import com.jess.arms.di.component.AppComponent;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.contract.LiveContract;
import com.zhongti.huacailauncher.di.component.DaggerLiveComponent;
import com.zhongti.huacailauncher.di.module.LiveModule;
import com.zhongti.huacailauncher.presenter.LivePresenter;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.KeyboardUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.widget.bamUI.BamButton;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by ShuHeMing on 18/6/9
 */
public class CTLiveJBActivity extends BaseSupportActivity<LivePresenter> implements LiveContract.View {

    @BindView(R.id.et_live_yp_jb_content)
    EditText etContent;
    @BindView(R.id.btn_live_yp_jb_sure)
    BamButton btnSure;

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
        DaggerLiveComponent
                .builder()
                .appComponent(appComponent)
                .liveModule(new LiveModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle sadInstanceState) {
        return R.layout.activity_live_yp_jb;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

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


    @OnClick({R.id.et_live_yp_jb_content, R.id.btn_live_yp_jb_cancel, R.id.btn_live_yp_jb_sure, R.id.rl_live_yp_jb_root})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.et_live_yp_jb_content:
                letEditFocus(true);
                break;
            case R.id.btn_live_yp_jb_cancel:
                letEditFocus(false);
                killMyself();
                break;
            case R.id.btn_live_yp_jb_sure:
                letEditFocus(false);
                String content = etContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.showShort("请填写您要举报的内容!");
                    return;
                }
                String code = getIntent().getStringExtra(Constants.INTENT_CHECK_TICKET_SURE_CODE);
                if (mPresenter != null) mPresenter.userJb(code, content, this, btnSure);
                break;
            case R.id.rl_live_yp_jb_root:
                letEditFocus(false);
                break;
        }
    }

    private void letEditFocus(boolean isFocus) {
        if (isFocus) {
            etContent.setFocusable(true);
            etContent.setFocusableInTouchMode(true);
            etContent.requestFocus();
            etContent.requestFocusFromTouch();
            etContent.setCursorVisible(true);
            KeyboardUtils.showKeyboard(etContent);
        } else {
            if (etContent.isFocused()) {
                etContent.clearFocus();
            }
            etContent.setFocusable(false);
            etContent.setFocusableInTouchMode(false);
            etContent.setCursorVisible(false);
            KeyboardUtils.hideKeyboard(etContent);
        }
    }

    @Override
    public void onJuBaoSuccess() {
        killMyself();
    }
}
