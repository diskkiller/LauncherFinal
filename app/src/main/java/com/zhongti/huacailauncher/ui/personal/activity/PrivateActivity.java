package com.zhongti.huacailauncher.ui.personal.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hcb.hcbsdk.service.msgBean.LoginReslut;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.config.net.img.CustomImgConfigImpl;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.contract.PrivateContract;
import com.zhongti.huacailauncher.di.component.DaggerPrivateComponent;
import com.zhongti.huacailauncher.di.module.PrivateModule;
import com.zhongti.huacailauncher.model.event.EvClosePrivatePage;
import com.zhongti.huacailauncher.presenter.PrivatePresenter;
import com.zhongti.huacailauncher.ui.lottery.activity.DemandTicketActivity;
import com.zhongti.huacailauncher.ui.personal.fragment.GoldOrFragment;
import com.zhongti.huacailauncher.ui.personal.fragment.LottiOrFragment;
import com.zhongti.huacailauncher.ui.personal.fragment.MarketOrFragment;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.RegexUtils;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import me.yokeyword.fragmentation.ISupportFragment;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class PrivateActivity extends BaseSupportActivity<PrivatePresenter> implements PrivateContract.View {

    public static final int LOTTI = 0;
    public static final int GOLD = 1;
    public static final int MARKET = 2;
    private ISupportFragment[] mFragments = new ISupportFragment[3];

    @BindViews({R.id.ll_private_left_item_lotti, R.id.ll_private_left_item_gold, R.id.ll_private_left_item_market})
    LinearLayout[] llIs;
    @BindViews({R.id.iv_private_left_item_lotti, R.id.iv_private_left_item_gold, R.id.iv_private_left_item_market})
    ImageView[] ivIIcons;
    @BindViews({R.id.tv_private_left_item_lotti, R.id.tv_private_left_item_goldT, R.id.tv_private_left_item_market})
    TextView[] tvIs;
    @BindView(R.id.tv_private_left_item_goldN)
    TextView tvGold;
    @BindViews({R.id.iv_private_left_item_lottiA, R.id.iv_private_left_item_goldA, R.id.iv_private_left_item_marketA})
    ImageView[] ivIAs;

    @BindView(R.id.iv_private_tx)
    ImageView ivTx;
    @BindView(R.id.tv_private_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_private_phone)
    TextView tvPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition explode = TransitionInflater.from(this).inflateTransition(R.transition.slide);
            Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.fade);
            explode.setInterpolator(new OvershootInterpolator(0.5f));
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
        if (hasFocus) BarUtils.hideSystemUI(PrivateActivity.this);
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerPrivateComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .privateModule(new PrivateModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_private; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ISupportFragment lottiOrFragment = findFragment(LottiOrFragment.class);
        if (lottiOrFragment == null) {
            mFragments[LOTTI] = LottiOrFragment.newInstance();
            mFragments[GOLD] = GoldOrFragment.newInstance();
            mFragments[MARKET] = MarketOrFragment.newInstance();
            loadMultipleRootFragment(R.id.private_right_container, 0, mFragments);
        } else {
            mFragments[LOTTI] = lottiOrFragment;
            mFragments[GOLD] = findFragment(GoldOrFragment.class);
            mFragments[MARKET] = findFragment(MarketOrFragment.class);
        }
        setSelectItem(0);
        setUserInfo();
    }

    private void setUserInfo() {
        LoginReslut.User user = UserUtils.getUser();
        if (user == null) return;
        loadHeadImg(user.getHeadPortrait(), ivTx);
        tvNickname.setText(TextUtils.isEmpty(user.getNickname()) ? "" : user.getNickname());
        String phoneNo;
        if (!TextUtils.isEmpty(user.getMobile()) && RegexUtils.isMobileExact(user.getMobile())) {
            StringBuilder sb = new StringBuilder();
            char[] chars = user.getMobile().toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (i == 3 || i == 4 || i == 5 || i == 6) {
                    sb.append('*');
                } else {
                    sb.append(chars[i]);
                }
            }
            phoneNo = sb.toString();
        } else if (!TextUtils.isEmpty(user.getMobile())) {
            phoneNo = user.getMobile();
        } else {
            phoneNo = "";
        }
        tvPhone.setText(phoneNo);
        tvGold.setText(String.valueOf((int) user.getGoldCoin()));

    }

    public void loadHeadImg(String url, ImageView ivHead) {
        AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(this);
        if (appComponent == null) return;
        if (ivHead == null) return;
        if (TextUtils.isEmpty(url)) {
            ivHead.setImageResource(R.drawable.img_default_tx);
            return;
        }
        appComponent.imageLoader().loadImage(this,
                CustomImgConfigImpl
                        .builder()
                        .url(url)
                        .placeholder(R.drawable.img_default_tx)
                        .errorPic(R.drawable.img_default_tx)
                        .loadShape(CustomImgConfigImpl.CIRCLE)
                        .imageView(ivHead)
                        .build()
        );
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    private void setSelectItem(int index) {
        llIs[0].setSelected(false);
        llIs[1].setSelected(false);
        llIs[2].setSelected(false);
        ivIIcons[0].setSelected(false);
        ivIIcons[1].setSelected(false);
        ivIIcons[2].setSelected(false);
        tvIs[0].setSelected(false);
        tvIs[1].setSelected(false);
        tvIs[2].setSelected(false);
        ivIAs[0].setSelected(false);
        ivIAs[1].setSelected(false);
        ivIAs[2].setSelected(false);
        llIs[index].setSelected(true);
        ivIIcons[index].setSelected(true);
        tvIs[index].setSelected(true);
        ivIAs[index].setSelected(true);
        if (index == 1) {
            tvGold.setSelected(true);
        } else {
            tvGold.setSelected(false);
        }
        showHideFragment(mFragments[index]);
    }

    @OnClick({R.id.ll_private_left_item_lotti, R.id.ll_private_left_item_gold, R.id.ll_private_left_item_market,
            R.id.btn_private_sp, R.id.btn_private_au, R.id.tv_private_charge, R.id.tv_private_got_lotti})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.ll_private_left_item_lotti:
                setSelectItem(0);
                break;
            case R.id.ll_private_left_item_gold:
                setSelectItem(1);
                break;
            case R.id.btn_private_sp:
                UserUtils.startProtoPage(this);
                break;
            case R.id.btn_private_au:
                openAboutUsPage();
                break;
            case R.id.tv_private_charge:
                openChargePage();
                break;
            case R.id.ll_private_left_item_market:
                setSelectItem(2);
                break;
            case R.id.tv_private_got_lotti:
                openDemandTicketPage();
                break;
        }
    }

    private void openAboutUsPage() {
        Intent intent = new Intent(this, AboutUsActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this, (Pair<View, String>[]) null);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void openChargePage() {
        UserUtils.openGameChargePage(PrivateActivity.this, "");
    }

    private void openDemandTicketPage() {
        Intent intent = new Intent(this, DemandTicketActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Subscriber(mode = ThreadMode.MAIN)
    private void onClosePage(EvClosePrivatePage evClosePrivatePage) {
        killMyself();
    }
}
