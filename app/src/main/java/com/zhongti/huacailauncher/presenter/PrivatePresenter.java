package com.zhongti.huacailauncher.presenter;

import android.app.Application;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.http.exception.ApiException;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObserver;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.contract.PrivateContract;
import com.zhongti.huacailauncher.model.entity.GoldRecordEntity;
import com.zhongti.huacailauncher.model.entity.PrivateLottiListEntity;
import com.zhongti.huacailauncher.ui.personal.adapter.PrivateGoldAdapter;
import com.zhongti.huacailauncher.ui.personal.adapter.PrivateLotti1Adapter;
import com.zhongti.huacailauncher.ui.personal.adapter.PrivateLotti2Adapter;
import com.zhongti.huacailauncher.ui.personal.adapter.PrivateLotti3Adapter;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class PrivatePresenter extends BasePresenter<PrivateContract.Model, PrivateContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    private PrivateLotti1Adapter adapterLotti1;
    private PrivateLotti2Adapter adapterLotti2;
    private PrivateLotti3Adapter adapterLotti3;
    private PrivateGoldAdapter adapterGold;

    @Inject
    public PrivatePresenter(PrivateContract.Model model, PrivateContract.View rootView) {
        super(model, rootView);
    }

    private int lottiPageIndex = 1;
    private static final int lottiPageCount = 10;

    /**
     * 请求彩票订单列表
     */
    public void reqLottiBuyList(boolean isRefresh) {
        if (isRefresh && !NetworkUtils.isConnected()) {
            mRootView.onRefreshLotti1Err(1);
            return;
        }
        if (isRefresh) {
            lottiPageIndex = 1;
        } else {
            lottiPageIndex++;
        }
        mModel.getPrivateLottiList(UserUtils.getToken(), 1, lottiPageIndex, lottiPageCount)
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<List<PrivateLottiListEntity>>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        if (!isRefresh) {
                            lottiPageIndex--;
                            mRootView.onRefreshLotti1Err(3);
                        } else {
                            mRootView.onRefreshLotti1Err(2);
                        }
                        mRootView.finishLoadLotti1(isRefresh);
                    }

                    @Override
                    protected void onSuccess(List<PrivateLottiListEntity> response) {
                        setLotti1AdapterAndData(response, isRefresh);
                    }
                });
    }

    /**
     * 设置彩票订单列表数据
     */
    private void setLotti1AdapterAndData(List<PrivateLottiListEntity> data, boolean isRefresh) {
        if (adapterLotti1 == null) {
            adapterLotti1 = new PrivateLotti1Adapter(R.layout.item_private_lotti_buy, null);
            mRootView.setLotti1Adapter(adapterLotti1);
        }
        if (isRefresh) {//刷新数据
            adapterLotti1.setNewData(data);
            mRootView.refreshLotti1End(data);
        } else {//加载更多
            if (data == null || data.size() == 0) {
                mRootView.loadLotti1End();
            } else {
                adapterLotti1.addData(data);
            }
        }
        mRootView.finishLoadLotti1(isRefresh);
    }

    /**
     * 删除一条彩票订单成功
     */
    public void delLottiOrSuccess(int pos) {
        if (adapterLotti1 != null) {
            adapterLotti1.remove(pos);
            int size = adapterLotti1.getData().size();
            if (size == 0) {
                mRootView.refreshLotti1End(null);
            }
        }
    }

    /**
     * 请求验票订单列表
     */
    public void reqLottiCheckList() {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            data.add("");
        }
        setLotti2AdapterAndData(data);
    }

    private void setLotti2AdapterAndData(List<String> data) {
        if (adapterLotti2 == null) {
            adapterLotti2 = new PrivateLotti2Adapter(R.layout.item_private_lotti_buy, null);
            mRootView.setLotti2Adapter(adapterLotti2);
        }
        adapterLotti2.setNewData(data);
    }

    /**
     * 请求邮寄订单列表
     */
    public void reqLottiPostList() {
//        List<String> data = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            data.add("");
//        }
//        setLotti3AdapterAndData(data);
    }

    private void setLotti3AdapterAndData(List<String> data) {
        if (adapterLotti3 == null) {
            adapterLotti3 = new PrivateLotti3Adapter(R.layout.item_private_lotti_post, null);
            mRootView.setLotti3Adapter(adapterLotti3);
        }
        adapterLotti3.setNewData(data);
    }

    private int goldPageIndex = 1;
    private static final int goldPageCount = 10;

    /**
     * 请求金豆记录列表
     */
    public void reqLottiGoldList(boolean isRefresh) {
//        List<String> data = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            data.add("");
//        }
//        setGoldAdapterAndData(data);
        if (isRefresh && !NetworkUtils.isConnected()) {
            mRootView.onRefreshGoldErr(1);
            return;
        }
        if (isRefresh) {
            goldPageIndex = 1;
        } else {
            goldPageIndex++;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("token", UserUtils.getToken());
        params.put("type", String.valueOf(1));
        params.put("start", String.valueOf(goldPageIndex));
        params.put("count", String.valueOf(goldPageCount));
        mModel.getGoldRecord(params)
                .retryWhen(new RetryWithDelay(3, 2))
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<List<GoldRecordEntity>>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        if (!isRefresh) {
                            goldPageIndex--;
                            mRootView.onRefreshGoldErr(3);
                        } else {
                            mRootView.onRefreshGoldErr(2);
                        }
                        mRootView.finishLoadGold(isRefresh);
                    }

                    @Override
                    protected void onSuccess(List<GoldRecordEntity> response) {
                        setGoldAdapterAndData(response, isRefresh);
                    }
                });
    }

    private void setGoldAdapterAndData(List<GoldRecordEntity> data, boolean isRefresh) {
        if (adapterGold == null) {
            adapterGold = new PrivateGoldAdapter(R.layout.item_private_gold, null);
            mRootView.setGoldAdapter(adapterGold);
        }
        if (isRefresh) {//刷新数据
            adapterGold.setNewData(data);
            mRootView.refreshGoldEnd(data);
        } else {//加载更多
            if (data == null || data.size() == 0) {
                mRootView.loadGoldEnd();
            } else {
                adapterGold.addData(data);
            }
        }
        mRootView.finishLoadGold(isRefresh);
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
