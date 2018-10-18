package com.zhongti.huacailauncher.app.http.fun;

import com.zhongti.huacailauncher.app.http.exception.ServerException;
import com.zhongti.huacailauncher.app.http.model.ApiResult;
import com.zhongti.huacailauncher.model.api.Api;

import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * 处理服务器返回的结果
 * Created by shuheming on 2018/1/15 0015.
 */

public class ServerResultFunction<T> implements Function<ApiResult<T>, T> {
    @Override
    public T apply(ApiResult<T> apiResult) {
        if (apiResult == null) {
            throw new ServerException(Api.SERVER_NOT_DATA, "服务器未正确返回数据...");
        }
        //打印服务器回传结果
        Timber.e(apiResult.toString());
        if (apiResult.getStatus() != Api.SUCCESS) {
            throw new ServerException(apiResult.getStatus(), apiResult.getMessage());
        }
        return apiResult.getData() == null ? (T) apiResult : apiResult.getData();
    }
}
