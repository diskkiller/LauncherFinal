package com.zhongti.huacailauncher.app.config.net.normal;

import android.content.Context;

import com.jess.arms.di.module.ClientModule;

import io.rx_cache2.internal.RxCache;

/**
 * 想自定义 RxCache 的缓存文件夹或者解析方式, 如改成 fastjson, 请 return rxCacheBuilder.persistence(cacheDirectory, new FastJsonSpeaker());
 * 否则请 return null
 * <p>
 * Created by shuheming on 2018/1/10 0010.
 */

public class RxCacheConfigImpl implements ClientModule.RxCacheConfiguration {
    @Override
    public RxCache configRxCache(Context context, RxCache.Builder builder) {
        builder.useExpiredDataIfLoaderNotAvailable(true);
        return null;
    }
}
