package com.zhongti.huacailauncher.app.config.net.normal;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.jess.arms.di.module.AppModule;

/**
 * Gson配置
 * <p>
 * Created by shuheming on 2018/1/10 0010.
 */

public class GsonConfigImpl implements AppModule.GsonConfiguration {
    @Override
    public void configGson(Context context, GsonBuilder builder) {
        builder.serializeNulls()//支持序列化null的参数
                .enableComplexMapKeySerialization();//支持将序列化key为object的map,默认只能序列化key为string的map
    }
}
