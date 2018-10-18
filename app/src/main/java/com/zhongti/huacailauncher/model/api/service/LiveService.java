package com.zhongti.huacailauncher.model.api.service;

import com.zhongti.huacailauncher.app.http.model.ApiResult;
import com.zhongti.huacailauncher.model.api.Api;
import com.zhongti.huacailauncher.model.entity.CLiveCodeListEntity;
import com.zhongti.huacailauncher.model.entity.CheckLiveJBEntity;
import com.zhongti.huacailauncher.model.entity.CheckLiveUrlsEntity;
import com.zhongti.huacailauncher.model.entity.RecordLiveUrlsEntity;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Create by ShuHeMing on 18/7/10
 */
public interface LiveService {
    /**
     * 获取录票直播的推流地址
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("liveApi/entryTicketLiveUrl")
    Observable<ApiResult<RecordLiveUrlsEntity>> getRecordLiveUrls();

    ///liveApi/getPlayStreamNameByCode

    /**
     * 获取录票直播的推流地址
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @FormUrlEncoded
    @POST("liveApi/getPlayStreamNameByCode")
    Observable<ApiResult<CheckLiveUrlsEntity>> getCheckUrls(@FieldMap Map<String, String> params);


    /**
     * 获取验票详情列表
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @FormUrlEncoded
    @POST("liveApi/padExamineTicketList")
    Observable<ApiResult<CLiveCodeListEntity>> getLiveCheckDetail(@Field("token") String token);

    /**
     * 验票队列退出
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @FormUrlEncoded
    @POST("liveApi/padExamineTicketLogOut")
    Observable<ApiResult<Object>> exitCheckTicket(@Field("token") String token);

    /**
     * 举报
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @FormUrlEncoded
    @POST("liveApi/userToReport")
    Observable<ApiResult<CheckLiveJBEntity>> checkTicketJB(@FieldMap Map<String, String> params);
}
