package com.zhongti.huacailauncher.widget.progress;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.zhongti.huacailauncher.R;


/**
 * Created by SHM on 2017/05/26.
 * post请求加载进度
 */
public class RandomTicketDialog extends Dialog {

    private TextView tvPost;

    public RandomTicketDialog(Context context) {
        super(context, R.style.dialog_activity_theme_trans);
    }

    public RandomTicketDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected RandomTicketDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (getWindow() != null && getWindow().getDecorView() != null && hasFocus) {
            View decorView = getWindow().getDecorView();
            hidSysUI(decorView);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getWindow() != null) getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getWindow() != null && getWindow().getDecorView() != null) {
            View decorView = getWindow().getDecorView();
            hidSysUI(decorView);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_ticket_dialog);
        Window window = getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
        }
        setCanceledOnTouchOutside(false);
        LottieAnimationView loadingView = findViewById(R.id.random_ticket_loading);
        loadingView.playAnimation();
        loadingView.setImageAssetsFolder("images/");
        loadingView.setAnimation("LottieAnim/random_ticket.json");
        tvPost = findViewById(R.id.tv_random_ticket_loading);
    }

    public void setContentText(String text) {
//        if (TextUtils.isEmpty(text)) return;
//        if (tvPost != null) tvPost.setText(text);
    }

    public void setTextVisible(int visibility) {
        if (tvPost != null) tvPost.setVisibility(visibility);
    }

    private void hidSysUI(View decorView) {
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(flags);
        decorView.setOnFocusChangeListener((v, hasFocus) -> decorView.setSystemUiVisibility(flags));
    }

}
