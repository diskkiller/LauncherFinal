package com.zhongti.huacailauncher.model.api.service;

import com.hcb.hcbsdk.service.msgBean.LoginReslut;
import com.zhongti.huacailauncher.app.http.model.ApiResult;
import com.zhongti.huacailauncher.model.api.Api;
import com.zhongti.huacailauncher.model.entity.CheckTicketEntity;
import com.zhongti.huacailauncher.model.entity.GoldRecordEntity;
import com.zhongti.huacailauncher.model.entity.OderEntity;
import com.zhongti.huacailauncher.model.entity.PrivateLottiListEntity;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Create by ShuHeMing on 18/6/8
 */
public interface UserService {

    /**
     * 设备绑定
     *
     * @param snNo   设备号
     * @param mobile 商家手机号
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @FormUrlEncoded
    @POST("api/home/equipment/activate")
    Observable<ApiResult<LoginReslut.User>> deviceActive(@Field("snNo") String snNo, @Field("mobile") String mobile);

    /**
     * 检查设备是否绑定
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/home/equipment/status")
    Observable<ApiResult<Integer>> checkIsActive(@Query("snNo") String snNo);

    /**
     * 微信下单
     *
     * @param params token packages(购买彩票包包号(多选时用,分隔)) singles(购买彩票单张id(多选时用,分隔))
     *               amount(所需支付金额(单位：元))  snNo(设备号) lotteryId(彩种id)
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @FormUrlEncoded
    @POST("api/common/wechat/order")
    Observable<ApiResult<OderEntity>> saveOr(@FieldMap Map<String, String> params);

    /**
     * 查询用户订单信息
     *
     * @param type  类型 1:兑奖订单 2:验票订单(待实现) 3:邮寄订单(待实现)
     * @param start 从1开始
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/mbr/user/lottery/order")
    Observable<ApiResult<List<PrivateLottiListEntity>>> getPrivateLottiList(@Query("token") String token, @Query("type") int type, @Query("start") int start, @Query("count") int count);

    /**
     * 删除一条彩票订单
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @DELETE("api/mbr/user/lottery/order")
    Observable<ApiResult<Object>> delLottiItem(@Query("token") String token, @Query("id") long id);

    /**
     * 查询彩票是否验证过
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @FormUrlEncoded
    @POST("liveApi/ticketIsExamine")
    Observable<ApiResult<CheckTicketEntity>> checkTicket(@FieldMap Map<String, String> params);

    /**
     * 获取金豆记录
     */
    @Headers({"Domain-Name: " + Api.APP_DOMAIN_HOST_NAME})
    @GET("api/mbr/user/lottery/gold/coin")
    Observable<ApiResult<List<GoldRecordEntity>>> getGoldRecord(@QueryMap Map<String, String> params);
}
