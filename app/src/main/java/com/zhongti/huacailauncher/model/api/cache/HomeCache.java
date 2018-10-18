package com.zhongti.huacailauncher.model.api.cache;

import com.zhongti.huacailauncher.model.entity.HomeBannerEntity;
import com.zhongti.huacailauncher.model.entity.HomeDeviceInfoEntity;
import com.zhongti.huacailauncher.model.entity.HomeGameEntity;
import com.zhongti.huacailauncher.model.entity.HomeH5AddrEntity;
import com.zhongti.huacailauncher.model.entity.HomeLottiEntity;
import com.zhongti.huacailauncher.model.entity.HomeTipsEntity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.rx_cache2.DynamicKey;
import io.rx_cache2.EvictProvider;
import io.rx_cache2.LifeCache;
import io.rx_cache2.Reply;

/**
 * Create by ShuHeMing on 18/6/19
 */
public interface HomeCache {
    /**
     * 首页轮播缓存
     */
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<Reply<List<HomeBannerEntity>>> getHomeBanner(Observable<List<HomeBannerEntity>> homeData, DynamicKey dynamicKey, EvictProvider evictProvider);

    /**
     * 首页跑马灯数据
     */
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<Reply<List<HomeTipsEntity>>> getHomeTips(Observable<List<HomeTipsEntity>> homeData, DynamicKey dynamicKey, EvictProvider evictProvider);

    /**
     * 首页彩票数据
     */
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<Reply<List<HomeLottiEntity>>> getHomeLotteries(Observable<List<HomeLottiEntity>> homeData, DynamicKey dynamicKey, EvictProvider evictProvider);

    /**
     * 首页底部商户信息
     */
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<Reply<HomeDeviceInfoEntity>> getHomeDeviceInfo(Observable<HomeDeviceInfoEntity> homeData, DynamicKey dynamicKey, EvictProvider evictProvider);

    /**
     * 首页游戏列表
     */
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<Reply<List<HomeGameEntity>>> getHomeGames(Observable<List<HomeGameEntity>> homeData, DynamicKey dynamicKey, EvictProvider evictProvider);

    /**
     * H5地址
     */
    @LifeCache(duration = 1, timeUnit = TimeUnit.DAYS)
    Observable<Reply<HomeH5AddrEntity>> getH5Address(Observable<HomeH5AddrEntity> homeData, DynamicKey dynamicKey, EvictProvider evictProvider);
}
