package com.zhongti.huacailauncher.presenter;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.text.TextUtils;
import android.widget.ImageView;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxLifecycleUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.config.net.img.CustomImgConfigImpl;
import com.zhongti.huacailauncher.app.config.net.img.RoundD;
import com.zhongti.huacailauncher.app.http.exception.ApiException;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObserver;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.contract.MainContract;
import com.zhongti.huacailauncher.model.entity.HomeBannerEntity;
import com.zhongti.huacailauncher.model.entity.HomeDeviceInfoEntity;
import com.zhongti.huacailauncher.model.entity.HomeGameEntity;
import com.zhongti.huacailauncher.model.entity.HomeH5AddrEntity;
import com.zhongti.huacailauncher.model.entity.HomeLottiEntity;
import com.zhongti.huacailauncher.model.event.EventLotteryTopData;
import com.zhongti.huacailauncher.ui.home.adapter.HomeGameAdapter;
import com.zhongti.huacailauncher.ui.home.adapter.HomeLotteryAdapter;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.SPUtils;
import com.zhongti.huacailauncher.utils.code.SizeUtils;
import com.zhongti.huacailauncher.utils.code.Utils;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import timber.log.Timber;


@ActivityScope
public class MainPresenter extends BasePresenter<MainContract.Model, MainContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    private HomeLotteryAdapter adapterLotti;
    private HomeGameAdapter adapterGame;

    @Inject
    public MainPresenter(MainContract.Model model, MainContract.View rootView) {
        super(model, rootView);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate() {
//        reqPermission();
    }

//    private void reqPermission() {
//        PermissionUtil.externalStorage(new PermissionUtil.RequestPermission() {
//            @Override
//            public void onRequestPermissionSuccess() {
//                Timber.i("读写权限申请成功");
//            }
//
//            @Override
//            public void onRequestPermissionFailure(List<String> permissions) {
//                Timber.i("读写权限申请失败: %s", permissions.toString());
//            }
//
//            @Override
//            public void onRequestPermissionFailureWithAskNeverAgain(List<String> permissions) {
//                Timber.i("读写权限申请失败并且不再提醒: %s", permissions.toString());
//                mRootView.showPermissionSetting();
//            }
//        }, mRootView.getRxPermissions(), mErrorHandler);
//    }

    /**
     * 检查设备是否绑定
     */
    public void checkDeviceIsBind() {
        if (!NetworkUtils.isConnected()) {
            return;
        }
        mModel.checkIsBind(UserUtils.getSNNum())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<Integer>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {

                    }

                    @Override
                    protected void onSuccess(Integer response) {
                        if (response == 0) { // 未绑定
                            if (mRootView != null) mRootView.openDeviceBindPage();
                        }
                    }
                });
    }

    private List<HomeBannerEntity> bannerData;

    public void reqBannerData(boolean isEvictCache) {
        if (!NetworkUtils.isConnected()) {
            isEvictCache = false;
        }
        mModel.getHomeBannerData(UserUtils.getSNNum(), isEvictCache)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<List<HomeBannerEntity>>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {

                    }

                    @Override
                    protected void onSuccess(List<HomeBannerEntity> response) {
                        bannerData = response;
                        mRootView.setBannerData(response);
                    }
                });
    }

    public void turnToBannerPage(int position) {
        if (bannerData == null || bannerData.size() == 0 || bannerData.size() - 1 < position) {
            return;
        }
        HomeBannerEntity homeBannerEntity = bannerData.get(position);
        int type = homeBannerEntity.getType();
        switch (type) {
            case 1://不跳转
                break;
            case 2://具体跳转的html页
                mRootView.bannerTurn2H5Page(homeBannerEntity.getRedirectUrl());
                break;
            case 3://跳转彩种详情
                mRootView.bannerTurn2LotteryPage(homeBannerEntity.getRedirectUrl());
                break;
            case 4://跳转游戏
                mRootView.bannerTurn2GamePage(homeBannerEntity.getRedirectUrl());
                break;

        }
    }
//    /**
//     * 跑马灯数据
//     */
//    public void reqHomeTips(boolean finalIsEvictCache) {
//        mModel.getHomeTips(UserUtils.getSNNum(), finalIsEvictCache)
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
//                .subscribe(new HttpRxObserver<List<HomeTipsEntity>>() {
//                    @Override
//                    protected void onStart(Disposable d) {
//
//                    }
//
//                    @Override
//                    protected void onError(ApiException e) {
//
//                    }
//
//                    @Override
//                    protected void onSuccess(List<HomeTipsEntity> response) {
//                        mRootView.onRefreshMarquee(response);
//                        //请求彩票列表数据
//                        reqLottiData(finalIsEvictCache);
//                    }
//                });
//    }
//

    /**
     * 请求彩票列表的数据
     */
    public void reqLottiData(boolean isEvictCache) {
        if (!NetworkUtils.isConnected()) {
            isEvictCache = false;
        }
        mModel.getHomeLottiData(UserUtils.getSNNum(), isEvictCache)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<List<HomeLottiEntity>>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {

                    }

                    @Override
                    protected void onSuccess(List<HomeLottiEntity> response) {
                        setLottiAdapterAndData(response);
                    }
                });
    }

    /**
     * 设置彩票列表的adapter,数据
     */
    private void setLottiAdapterAndData(List<HomeLottiEntity> data) {
        if (adapterLotti == null) {
            adapterLotti = new HomeLotteryAdapter(R.layout.item_main_lotti, null);
            mRootView.setLottiListAdapter(adapterLotti);
        }
        adapterLotti.setNewData(data);
    }

    //
