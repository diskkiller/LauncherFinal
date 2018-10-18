package com.zhongti.huacailauncher.model.impl;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObservable;
import com.zhongti.huacailauncher.contract.ScratchContract;
import com.zhongti.huacailauncher.model.api.service.LotteryService;
import com.zhongti.huacailauncher.model.api.service.UserService;
import com.zhongti.huacailauncher.model.entity.LotteryFunEntity;
import com.zhongti.huacailauncher.model.entity.LotteryRandomEntity;
import com.zhongti.huacailauncher.model.entity.LottiDetailEntity;
import com.zhongti.huacailauncher.model.entity.LottiListEntity;
import com.zhongti.huacailauncher.model.entity.LottiOrLeftEntity;
import com.zhongti.huacailauncher.model.entity.LottiOrListEntity;
import com.zhongti.huacailauncher.model.entity.OderEntity;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;


@ActivityScope
public class ScratchModel extends BaseModel implements ScratchContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public ScratchModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<LottiDetailEntity> getLottiDetail(long id) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LotteryService.class).getLottiDetail(id), 800);
    }

    @Override
    public Observable<List<LottiListEntity>> getLottiListData(long lotteryId, int type) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LotteryService.class).getLottiList(lotteryId, type), 500);
    }

    @Override
    public Observable<OderEntity> saveOr(Map<String, String> params) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(UserService.class).saveOr(params), 500);
    }

    @Override
    public Observable<List<LottiOrListEntity>> getLottiOr(String token, long orId) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LotteryService.class).getUserLottiOrByOrId(token, orId), 500);
    }

    @Override
    public Observable<LottiOrLeftEntity> getLottiOrLeft(String token, long orId) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LotteryService.class).getLottiOrDetailLeft(token, orId), 800);
    }

    @Override
    public Observable<Object> lottiDj(String token, long id) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LotteryService.class).lottiDJ(token, id));
    }

    @Override
    public Observable<Object> lockLotti(String token, String id, int type) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LotteryService.class).lockLotti(token, id, type), 300);
    }

    @Override
    public Observable<Object> unlockLotti(String token, String id, int type) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LotteryService.class).unlockLotti(token, id, type), 300);
    }

    @Override
    public Observable<LotteryRandomEntity> getRandomLottery(String token, long id, int type) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LotteryService.class).getRandomLottery(token, id, type), 1500);
    }

    @Override
    public Observable<List<LotteryFunEntity>> getLotteryFunList(String snNo) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LotteryService.class).getLotteryFunList(snNo));
    }

    @Override
    public Observable<List<String>> getLotteryMsg() {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LotteryService.class).getLotteryMsg());
    }
}