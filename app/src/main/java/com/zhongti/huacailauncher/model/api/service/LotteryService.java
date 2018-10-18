package com.zhongti.huacailauncher.model.api.service;

import com.zhongti.huacailauncher.app.http.model.ApiResult;
import com.zhongti.huacailauncher.model.api.Api;
import com.zhongti.huacailauncher.model.entity.LotteryFunEntity;
import com.zhongti.huacailauncher.model.entity.LotteryRandomEntity;
import com.zhongti.huacailauncher.model.entity.LottiDetailEntity;
import com.zhongti.huacailauncher.model.entity.LottiListEntity;
import com.zhongti.huacailauncher.model.entity.LottiOrLeftEntity;
import com.zhongti.huacailauncher.model.entity.LottiOrListEntity;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 首页接口
 * Create by ShuHeMing on 18/6/8
 */
public interface LotteryService {

    /**
     * 彩种详情
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/home/lottery")
    Observable<ApiResult<LottiDetailEntity>> getLottiDetail(@Query("id") long id);

    /**
     * 获取彩票列表
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/home/stock")
    Observable<ApiResult<List<LottiListEntity>>> getLottiList(@Query("lotteryId") long id, @Query("type") int type);

    /**
     * @param token   用户token
     * @param orderId 订单号
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/mbr/user/order/detail")
    Observable<ApiResult<List<LottiOrListEntity>>> getUserLottiOrByOrId(@Query("token") String token, @Query("orderId") long orderId);

    /**
     * 兑奖
     *
     * @param token 用户Token
     * @param id    彩票id
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @FormUrlEncoded
    @POST("api/mbr/user/lottery/cash")
    Observable<ApiResult<Object>> lottiDJ(@Field("token") String token, @Field("id") long id);

    /**
     * 根据订单号查询订单详情
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/common/order/detail/shm")
    Observable<ApiResult<LottiOrLeftEntity>> getLottiOrDetailLeft(@Query("token") String token, @Query("orderId") long orderId);

    /**
     * 锁定票(包)
     *
     * @param type 1:整包 2:单张
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @FormUrlEncoded
    @POST("api/common/ticket/lock")
    Observable<ApiResult<Object>> lockLotti(@Field("token") String token, @Field("id") String id, @Field("type") int type);

    /**
     * 锁定票(包)
     *
     * @param type 1:整包 2:单张
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @FormUrlEncoded
    @POST("api/common/ticket/unlock")
    Observable<ApiResult<Object>> unlockLotti(@Field("token") String token, @Field("id") String id, @Field("type") int type);

    /**
     * 好运来一张/包
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/home/luck/one")
    Observable<ApiResult<LotteryRandomEntity>> getRandomLottery(@Query("token") String token, @Query("lotteryId") long lotteryId, @Query("type") int type);

    /**
     * 开始直播验票---加入队列
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @FormUrlEncoded
    @POST("liveApi/userToExamineTicket")
    Observable<ApiResult<Object>> startCheckTicket(@Field("code") String code, @Field("token") String token);

    /**
     * 获取彩票趣味玩法列表
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/activity/list")
    Observable<ApiResult<List<LotteryFunEntity>>> getLotteryFunList(@Query("snNo") String snNo);

    /**
     * 获取彩票右下方中奖消息列表
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/home/messages")
    Observable<ApiResult<List<String>>> getLotteryMsg();
}
