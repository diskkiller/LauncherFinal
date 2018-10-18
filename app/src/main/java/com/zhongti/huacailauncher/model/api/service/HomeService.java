package com.zhongti.huacailauncher.model.api.service;

import com.zhongti.huacailauncher.app.http.model.ApiResult;
import com.zhongti.huacailauncher.model.api.Api;
import com.zhongti.huacailauncher.model.entity.HomeBannerEntity;
import com.zhongti.huacailauncher.model.entity.HomeDeviceInfoEntity;
import com.zhongti.huacailauncher.model.entity.HomeGameEntity;
import com.zhongti.huacailauncher.model.entity.HomeH5AddrEntity;
import com.zhongti.huacailauncher.model.entity.HomeLottiEntity;
import com.zhongti.huacailauncher.model.entity.HomeTipsEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * 首页接口
 * Create by ShuHeMing on 18/6/8
 */
public interface HomeService {

    /**
     * 首页轮播
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/home/posters")
    Observable<ApiResult<List<HomeBannerEntity>>> getHomeBanner(@Query("snNo") String snNo);

    /**
     * 首页彩种列表
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/home/lotteries")
    Observable<ApiResult<List<HomeLottiEntity>>> getHomeLotteries(@Query("snNo") String snNo);

    /**
     * 首页跑马灯数据
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/home/tips")
    Observable<ApiResult<List<HomeTipsEntity>>> getHomeTips(@Query("snNo") String snNo);

    /**
     * 首页底部设备信息
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/home/merchant/info")
    Observable<ApiResult<HomeDeviceInfoEntity>> getHomeDeviceInfo(@Query("snCode") String snCode);

    /**
     * 成为推广员
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @FormUrlEncoded
    @POST("api/home/promoter/login")
    Observable<ApiResult<Object>> bePromoter(@FieldMap Map<String, String> params);

    /**
     * 获取首页游戏列表
     */
    @GET("api/home/games")
    Observable<ApiResult<List<HomeGameEntity>>> getHomeGames(@QueryMap Map<String, String> params);

    /**
     * 首页H5链接
     */
    @GET("api/home/addresses")
    Observable<ApiResult<HomeH5AddrEntity>> getH5Addresses();
}
