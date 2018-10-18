package com.zhongti.huacailauncher.app.config.net.normal;

import android.content.Context;

import com.jess.arms.di.module.ClientModule;

import retrofit2.Retrofit;

/**
 * 这里可以自己自定义配置Retrofit的参数,甚至你可以替换系统配置好的okhttp对象
 * <p>
 * Created by shuheming on 2018/1/10 0010.
 */

public class RetrofitConfigImpl implements ClientModule.RetrofitConfiguration {

    @Override
    public void configRetrofit(Context context, Retrofit.Builder builder) {
//        retrofitBuilder.addConverterFactory(FastJsonConverterFactory.create());//比如使用fastjson替代gson
    }
}
