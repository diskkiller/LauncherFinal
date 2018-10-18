package com.zhongti.huacailauncher.app.config.net.normal;

import android.content.Context;

import com.jess.arms.di.module.ClientModule;
import com.zhongti.huacailauncher.app.utils.HttpsUtils;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.OkHttpClient;

/**
 * 配置OkHttpClient
 * <p>
 * Created by shuheming on 2018/1/10 0010.
 */

public class OkHttpConfigImpl implements ClientModule.OkhttpConfiguration {

    private static final int CONNECT_TIME_OUT = 10;//连接超时时长x秒
    private static final int READ_TIME_OUT = 10;//读数据超时时长x秒
    private static final int WRITE_TIME_OUT = 10;//写数据接超时时长x秒

    @Override
    public void configOkhttp(Context context, OkHttpClient.Builder builder) {
        //日志
//        if (BuildConfig.LOG_DEBUG) {
//            HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message -> Timber.w("okHttp:%s", message));
//            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//            builder.addInterceptor(logging);
//        }
        //支持 Https
//        setCertificates(builder);
        //设置超时时间
        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);
        //使用一行代码监听 Retrofit／Okhttp 上传下载进度监听,以及 Glide 加载进度监听
        ProgressManager.getInstance().with(builder);
        //让 Retrofit 同时支持多个 BaseUrl 以及动态改变 BaseUrl.
        RetrofitUrlManager.getInstance().with(builder);
    }

    /**
     * https的全局自签名证书
     */
    private void setCertificates(OkHttpClient.Builder builder, InputStream... certificates) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, certificates);
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
    }
}
