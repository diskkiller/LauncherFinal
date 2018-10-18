package com.zhongti.huacailauncher.app.config.net.normal;

import com.jess.arms.http.BaseUrl;

import okhttp3.HttpUrl;


/**
 * 想支持多 BaseUrl,以及运行时动态切换任意一个 BaseUrl,请使用 https://github.com/JessYanCoding/RetrofitUrlManager
 * 如果 BaseUrl 在 App 启动时不能确定,需要请求服务器接口动态获取,请使用以下代码
 * 以下代码只是配置,还要使用 Okhttp (AppComponent中提供) 请求服务器获取到正确的 BaseUrl 后赋值给 GlobalConfiguration.sDomain
 * 切记整个过程必须在第一次调用 Retrofit 接口之前完成,如果已经调用过 Retrofit 接口,将不能动态切换 BaseUrl
 * <p>
 * Created by shuheming on 2018/1/10 0010.
 */

public class DynamicUrlConfig implements BaseUrl {

    //    public static String sDomain = Api.APP_DOMAIN_TEST;

    @Override
    public HttpUrl url() {
//        return HttpUrl.parse(sDomain);
        return null;
    }
}
