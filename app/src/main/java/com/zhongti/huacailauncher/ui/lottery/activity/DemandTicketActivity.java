package com.zhongti.huacailauncher.ui.lottery.activity;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.di.component.AppComponent;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.utils.EnvSwitchUtils;
import com.zhongti.huacailauncher.utils.code.BarUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by ShuHeMing on 18/6/9
 */
public class DemandTicketActivity extends BaseSupportActivity {

    @BindView(R.id.tv_demand_ticket_intro)
    TextView tvIntro;
    @BindView(R.id.iv_demand_ticket_erCode)
    ImageView ivErCode;

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

    }

    @Override
    public int initView(@Nullable Bundle sadInstanceState) {
        return R.layout.activity_demand_ticket;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        tvIntro.setText("1、如需索要纸质彩票（如：中奖金额1000元以上），请使用微信扫描下方二维码，选择要邮寄的彩票订单，填写快递信息，由平台统一配送，凭票可到所在省（市）体彩中心兑奖；\n" +
                "2、为了保证彩票的真实性，完成视频验票后，才能索要彩票；\n" +
                "3、邮寄彩票所产生的快递费用由用户自行承担,我司不收取任何费用！");
        if (EnvSwitchUtils.isDebug) {
            ivErCode.setImageResource(R.drawable.img_demand_er_debug);
        } else {
            ivErCode.setImageResource(R.drawable.img_demand_er_release);
        }
    }

    @OnClick(R.id.fl_check_ticket_sure_back)
    public void onViewClicked() {
        finish();
    }
}
