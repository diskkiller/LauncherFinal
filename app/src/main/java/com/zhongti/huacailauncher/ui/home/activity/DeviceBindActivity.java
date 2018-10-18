package com.zhongti.huacailauncher.ui.home.activity;

import android.graphics.Typeface;
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
import com.zhongti.huacailauncher.model.api.service.UserService;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.SpanUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.widget.progress.ProgressReqDialog;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class DeviceBindActivity extends BaseSupportActivity {

    @BindView(R.id.tv_device_bind_step_num1)
    TextView tvNum1;
    @BindView(R.id.tv_device_bind_step_num2)
    TextView tvNum2;
    @BindView(R.id.tv_device_bind_step_num3)
    TextView tvNum3;
    @BindView(R.id.et_device_bind_code)
    EditText etCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition slide = TransitionInflater.from(this).inflateTransition(R.transition.slide);
            Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.fade);
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
        return R.layout.activity_device_bind;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initNumText();
    }

    private void initNumText() {
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");
        tvNum1.setTypeface(typeFace);
        tvNum2.setTypeface(typeFace);
        tvNum3.setTypeface(typeFace);
        tvNum1.setText(new SpanUtils().append("01").setItalic().create());
        tvNum2.setText(new SpanUtils().append("02").setItalic().create());
        tvNum3.setText(new SpanUtils().append("03").setItalic().create());
    }

    @OnClick(R.id.btn_device_bind_sure)
    public void onViewClicked() {
        String code = etCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showShort("请输入邀请码");
            return;
        }
        goAuthor(code);
    }


    private void goAuthor(String code) {
        if (this.isFinishing()) return;
        AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(this);
        if (appComponent == null) return;
        ProgressReqDialog progressReqDialog = new ProgressReqDialog(this);
        HttpRxObservable.getObservable(appComponent.repositoryManager().obtainRetrofitService(UserService.class).deviceActive(UserUtils.getSNNum(), code))
                .delay(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(this))
                .subscribe(new HttpRxObserver<Object>(progressReqDialog) {
                    @Override
                    protected void onStart(Disposable d) {
                        progressReqDialog.setTextVisible(View.VISIBLE);
                        progressReqDialog.setContentText("正在激活...");
                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort(e.getMsg());
                    }

                    @Override
                    protected void onSuccess(Object response) {
                        ToastUtils.showShort("激活成功");
                        openLogin();
                    }
                });
    }

    private void openLogin() {
        UserUtils.isLogin();
        finish();
    }

    @Override
    public void onBackPressedSupport() {
    }
}
