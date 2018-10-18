package com.zhongti.huacailauncher.model.impl;

import android.app.Application;

import com.google.gson.Gson;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.integration.IRepositoryManager;
import com.jess.arms.mvp.BaseModel;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObservable;
import com.zhongti.huacailauncher.contract.LiveContract;
import com.zhongti.huacailauncher.model.api.service.LiveService;
import com.zhongti.huacailauncher.model.entity.CLiveCodeListEntity;
import com.zhongti.huacailauncher.model.entity.CheckLiveJBEntity;
import com.zhongti.huacailauncher.model.entity.CheckLiveUrlsEntity;

import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;


@ActivityScope
public class LiveModel extends BaseModel implements LiveContract.Model {
    @Inject
    Gson mGson;
    @Inject
    Application mApplication;

    @Inject
    public LiveModel(IRepositoryManager repositoryManager) {
        super(repositoryManager);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mGson = null;
        this.mApplication = null;
    }

    @Override
    public Observable<CheckLiveUrlsEntity> getCheckUrls(Map<String, String> params) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LiveService.class).getCheckUrls(params), 500);
    }

    @Override
    public Observable<CLiveCodeListEntity> getLiveCheckDetail(String token) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LiveService.class).getLiveCheckDetail(token));
    }

    @Override
    public Observable<Object> exitCheckTicket(String token) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LiveService.class).exitCheckTicket(token), 500);
    }

    @Override
    public Observable<CheckLiveJBEntity> checkTicketJB(Map<String, String> params) {
        return HttpRxObservable.getObservable(mRepositoryManager.obtainRetrofitService(LiveService.class).checkTicketJB(params));
    }


}