//    public long getHomeFirstLottiId() {
//        if (homeFirstLottiEntity != null && homeFirstLottiEntity.getId() != 0) {
//            return homeFirstLottiEntity.getId();
//        }
//        return -1;
//    }
//
    public void reqGameListData(boolean finalIsEvictCache) {
        HashMap<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(UserUtils.getToken())) {
            params.put("token", UserUtils.getToken());
        }
        params.put("snNo", UserUtils.getSNNum());
        mModel.getHomeGames(params, finalIsEvictCache)
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<List<HomeGameEntity>>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {

                    }

                    @Override
                    protected void onSuccess(List<HomeGameEntity> response) {
                        setGameListAdapter(response);
                    }
                });
    }

    private void setGameListAdapter(List<HomeGameEntity> data) {
        if (adapterGame == null) {
            adapterGame = new HomeGameAdapter(R.layout.item_main_game, null);
            mRootView.setGameListAdapter(adapterGame);
        }
        adapterGame.setNewData(data);
        /*if (data == null || data.size() == 0) {
//            mRootView.gameDataIsNull(true);
        } else {
        }*/
//        mRootView.gameDataIsNull(false);
    }

    public void reqDeviceInfo(boolean isUpdate) {
        if (!NetworkUtils.isConnected()) {
            isUpdate = false;
        }
        mModel.getHomeDeviceInfo(UserUtils.getSNNum(), isUpdate)
                .retryWhen(new RetryWithDelay(5, 1))
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<HomeDeviceInfoEntity>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {

                    }

                    @Override
                    protected void onSuccess(HomeDeviceInfoEntity response) {
                        if (mRootView != null) mRootView.setBottomData(response);
                    }
                });
    }

    public void getH5Addresses(boolean isEvictCache) {
        if (!NetworkUtils.isConnected()) {
            isEvictCache = false;
        }
        mModel.getH5Address(isEvictCache)
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .observeOn(AndroidSchedulers.mainThread())
                .retryWhen(new RetryWithDelay(5, 1))
                .subscribe(new HttpRxObserver<HomeH5AddrEntity>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {

                    }

                    @Override
                    protected void onSuccess(HomeH5AddrEntity response) {
                        //金豆商城
                        SPUtils.getInstance().put(Constants.LINK_HOME_GOLD,
                                TextUtils.isEmpty(response.getCoinMallUrl()) ? "" : response.getCoinMallUrl());
                        SPUtils.getInstance().put(Constants.IMG_HOME_GOLD,
                                TextUtils.isEmpty(response.getCoinMallImgUrl()) ? "" : response.getCoinMallImgUrl());
//                        SPUtils.getInstance().put(Constants.RECORD_TICKET_URL,
//                                TextUtils.isEmpty(response.getRecordTicketUrl()) ? "" : response.getRecordTicketUrl());
//                        SPUtils.getInstance().put(Constants.CHECK_TICKET_URL,
//                                TextUtils.isEmpty(response.getAttemptTicketUrl()) ? "" : response.getInspectTicketUrl());
                        //顶呱呱试玩
                        SPUtils.getInstance().put(Constants.EXAMPLE_TICKET_URL,
                                TextUtils.isEmpty(response.getAttemptTicketUrl()) ? "" : response.getAttemptTicketUrl());
                        //公益
                        SPUtils.getInstance().put(Constants.IMG_HOME_GY,
                                TextUtils.isEmpty(response.getPublicWelfareImgUrl()) ? "" : response.getPublicWelfareImgUrl());
                        SPUtils.getInstance().put(Constants.LINK_HOME_GY,
                                TextUtils.isEmpty(response.getPublicWelfareUrl()) ? "" : response.getPublicWelfareUrl());
                        //注册
                        SPUtils.getInstance().put(Constants.IMG_HOME_REGISTER,
                                TextUtils.isEmpty(response.getRegisterImgUrl()) ? "" : response.getRegisterImgUrl());
                        SPUtils.getInstance().put(Constants.LINK_HOME_REGISTER,
                                TextUtils.isEmpty(response.getRegisterUrl()) ? "" : response.getRegisterUrl());
                        //王座
                        SPUtils.getInstance().put(Constants.IMG_HOME_KING,
                                TextUtils.isEmpty(response.getWinningImgUrl()) ? "" : response.getWinningImgUrl());
                        SPUtils.getInstance().put(Constants.LINK_HOME_KING,
                                TextUtils.isEmpty(response.getWinningUrl()) ? "" : response.getWinningUrl());
                        //录票直播
                        SPUtils.getInstance().put(Constants.IMG_HOME_LIVERECORD,
                                TextUtils.isEmpty(response.getRecordTicketImgUrl()) ? "" : response.getRecordTicketImgUrl());
                        EventBus.getDefault().post(new EventLotteryTopData(), EventBusTags.EVENT_LOTTERY_TOP_DATA_REFRESH);
                    }
                });
    }

    public void createLotteryAdapter() {
        if (adapterLotti == null) {
            adapterLotti = new HomeLotteryAdapter(R.layout.item_main_lottery, null);
            mRootView.setLottiListAdapter(adapterLotti);
        }
    }

    /**
     * 加载头像
     */
    public void loadHeadImg(String url, ImageView ivHead) {
        if (ivHead == null) return;
        if (TextUtils.isEmpty(url) || mRootView.getActivity() == null) {
            ivHead.setImageResource(R.drawable.img_user_head_default);
            return;
        }
        mImageLoader.loadImage(mRootView.getActivity(),
                CustomImgConfigImpl
                        .builder()
                        .url(url)
                        .placeholder(R.drawable.img_user_head_default)
                        .errorPic(R.drawable.img_user_head_default)
                        .loadShape(CustomImgConfigImpl.CIRCLE)
                        .imageView(ivHead)
                        .build()
        );
    }

    /**
     * 加载普通的图片
     */
    public void loadBannerImg(String url, ImageView iv) {
        if (iv == null) return;
        if (!TextUtils.isEmpty(url)) {
            int radius = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x10);
            mImageLoader.loadImage(Utils.getApp(),
                    CustomImgConfigImpl
                            .builder()
                            .url(url)
                            .loadShape(CustomImgConfigImpl.ROUNDED)
                            .rounded(new RoundD(radius, radius, radius, radius))
                            .placeholder(R.drawable.img_place_banner)
                            .errorPic(R.drawable.img_place_banner)
                            .imageView(iv)
                            .build()
            );
        } else {
            Timber.e("图片加载err: %s", url);
            // TODO: 18/6/5 错误占位图片
            iv.setImageResource(R.drawable.img_place_banner);
        }
    }

    /**
     * 加载首页图片
     */
    public void loaFirstLottiThumb(String url, ImageView ivThumb) {
        if (!TextUtils.isEmpty(url) && ivThumb != null) {
            int radius = SizeUtils.dp2px(10);
            mImageLoader.loadImage(Utils.getApp(),
                    CustomImgConfigImpl
                            .builder()
                            .url(url)
                            .placeholder(R.drawable.img_place_home_lotti)
                            .errorPic(R.drawable.img_place_home_lotti)
                            .loadShape(CustomImgConfigImpl.ROUNDED)
                            .rounded(new RoundD(radius, radius, radius, radius))
                            .imageView(ivThumb)
                            .build()
            );
        } else {
            Timber.e("图片加载err: %s", url);
            // TODO: 18/6/5 错误占位图片
            if (ivThumb != null) ivThumb.setImageResource(R.drawable.img_place_home_lotti);
        }
    }

    public void loadGyRegisterImg(int type, String url, ImageView iv) {
        if (iv == null) return;
        int err = R.drawable.img_place_register_gy;
        if (type == 1) {
            err = R.drawable.img_mainbanner_gy;
        } else if (type == 2) {
            err = R.drawable.img_mainbanner_regist;
        }
        if (!TextUtils.isEmpty(url)) {
            int radius = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x10);
            mImageLoader.loadImage(Utils.getApp(),
                    CustomImgConfigImpl
                            .builder()
                            .url(url)
                            .loadShape(CustomImgConfigImpl.ROUNDED)
                            .rounded(new RoundD(radius, radius, radius, radius))
                            .placeholder(R.drawable.img_place_register_gy)
                            .errorPic(err)
                            .imageView(iv)
                            .build()
            );
        } else {
            Timber.e("图片加载err: %s", url);
            // TODO: 18/6/5 错误占位图片
            iv.setImageResource(err);
        }
    }

    public void loadGolds(int type, String url, ImageView iv) {
        if (iv == null) return;
        int err = R.drawable.img_place_gold_record;
        if (type == 1) {
            err = R.drawable.img_mainbanner_zjwz;
        } else if (type == 2) {
            err = R.drawable.img_mainbanner_record;
        } else if (type == 3) {
            err = R.drawable.img_mainbanner_market;
        }
        if (!TextUtils.isEmpty(url)) {
            int radius = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x10);
            mImageLoader.loadImage(Utils.getApp(),
                    CustomImgConfigImpl
                            .builder()
                            .url(url)
                            .loadShape(CustomImgConfigImpl.ROUNDED)
                            .rounded(new RoundD(radius, radius, radius, radius))
                            .placeholder(R.drawable.img_place_gold_record)
                            .errorPic(err)
                            .imageView(iv)
                            .build()
            );
        } else {
            Timber.e("图片加载err: %s", url);
            // TODO: 18/6/5 错误占位图片
            iv.setImageResource(err);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
