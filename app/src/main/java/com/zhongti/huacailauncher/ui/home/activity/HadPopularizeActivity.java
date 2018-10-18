package com.zhongti.huacailauncher.ui.home.activity;

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
import android.widget.EditText;
import android.widget.TextView;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxLifecycleUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.http.exception.ApiException;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObservable;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObserver;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.model.api.service.HomeService;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.Keyboard2Utils;
import com.zhongti.huacailauncher.utils.code.RegexUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.widget.progress.ProgressReqFrameDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Create by ShuHeMing on 18/6/9
 */
public class HadPopularizeActivity extends BaseSupportActivity {

    @BindView(R.id.tv_become_popularize_code)
    TextView tvCode;
    @BindView(R.id.et_become_popularize_code)
    EditText etCode;
    @BindView(R.id.btn_become_popularize_sure)
    Button btnSure;
    private SoftKeyboardToggleListener keyboardListener;

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
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BarUtils.hideSystemUI(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) BarUtils.hideSystemUI(this);
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle sadInstanceState) {
        return R.layout.activity_become_popularize;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        keyboardListener = new SoftKeyboardToggleListener();
        Keyboard2Utils.addKeyboardToggleListener(this, keyboardListener);
    }

    @OnClick({R.id.fl_become_popularize_back, R.id.btn_become_popularize_cancel, R.id.btn_become_popularize_sure})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_become_popularize_back:
            case R.id.btn_become_popularize_cancel:
                finish();
                break;
            case R.id.btn_become_popularize_sure:
                String mobile = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    ToastUtils.showShort("请填写推广员手机号!");
                    return;
                }
                if (!RegexUtils.isMobileExact(mobile)) {
                    ToastUtils.showShort("请填写正确的手机号!");
                    return;
                }
                toBePromoter(mobile);
                break;
        }
    }


    private void toBePromoter(String mobile) {
        if (this.isFinishing()) return;
        AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(this);
        if (appComponent == null) return;
        ProgressReqFrameDialog dialog = new ProgressReqFrameDialog(this);
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(UserUtils.getToken())) {
            params.put("token", UserUtils.getToken());
        }
        params.put("snNo", UserUtils.getSNNum());
        params.put("mobile", mobile);
        HttpRxObservable.getObservable(appComponent.repositoryManager().obtainRetrofitService(HomeService.class).bePromoter(params), 200)
                .compose(RxLifecycleUtils.bindToLifecycle(this))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<Object>(dialog) {
                    @Override
                    protected void onStart(Disposable d) {
                        dialog.setTextVisible(View.VISIBLE);
                        dialog.setContentText("请求中...");
                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort(e.getMsg());
                    }

                    @Override
                    protected void onSuccess(Object response) {
                        ToastUtils.showShort("成功成为该设备推广员");
                        finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Keyboard2Utils.removeKeyboardToggleListener(keyboardListener);
    }

    private class SoftKeyboardToggleListener implements Keyboard2Utils.SoftKeyboardToggleListener {
        @Override
        public void onToggleSoftKeyboard(boolean isVisible) {
            Timber.i("键盘是否可见: %s", (isVisible ? "可见" : "不可见"));
        }
    }
}
