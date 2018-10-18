package com.zhongti.huacailauncher.app.config.net.normal;

import android.support.annotation.NonNull;

import com.jess.arms.integration.cache.Cache;
import com.jess.arms.integration.cache.CacheType;

/**
 * 可根据当前项目的情况以及环境为框架某些部件提供自定义的缓存策略, 具有强大的扩展性
 * <p>
 * Created by shuheming on 2018/1/10 0010.
 */

public class CacheConfigImpl implements Cache.Factory {
    @NonNull
    @Override
    public Cache build(CacheType type) {
//        switch (type.getCacheTypeId()) {
//            case CacheType.EXTRAS_TYPE_ID:
//                return new LruCache(1000);
//            case CacheType.CACHE_SERVICE_CACHE_TYPE_ID:
//                return new Cache(type.calculateCacheSize(context));//自定义 Cache
//            default:
//                return new LruCache(200);
//        }
        return null;
    }
}
