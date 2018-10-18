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
import android.widget.Button;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxLifecycleUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.http.exception.ApiException;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObservable;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObserver;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.model.api.service.UserService;
import com.zhongti.huacailauncher.model.event.EventDelLottiSuccess;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.widget.progress.ProgressReqFrameDialog;

import org.simple.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

/**
 * Create by ShuHeMing on 18/6/12
 */
public class DelLottiSureActivity extends BaseSupportActivity {

    @BindView(R.id.btn_del_lotti_sure)
    Button btnSure;

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
    public int initView(@Nullable Bundle sadInstanceState) {
        return R.layout.activity_del_lotti_sure;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @OnClick({R.id.fl_del_lotti_sure_back, R.id.btn_del_lotti_sure_cancel, R.id.btn_del_lotti_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_del_lotti_sure_back:
            case R.id.btn_del_lotti_sure_cancel:
                finish();
                break;
            case R.id.btn_del_lotti_sure:
                delSure();
                break;
        }
    }

    private void delSure() {
        if (this.isFinishing()) return;
        long id = getIntent().getLongExtra("del_id", -1);
        AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(this);
        if (appComponent == null) return;
        btnSure.setClickable(false);
        ProgressReqFrameDialog progressReqDialog = new ProgressReqFrameDialog(this);
        HttpRxObservable.getObservable(appComponent.repositoryManager().obtainRetrofitService(UserService.class).delLottiItem(UserUtils.getToken(), id), 500)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(this))
                .subscribe(new HttpRxObserver<Object>(progressReqDialog) {
                    @Override
                    protected void onStart(Disposable d) {
                        progressReqDialog.setTextVisible(View.VISIBLE);
                        progressReqDialog.setContentText("正在删除...");
                    }

                    @Override
                    protected void onError(ApiException e) {
                        btnSure.setClickable(true);
                        ToastUtils.showShort(e.getMsg());
                    }

                    @Override
                    protected void onSuccess(Object response) {
                        ToastUtils.showShort("删除成功");
                        DelLottiSureActivity.this.finish();
                        int del_pos = getIntent().getIntExtra("del_pos", -1);
                        EventBus.getDefault().post(new EventDelLottiSuccess(del_pos), EventBusTags.EVENT_DEL_LOTTI_SUCCESS);
                        btnSure.setClickable(true);
                    }
                });
    }
}
