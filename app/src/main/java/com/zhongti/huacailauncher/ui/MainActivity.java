package com.zhongti.huacailauncher.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcb.hcbsdk.service.msgBean.LoginReslut;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.integration.AppManager;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.widget.autolayout.AutoTabLayout;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.utils.NoHandleTimer;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.contract.MainContract;
import com.zhongti.huacailauncher.di.component.DaggerMainComponent;
import com.zhongti.huacailauncher.di.module.MainModule;
import com.zhongti.huacailauncher.model.entity.HomeDeviceInfoEntity;
import com.zhongti.huacailauncher.model.event.EventNetWorkChange;
import com.zhongti.huacailauncher.model.event.EventRefreshH5Urls;
import com.zhongti.huacailauncher.model.event.EventUpdateUser;
import com.zhongti.huacailauncher.presenter.MainPresenter;
import com.zhongti.huacailauncher.ui.home.activity.HadPopularizeActivity;
import com.zhongti.huacailauncher.ui.home.activity.DeviceBindActivity;
import com.zhongti.huacailauncher.ui.home.fragment.LotteryFragment;
import com.zhongti.huacailauncher.ui.home.fragment.GameFragment;
import com.zhongti.huacailauncher.ui.personal.activity.PrivateActivity;
import com.zhongti.huacailauncher.ui.personal.activity.ForceExitLoginActivity;
import com.zhongti.huacailauncher.ui.personal.activity.ExitLoginActivity;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.ScreenUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhongti.huacailauncher.widget.bamUI.BamRoundedImgView;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import me.yokeyword.fragmentation.ISupportFragment;
import timber.log.Timber;


public class MainActivity extends BaseSupportActivity<MainPresenter> implements MainContract.View {

