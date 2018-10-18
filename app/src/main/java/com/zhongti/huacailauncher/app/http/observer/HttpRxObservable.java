package com.zhongti.huacailauncher.app.http.observer;

import com.zhongti.huacailauncher.app.http.fun.HttpResultFunction;
import com.zhongti.huacailauncher.app.http.fun.ServerResultFunction;
import com.zhongti.huacailauncher.app.http.model.ApiResult;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * 适用Retrofit网络请求Observable(被监听者)
 * <p>
 * Create by ShuHeMing on 2017/1/6
 */
public class HttpRxObservable {

    /**
     * 获取被监听者
     * 备注:网络请求Observable构建
     * data:网络请求参数
     * <h1>补充说明</h1>
     * 传入LifecycleProvider<FragmentEvent>手动管理生命周期,避免内存溢出
     * 备注:需要继承RxFragment,RxDialogFragment
     */
    public static <T> Observable<T> getObservable(Observable<ApiResult<T>> apiObservable) {
        return apiObservable.map(new ServerResultFunction<>())
                .onErrorResumeNext(new HttpResultFunction<>())
                .subscribeOn(Schedulers.io());
    }

    public static <T> Observable<T> getObservable(Observable<ApiResult<T>> apiObservable, long delayTime) {
        return apiObservable
                .delay(delayTime, TimeUnit.MILLISECONDS)
                .map(new ServerResultFunction<>())
                .onErrorResumeNext(new HttpResultFunction<>())
                .subscribeOn(Schedulers.io());
    }

}
