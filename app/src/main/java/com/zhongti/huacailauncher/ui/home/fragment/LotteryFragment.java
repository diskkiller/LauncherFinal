package com.zhongti.huacailauncher.ui.home.fragment;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportFragment;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.contract.MainContract;
import com.zhongti.huacailauncher.di.component.DaggerMainComponent;
import com.zhongti.huacailauncher.di.module.MainModule;
import com.zhongti.huacailauncher.model.entity.HomeBannerEntity;
import com.zhongti.huacailauncher.model.entity.HomeLottiEntity;
import com.zhongti.huacailauncher.model.event.EventLotteryTopData;
import com.zhongti.huacailauncher.model.event.EventNetWorkChange;
import com.zhongti.huacailauncher.model.event.EventRefreshH5Urls;
import com.zhongti.huacailauncher.presenter.MainPresenter;
import com.zhongti.huacailauncher.ui.MainActivity;
import com.zhongti.huacailauncher.ui.home.adapter.HomeLotteryAdapter;
import com.zhongti.huacailauncher.ui.lottery.activity.ScratchDetailActivity;
import com.zhongti.huacailauncher.ui.lottery.activity.RecordLiveActivity;
import com.zhongti.huacailauncher.ui.web.LHWebActivity;
import com.zhongti.huacailauncher.ui.web.RH5WebActivity;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.RegexUtils;
import com.zhongti.huacailauncher.utils.code.SPUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhongti.huacailauncher.widget.banner.MZBannerView;
import com.zhongti.huacailauncher.widget.banner.holder.MZViewHolder;
import com.zhongti.huacailauncher.widget.recycler.GridLottiDecoration;
import com.zhongti.huacailauncher.widget.recycler.NoScrollGridLayoutManager;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Create by ShuHeMing on 18/7/29
 */
public class LotteryFragment extends BaseSupportFragment<MainPresenter> implements MainContract.View {

    @BindView(R.id.rv_main_lottery)
    RecyclerView recycler;
    @BindView(R.id.banner_main_lottery)
    MZBannerView<HomeBannerEntity> banner;
    @BindView(R.id.iv_main_banner_gy)
    RoundedImageView ivGy;
    @BindView(R.id.iv_main_banner_register)
    RoundedImageView ivRegister;
    @BindView(R.id.iv_main_banner_zjwz)
    RoundedImageView ivZjwz;
    @BindView(R.id.iv_main_banner_record)
    RoundedImageView ivRecord;
    @BindView(R.id.iv_main_banner_market)
    RoundedImageView ivMarket;
    @BindView(R.id.scroll_main_lottery)
    NestedScrollView scrollView;

    public static LotteryFragment newInstance() {

        Bundle args = new Bundle();

        LotteryFragment fragment = new LotteryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInceState) {
        return inflater.inflate(R.layout.fragment_main_lottery, container, false);
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        activityWef = new WeakReference<>((MainActivity) _mActivity);
        height = ArmsUtils.getDimens(Utils.getApp(), R.dimen.y492);
        initRecyclerView();
        initBanner();
        initScrollView();
        //刷新数据
        refreshData();
    }

    private int mScrollY = 0;
    private int lastScrollY = 0;

    private int height;
    WeakReference<MainActivity> activityWef;

