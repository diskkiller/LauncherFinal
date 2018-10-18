package com.zhongti.huacailauncher.model.impl;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObservable;
import com.zhongti.huacailauncher.contract.MainContract;
import com.zhongti.huacailauncher.model.api.cache.HomeCache;
import com.zhongti.huacailauncher.model.api.service.HomeService;
import com.zhongti.huacailauncher.model.api.service.UserService;
import com.zhongti.huacailauncher.model.entity.HomeBannerEntity;
import com.zhongti.huacailauncher.model.entity.HomeDeviceInfoEntity;
import com.zhongti.huacailauncher.model.entity.HomeGameEntity;
import com.zhongti.huacailauncher.model.entity.HomeH5AddrEntity;
import com.zhongti.huacailauncher.model.entity.HomeLottiEntity;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictDynamicKey;
import io.rx_cache2.Reply;


@ActivityScope
public class MainModel extends BaseModel implements MainContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public MainModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    public Observable<List<HomeBannerEntity>> getHomeBannerData(String snNo, boolean isUpdate) {
        return Observable.just(HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(HomeService.class).getHomeBanner(snNo)))
                .flatMap(listObservable -> mRepositoryManager.obtainCacheService(HomeCache.class)
                        .getHomeBanner(listObservable, new DynamicKey("getHomeBannerData"), new EvictDynamicKey(isUpdate))
                        .map(Reply::getData));
    }

    @Override
    public Observable<List<HomeLottiEntity>> getHomeLottiData(String snNo, boolean isUpdate) {
        return Observable.just(HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(HomeService.class).getHomeLotteries(snNo)))
                .flatMap(listObservable -> mRepositoryManager.obtainCacheService(HomeCache.class)
                        .getHomeLotteries(listObservable, new DynamicKey("getHomeLottiData"), new EvictDynamicKey(isUpdate))
                        .map(Reply::getData));
    }

    //    @Override
//    public Observable<List<HomeTipsEntity>> getHomeTips(String snNo, boolean isUpdate) {
//        return Observable.just(HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(HomeService.class).getHomeTips(snNo)))
//                .flatMap(listObservable -> mRepositoryManager.obtainCacheService(HomeCache.class)
//                        .getHomeTips(listObservable, new DynamicKey("getHomeTips"), new EvictDynamicKey(isUpdate))
//                        .map(Reply::getData));
//    }
//
    @Override
    public Observable<Integer> checkIsBind(String snNo) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(UserService.class).checkIsActive(snNo));
    }

    @Override
    public Observable<HomeDeviceInfoEntity> getHomeDeviceInfo(String snNo, boolean isUpdate) {
        return Observable.just(HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(HomeService.class).getHomeDeviceInfo(snNo)))
                .flatMap(listObservable -> mRepositoryManager.obtainCacheService(HomeCache.class)
                        .getHomeDeviceInfo(listObservable, new DynamicKey("getHomeDeviceInfo"), new EvictDynamicKey(isUpdate))
                        .map(Reply::getData));
    }

    @Override
    public Observable<List<HomeGameEntity>> getHomeGames(Map<String, String> params, boolean isUpdate) {
        return Observable.just(HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(HomeService.class).getHomeGames(params)))
                .flatMap(listObservable -> mRepositoryManager.obtainCacheService(HomeCache.class)
                        .getHomeGames(listObservable, new DynamicKey("getHomeGames"), new EvictDynamicKey(isUpdate))
                        .map(Reply::getData));
    }

    @Override
    public Observable<HomeH5AddrEntity> getH5Address(boolean isUpdate) {
        return Observable.just(HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(HomeService.class).getH5Addresses()))
                .flatMap(homeH5AddrEntityObservable -> mRepositoryManager.obtainCacheService(HomeCache.class)
                        .getH5Address(homeH5AddrEntityObservable, new DynamicKey("getH5Address"), new EvictDynamicKey(isUpdate))
                        .map(Reply::getData));
    }
}