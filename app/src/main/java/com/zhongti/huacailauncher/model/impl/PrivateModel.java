package com.zhongti.huacailauncher.model.impl;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObservable;
import com.zhongti.huacailauncher.contract.PrivateContract;
import com.zhongti.huacailauncher.model.api.service.UserService;
import com.zhongti.huacailauncher.model.entity.GoldRecordEntity;
import com.zhongti.huacailauncher.model.entity.PrivateLottiListEntity;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;


@ActivityScope
public class PrivateModel extends BaseModel implements PrivateContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public PrivateModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<List<PrivateLottiListEntity>> getPrivateLottiList(String token, int type, int start, int count) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(UserService.class).getPrivateLottiList(token, type, start, count));
    }

    @Override
    public Observable<List<GoldRecordEntity>> getGoldRecord(Map<String, String> params) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(UserService.class).getGoldRecord(params), 500);
    }
}