package com.zhongti.huacailauncher.app.http.observer;


import android.app.Dialog;

import com.zhongti.huacailauncher.app.http.exception.ApiException;
import com.zhongti.huacailauncher.app.http.exception.ExceptionEngine;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;


/**
 * 适用Retrofit网络请求Observer(监听者)
 * 备注:
 * 1.重写onSubscribe，添加请求标识
 * 2.重写onError，封装错误/异常处理，移除请求
 * 3.重写onNext，移除请求
 * 4.重写cancel，取消请求
 * <p>
 * Create by ShuHeMing on 2017/11/20
 */
public abstract class HttpRxObserver<T> implements Observer<T> {

    private Dialog mProgress;

    protected HttpRxObserver() {
    }

    /**
     * 提供带进度的构造函数
     *
     * @param progress 自定义的进度ProgressDialog
     */
    public HttpRxObserver(Dialog progress) {
        mProgress = progress;
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof ApiException) {
            onError((ApiException) e);
        } else {
            onError(new ApiException(e, ExceptionEngine.ERROR.UNKNOWN));
        }
        hideProgress();
    }

    @Override
    public void onComplete() {
        hideProgress();
    }

    @Override
    public void onNext(@NonNull T t) {
        onSuccess(t);
        hideProgress();
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        showProgress();
        onStart(d);
    }

    private void showProgress() {
        if (mProgress != null && !mProgress.isShowing()) {
            mProgress.show();
        }
    }

    private void hideProgress() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
            mProgress = null;
        }
    }

    protected abstract void onStart(Disposable d);

    /**
     * 错误/异常回调
     */
    protected abstract void onError(ApiException e);

    /**
     * 成功回调
     */
    protected abstract void onSuccess(T response);


}