    public static final int LOTTERY = 0;
    public static final int GAME = 1;
    private ISupportFragment[] mFragments = new ISupportFragment[2];
    private static final String[] tabText = {"彩票", "娱乐"};
    @BindView(R.id.fl_main_container)
    FrameLayout flContainer;
    @BindView(R.id.view_main_toolbar_back)
    View viewTop;
    @BindView(R.id.main_toolbar)
    RelativeLayout toolBar;
    @BindView(R.id.tab_main_toolbar)
    AutoTabLayout tablayout;
    @BindView(R.id.iv_main_toolbar_tx)
    BamRoundedImgView ivTx;
    @BindView(R.id.iv_main_toolbar_loginStatus)
    TextView tvStatus;
    @BindView(R.id.tv_main_bottom_area)
    TextView tvArea;
    @BindView(R.id.tv_main_bottom_business)
    TextView tvBusiness;
    @BindView(R.id.tv_main_bottom_deviceName)
    TextView tvDeviceName;
    @BindView(R.id.tv_main_bottom_deviceNo)
    TextView tvDeviceNo;
    @BindView(R.id.tv_main_bottom_service)
    TextView tvService;
    @BindView(R.id.tv_main_bottom_company)
    TextView tvCompany;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BarUtils.hideSystemUI(MainActivity.this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) BarUtils.hideSystemUI(MainActivity.this);
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ISupportFragment lottiOrFragment = findFragment(LotteryFragment.class);
        if (lottiOrFragment == null) {
            mFragments[LOTTERY] = LotteryFragment.newInstance();
            mFragments[GAME] = GameFragment.newInstance();
            loadMultipleRootFragment(R.id.fl_main_container, 0, mFragments);
        } else {
            mFragments[LOTTERY] = lottiOrFragment;
            mFragments[GAME] = findFragment(GameFragment.class);
        }
        initTabLayout();
        TabLayout.Tab ta1 = tablayout.getTabAt(0);
        if (ta1 != null) {
            ta1.select();
        }
        setDefSelect();
        refreshData(true);
        if (toolBar != null) toolBar.postDelayed(() -> updateUserInfo(UserUtils.getUser()), 1000);
        Timber.i("屏幕分辨率: " + ScreenUtils.getScreenWidth() + ",,," + ScreenUtils.getScreenHeight());
        Timber.i("屏幕真实的宽高: " + ScreenUtils.getScreenRealWidth() + ",,," + ScreenUtils.getScreenRealHeight());
    }

    private void setDefSelect() {
        showHideFragment(mFragments[0]);
        viewTop.setVisibility(View.VISIBLE);
        toolBar.setBackgroundColor(colorTrans1);
        flContainer.setBackgroundResource(R.drawable.bg_main_lottery);
    }

    private void initTabLayout() {
        addCustomTab(tabText, 0);
        addCustomTab(tabText, 1);
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Timber.i("onTabSelected()  %s", tabText[tab.getPosition()]);
                showHideFragment(mFragments[tab.getPosition()]);
                switch (tab.getPosition()) {
                    case 0:
                        viewTop.setVisibility(View.VISIBLE);
                        flContainer.setBackgroundResource(R.drawable.bg_main_lottery);
                        break;
                    case 1:
                        viewTop.setVisibility(View.INVISIBLE);
                        toolBar.setBackgroundColor(colorTrans1);
                        flContainer.setBackgroundResource(R.drawable.bg_main_game);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Timber.i("onTabUnselected()  %s", tabText[tab.getPosition()]);
                if (tab.getPosition() == 0) {
                    LotteryFragment lotteryFragment = findFragment(LotteryFragment.class);
                    if (lotteryFragment != null) lotteryFragment.scrollToTop();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Timber.i("onTabReselected()  %s", tabText[tab.getPosition()]);
            }
        });
    }

    private int colorTrans1 = ArmsUtils.getColor(Utils.getApp(), R.color.bg_toolbar);
    private int colorTrans2 = ArmsUtils.getColor(Utils.getApp(), R.color.bg_toolbar1);

    public void setBackIsVisible(boolean isVisible) {
        if (viewTop != null) viewTop.setVisibility(isVisible ? View.VISIBLE : View.INVISIBLE);
        if (toolBar != null) toolBar.setBackgroundColor(isVisible ? colorTrans1 : colorTrans2);
    }

    public void setBarAlpha(float v) {
        viewTop.setAlpha(v);
    }

    private void addCustomTab(String[] mTabText, int i) {
        TabLayout.Tab tab = tablayout.newTab();
        //加载自定义的布局
        View view = LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        TextView tv = view.findViewById(R.id.tv_tab_s);
        tv.setText(mTabText[i]);
        tab.setCustomView(view);
        tablayout.addTab(tab, false);
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserUtils.isLoginOnly()) {
            if (!NoHandleTimer.isIsRunning()) NoHandleTimer.startTimer();
        }
        updateUserInfo(UserUtils.getUser());
        refreshData(false);
        LotteryFragment lotteryFragment = findFragment(LotteryFragment.class);
        if (lotteryFragment != null) lotteryFragment.scrollToTop();
    }

    @Subscriber(tag = EventBusTags.EVENT_NET_CONNECTED, mode = ThreadMode.MAIN)
    private void onNetConnected(EventNetWorkChange netWorkChange) {
        Timber.i("网络状态: 已连接------- 刷新MainActivity数据");
        refreshData(false);
    }

    /**
     * 刷新数据
     */
    private void refreshData(boolean isFirst) {
        if (mPresenter != null) {
            if (isFirst) mPresenter.checkDeviceIsBind();
            mPresenter.getH5Addresses(true);
            mPresenter.reqDeviceInfo(true);
        }
    }

    private long mClickTime;

    @OnClick({R.id.iv_main_toolbar_logo, R.id.iv_main_toolbar_tx, R.id.iv_main_toolbar_loginStatus, R.id.ll_main_toolbar_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_main_toolbar_logo:
                if (System.currentTimeMillis() - mClickTime < 800) {
                    openBecomePopularizePage();
                } else {
                    mClickTime = System.currentTimeMillis();
                }
                Timber.i("设备的mac地址: %s", UserUtils.getSNNum());
                break;
            case R.id.iv_main_toolbar_tx:
                if (UserUtils.isLoginOnly()) {
                    openPrivatePage();
                } else {
                    UserUtils.isBindAndLogin(UserUtils::isLogin, MainActivity.this, this);
                }
                break;
            case R.id.iv_main_toolbar_loginStatus:
                if (UserUtils.isLoginOnly()) {
                    openExitLoginPage();
                } else {
                    UserUtils.isBindAndLogin(UserUtils::isLogin, MainActivity.this, this);
                }
                break;
            case R.id.ll_main_toolbar_login:
                break;
        }
    }

    private void openPrivatePage() {
        Intent intent = new Intent(this, PrivateActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void openBecomePopularizePage() {
        Intent intent = new Intent(this, HadPopularizeActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }

    }

    private void openExitLoginPage() {
        Intent intent = new Intent(this, ExitLoginActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setBottomData(HomeDeviceInfoEntity response) {
        tvArea.setText("地区：" + (response == null || TextUtils.isEmpty(response.getProvince()) ? "" : response.getProvince()));
        tvBusiness.setText("商家：" + (response == null || TextUtils.isEmpty(response.getMerchant()) ? "" : response.getMerchant()));
        tvDeviceName.setText("设备：" + (response == null || TextUtils.isEmpty(response.getEquipment()) ? "" : response.getEquipment()));
        tvDeviceNo.setText("编号：" + (response == null || TextUtils.isEmpty(response.getSnCode()) ? "" : response.getSnCode()));
        tvService.setText("客服：" + (response == null || TextUtils.isEmpty(response.getCustomerService()) ? "" : response.getCustomerService()));
        tvCompany.setText("技术：" + (response == null || TextUtils.isEmpty(response.getCompany()) ? "" : response.getCompany()));
    }

    @Subscriber(tag = EventBusTags.EVENT_REFRESH_H5_LINKS, mode = ThreadMode.MAIN)
    private void onRefreshH5Urls(EventRefreshH5Urls h5Urls) {
        if (mPresenter != null) mPresenter.getH5Addresses(true);
    }

    @Subscriber(tag = EventBusTags.EVENT_LOGIN_SUCCESS, mode = ThreadMode.MAIN)
    private void onLoginSuccess(EventUpdateUser updateUser) {
        if (updateUser.getUser() != null) {
            updateUserInfo(updateUser.getUser());
        }
    }

    @Subscriber(tag = EventBusTags.EVENT_LOGIN_OUT, mode = ThreadMode.MAIN)
    private void onLoginOut(EventUpdateUser updateUser) {
        NoHandleTimer.pauseTimer();
        AppManager appManager = ArmsUtils.obtainAppComponentFromContext(Utils.getApp()).appManager();
        appManager.killAll(MainActivity.class, ForceExitLoginActivity.class);
        updateUserInfo(UserUtils.getUser());
    }

    @Subscriber(tag = EventBusTags.EVENT_FORCE_LOGIN_OUT, mode = ThreadMode.MAIN)
    private void onFoeceLoginOut(EventUpdateUser updateUser) {
        updateUserInfo(UserUtils.getUser());
    }

    @Subscriber(tag = EventBusTags.EVENT_SOCKET_CONNECTED, mode = ThreadMode.MAIN)
    private void onSocketConnected(EventUpdateUser updateUser) {
        if (!NoHandleTimer.isIsRunning()) NoHandleTimer.startTimer();
        updateUserInfo(updateUser.getUser());
    }

    public void updateUserInfo(LoginReslut.User user) {
        if (ivTx == null || tvStatus == null) return;
        if (user != null) {
            if (mPresenter != null) mPresenter.loadHeadImg(user.getHeadPortrait(), ivTx);
            tvStatus.setText(user.getNickname());
            tvStatus.setText("退出");
        } else {
            ivTx.setImageResource(R.drawable.img_user_head_default);
            tvStatus.setText("未登录");
        }
    }


    @Override
    public void openDeviceBindPage() {
        if (this.isFinishing()) return;
        Intent intent = new Intent(this, DeviceBindActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressedSupport() {
    }
}