    private void initScrollView() {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (lastScrollY < height) {
                scrollY = Math.min(height, scrollY);
                mScrollY = scrollY > height ? height : scrollY;
                if (activityWef != null && activityWef.get() != null)
                    activityWef.get().setBarAlpha(1.f - 1f * mScrollY / height);
                if (activityWef != null && activityWef.get() != null)
                    activityWef.get().setBackIsVisible(true);
            } else {
                if (activityWef.get() != null) activityWef.get().setBackIsVisible(false);
            }
            lastScrollY = scrollY;
        });
    }

    private void initRecyclerView() {
        NoScrollGridLayoutManager gridLayoutManager = new NoScrollGridLayoutManager(_mActivity, 3, GridLayoutManager.VERTICAL, false);
        gridLayoutManager.setScrollEnabled(false);
        recycler.setLayoutManager(gridLayoutManager);
        recycler.addItemDecoration(new GridLottiDecoration());
        recycler.addOnItemTouchListener(new MyOnItemClickListener());
    }

    private void initBanner() {
        ViewPager viewPager = banner.getViewPager();
        if (viewPager != null) {
            viewPager.setPageMargin(ArmsUtils.getDimens(Utils.getApp(), R.dimen.x40));
        }
        //banner
        banner.setIndicatorRes(R.drawable.banner_indicator_n, R.drawable.banner_indicator_s);
        banner.setDuration(1000);
        banner.setDelayedTime(5000);
        banner.setBannerPageClickListener((view, position) -> {
            Timber.e("BannerClick : %s", position);
            if (mPresenter != null) {
                mPresenter.turnToBannerPage(position);
            }
        });
    }

    @Subscriber(tag = EventBusTags.EVENT_NET_CONNECTED, mode = ThreadMode.MAIN)
    private void onNetConnected(EventNetWorkChange netWorkChange) {
        Timber.i("网络状态: 已连接------- 刷新彩票页数据");
        refreshData();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (banner != null) banner.start();
        refreshData();
        //判断是否显示title

        if (lastScrollY < height) {
            lastScrollY = Math.min(height, lastScrollY);
            mScrollY = lastScrollY > height ? height : lastScrollY;
            if (activityWef != null && activityWef.get() != null)
                activityWef.get().setBarAlpha(1.f - 1f * mScrollY / height);
            if (activityWef != null && activityWef.get() != null)
                activityWef.get().setBackIsVisible(true);
        } else {
            if (activityWef != null && activityWef.get() != null)
                activityWef.get().setBackIsVisible(false);
        }
    }

    public void scrollToTop() {
        if (scrollView != null) scrollView.scrollTo(0, 0);
    }

    public void refreshData() {
        if (mPresenter != null) {
            mPresenter.createLotteryAdapter();
            mPresenter.reqLottiData(true);
            mPresenter.reqBannerData(true);
        }
        EventBus.getDefault().post(new EventRefreshH5Urls(), EventBusTags.EVENT_REFRESH_H5_LINKS);
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        banner.pause();
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
    public void setLottiListAdapter(HomeLotteryAdapter adapterLotti) {
        recycler.setAdapter(adapterLotti);
    }

    @Override
    public void setBannerData(List<HomeBannerEntity> response) {
        if (response == null) {
            response = new ArrayList<>();
        }
        if (response.size() == 0) {
            response.add(new HomeBannerEntity());
        }
        banner.setPages(response, BannerViewHolder::new);
        banner.pause();
        banner.start();
    }

    @Subscriber(tag = EventBusTags.EVENT_LOTTERY_TOP_DATA_REFRESH, mode = ThreadMode.MAIN)
    private void onLotteryTopRefresh(EventLotteryTopData topData) {
        loadTopImgs();
    }

    private void loadTopImgs() {
        if (mPresenter != null) {
            mPresenter.loadGyRegisterImg(1, SPUtils.getInstance().getString(Constants.IMG_HOME_GY), ivGy);
            mPresenter.loadGyRegisterImg(2, SPUtils.getInstance().getString(Constants.IMG_HOME_REGISTER), ivRegister);
            mPresenter.loadGolds(1, SPUtils.getInstance().getString(Constants.IMG_HOME_KING), ivZjwz);
            mPresenter.loadGolds(2, SPUtils.getInstance().getString(Constants.IMG_HOME_LIVERECORD), ivRecord);
            mPresenter.loadGolds(3, SPUtils.getInstance().getString(Constants.IMG_HOME_GOLD), ivMarket);
        }
    }

    @OnClick({R.id.card_main_banner_gy, R.id.card_main_banner_register, R.id.card_main_banner_zjwz, R.id.card_main_banner_record, R.id.card_main_banner_market, R.id.tv_main_header_dggsw})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.card_main_banner_gy:
                ToastUtils.showShort("暂未开放");
                break;
            case R.id.card_main_banner_register:
                if (UserUtils.isLoginOnly()) {
                    openFpGame();
                } else {
                    UserUtils.isBindAndLogin(UserUtils::isLogin, this, _mActivity);
                }
                break;
            case R.id.card_main_banner_zjwz:
                ToastUtils.showShort("暂未开放");
                break;
            case R.id.card_main_banner_record:
                if (UserUtils.isLoginOnly()) {
                    openRecordTicketPage();
                } else {
                    UserUtils.isBindAndLogin(UserUtils::isLogin, this, _mActivity);
                }
                break;
            case R.id.card_main_banner_market:
                if (UserUtils.isLoginOnly()) {
                    openGoldMarketPage();
                } else {
                    UserUtils.isBindAndLogin(UserUtils::isLogin, this, _mActivity);
                }
                break;
            case R.id.tv_main_header_dggsw:
                openEgPage();
                break;
        }
    }

    public class BannerViewHolder implements MZViewHolder<HomeBannerEntity> {
        private ImageView mImageView;

        @Override
        public View createView(Context context) {
            @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.home_banner_layout, null);
            mImageView = view.findViewById(R.id.iv_home_banner);
            return view;
        }

        @Override
        public void onBind(Context context, int i, HomeBannerEntity data) {
            if (mPresenter != null) mPresenter.loadBannerImg(data.getUrl(), mImageView);
        }
    }

    /**
     * 打开彩票示例页面
     */
    private void openEgPage() {
        String url = SPUtils.getInstance().getString(Constants.EXAMPLE_TICKET_URL);
        if (TextUtils.isEmpty(url) && mPresenter != null) {
            if (!NetworkUtils.isConnected()) {
                ToastUtils.showShort(R.string.net_unavailable);
                return;
            }
            ToastUtils.showShort("页面正在赶来,请稍后重试");
            mPresenter.getH5Addresses(true);
            return;
        }
        Intent intent = new Intent(_mActivity, RH5WebActivity.class);
        intent.putExtra(Constants.INTENT_REMOTE_WEB_URL, url);
        intent.putExtra(Constants.INTENT_REMOTE_WEB_LOAD, 2);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(_mActivity);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void openRecordTicketPage() {
        Intent intent = new Intent(_mActivity, RecordLiveActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(_mActivity);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void openGoldMarketPage() {
        String url = SPUtils.getInstance().getString(Constants.LINK_HOME_GOLD);
        if (TextUtils.isEmpty(url) && mPresenter != null) {
            if (!NetworkUtils.isConnected()) {
                ToastUtils.showShort(R.string.net_unavailable);
                return;
            }
            ToastUtils.showShort("页面正在赶来,请稍后重试");
            mPresenter.getH5Addresses(true);
            return;
        }
        Intent intent = new Intent(_mActivity, RH5WebActivity.class);
        intent.putExtra(Constants.INTENT_REMOTE_WEB_URL, UserUtils.getDealH5Url(url));
        intent.putExtra(Constants.INTENT_REMOTE_WEB_LOAD, 2);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(_mActivity);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void openFpGame() {
        String url = SPUtils.getInstance().getString(Constants.LINK_HOME_REGISTER);
        if (TextUtils.isEmpty(url) && mPresenter != null) {
            if (!NetworkUtils.isConnected()) {
                ToastUtils.showShort(R.string.net_unavailable);
                return;
            }
            ToastUtils.showShort("页面正在赶来,请稍后重试");
            mPresenter.getH5Addresses(true);
            return;
        }
        Intent intent = new Intent(_mActivity, LHWebActivity.class);
        intent.putExtra(Constants.INTENT_REMOTE_WEB_URL, UserUtils.getDealH5Url(url));
        intent.putExtra(Constants.INTENT_REMOTE_WEB_LOAD, 1);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(_mActivity);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private class MyOnItemClickListener extends OnItemClickListener {
        @Override
        public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
            HomeLottiEntity entity = (HomeLottiEntity) adapter.getData().get(position);
            openLottiDetailPage(entity.getId());
        }
    }

    private void openLottiDetailPage(long id) {
        if (id == 0 || id == -1) return;
        if (recycler != null) {
            recycler.postDelayed(() -> {
                Intent intent = new Intent(_mActivity, ScratchDetailActivity.class);
                intent.putExtra(Constants.INTENT_TO_LOTTI_DETAIL, id);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                        && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(_mActivity);
                    startActivity(intent, optionsCompat.toBundle());
                } else {
                    startActivity(intent);
                }
            }, 100);
        }
    }

    @Override
    public void bannerTurn2H5Page(String redirectUrl) {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.net_unavailable);
            return;
        }
        if (TextUtils.isEmpty(redirectUrl)) {
            return;
        }
        if (!RegexUtils.isURL(redirectUrl)) {
            return;
        }
        Intent intent = new Intent(this.getActivity(), RH5WebActivity.class);
        intent.putExtra(Constants.INTENT_REMOTE_WEB_URL, UserUtils.getDealH5Url(redirectUrl));
        intent.putExtra(Constants.INTENT_REMOTE_WEB_LOAD, 1);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this.getActivity());
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void bannerTurn2LotteryPage(String lotteryId) {
        try {
            long id = Long.parseLong(lotteryId);
            openLottiDetailPage(id);
        } catch (Exception e) {
            Timber.e("banner打开彩票详情失败");
        }
    }

    @Override
    public void bannerTurn2GamePage(String gameUrl) {
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.net_unavailable);
            return;
        }
        if (TextUtils.isEmpty(gameUrl)) {
            return;
        }
        if (!RegexUtils.isURL(gameUrl)) {
            return;
        }
        if (UserUtils.isLoginOnly()) {
            openGamePage(gameUrl);
        } else {
            UserUtils.isBindAndLogin(UserUtils::isLogin, this, _mActivity);
        }

    }

    private void openGamePage(String gameUrl) {
        Intent intent = new Intent(this.getActivity(), RH5WebActivity.class);
        intent.putExtra(Constants.INTENT_REMOTE_WEB_URL, UserUtils.getDealH5Url(gameUrl));
        intent.putExtra(Constants.INTENT_REMOTE_WEB_LOAD, 1);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this.getActivity());
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }
}
