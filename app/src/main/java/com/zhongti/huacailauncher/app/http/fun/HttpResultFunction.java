package com.zhongti.huacailauncher.app.http.fun;


import com.zhongti.huacailauncher.app.http.exception.ExceptionEngine;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * http结果处理函数
 * <p>
 * Create by ShuHeMing 2017/11/20
 */
public class HttpResultFunction<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(@NonNull Throwable throwable) throws Exception {
        //打印具体错误
        Timber.e("HttpResultFunction:%s", throwable.toString());
        return Observable.error(ExceptionEngine.handleException(throwable));
    }
}
