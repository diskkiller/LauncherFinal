package com.zhongti.huacailauncher.app.config.global;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.jess.arms.base.delegate.AppLifecycles;
import com.jess.arms.di.module.GlobalConfigModule;
import com.jess.arms.http.log.RequestInterceptor;
import com.jess.arms.integration.ConfigModule;
import com.jess.arms.utils.ArmsUtils;
import com.squareup.leakcanary.RefWatcher;
import com.zhongti.huacailauncher.BuildConfig;
import com.zhongti.huacailauncher.app.config.lifecycle.ActivityLifecycleCallbacksImpl;
import com.zhongti.huacailauncher.app.config.lifecycle.AppLifecyclesImpl;
import com.zhongti.huacailauncher.app.config.net.img.CustomGlideStrategyImpl;
import com.zhongti.huacailauncher.app.config.net.normal.GlobalHttpHandlerImpl;
import com.zhongti.huacailauncher.app.config.net.normal.GsonConfigImpl;
import com.zhongti.huacailauncher.app.config.net.normal.OkHttpConfigImpl;
import com.zhongti.huacailauncher.app.config.net.normal.ResponseErrorListenerImpl;
import com.zhongti.huacailauncher.app.config.net.normal.RetrofitConfigImpl;
import com.zhongti.huacailauncher.app.config.net.normal.RxCacheConfigImpl;
import com.zhongti.huacailauncher.app.utils.EnvSwitchUtils;
import com.zhongti.huacailauncher.model.api.Api;

import java.util.List;

/**
 * ================================================
 * app 的全局配置信息在此配置,需要将此实现类声明到 AndroidManifest 中
 * <p>
 * Created by shuheming on 2018/1/5
 * ================================================
 */
@Keep
public final class GlobalConfiguration implements ConfigModule {

    @Override
    public void applyOptions(Context context, GlobalConfigModule.Builder builder) {
        //这里可以自己自定义配置Okhttp的参数
        builder.baseurl(EnvSwitchUtils.isDebug ? Api.APP_DOMAIN_TEST : Api.APP_DOMAIN_RELEASE)
                .printHttpLogLevel(BuildConfig.LOG_DEBUG ? RequestInterceptor.Level.ALL : RequestInterceptor.Level.NONE) //http日志打印
                //.formatPrinter(null) // 格式化日志输出
                .imageLoaderStrategy(new CustomGlideStrategyImpl())//自定义图片的加载策略
                //.cacheFactory(new CacheConfigImpl())
                //.baseurl(new DynamicUrlConfig())
                .globalHttpHandler(new GlobalHttpHandlerImpl(context)) //类似全局请求的拦截器
                .responseErrorListener(new ResponseErrorListenerImpl()) //响应错误的统一处理(暂时不使用)
                .gsonConfiguration(new GsonConfigImpl())  //配置Gson
                .retrofitConfiguration(new RetrofitConfigImpl()) //配置Retrofit客户端
                .okhttpConfiguration(new OkHttpConfigImpl()) //配置okHttpClient
                .rxCacheConfiguration(new RxCacheConfigImpl()); //配置RxCache
    }

    @Override
    public void injectAppLifecycle(Context context, List<AppLifecycles> lifecycles) {
        // AppLifecycles 的所有方法都会在基类 Application 的对应的生命周期中被调用,所以在对应的方法中可以扩展一些自己需要的逻辑
        // 可以根据不同的逻辑添加多个实现类
        lifecycles.add(new AppLifecyclesImpl());
    }

    @Override
    public void injectActivityLifecycle(Context context, List<Application.ActivityLifecycleCallbacks> lifecycles) {
        // ActivityLifecycleCallbacks 的所有方法都会在 Activity (包括三方库) 的对应的生命周期中被调用,所以在对应的方法中可以扩展一些自己需要的逻辑
        // 可以根据不同的逻辑添加多个实现类
        lifecycles.add(new ActivityLifecycleCallbacksImpl());
    }

    @Override
    public void injectFragmentLifecycle(Context context, List<FragmentManager.FragmentLifecycleCallbacks> lifecycles) {
        lifecycles.add(new FragmentManager.FragmentLifecycleCallbacks() {

            @Override
            public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
                // 在配置变化的时候将这个 Fragment 保存下来,在 Activity 由于配置变化重建时重复利用已经创建的 Fragment。
                // https://developer.android.com/reference/android/app/Fragment.html?hl=zh-cn#setRetainInstance(boolean)
                // 如果在 XML 中使用 <Fragment/> 标签,的方式创建 Fragment 请务必在标签中加上 android:id 或者 android:tag 属性,否则 setRetainInstance(true) 无效
                // 在 Activity 中绑定少量的 Fragment 建议这样做,如果需要绑定较多的 Fragment 不建议设置此参数,如 ViewPager 需要展示较多 Fragment
                // f.setRetainInstance(true);
            }

            @Override
            public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
                if (f.getActivity()!= null){
                    RefWatcher watcher = (RefWatcher) ArmsUtils
                            .obtainAppComponentFromContext(f.getActivity())
                            .extras()
                            .get(RefWatcher.class.getName());
                    if (watcher!= null){
                        watcher.watch(f);
                    }
                }
            }
        });
    }

}
