/**
 * Copyright 2017 JessYan
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zhongti.huacailauncher.model.api;

/**
 * ================================================
 * 存放一些与 API 有关的东西,如请求地址,请求码等
 * <p>
 * Created by shuheming on 2018/1/7
 * ================================================
 */
public interface Api {
    // TODO: 2018/3/9 0009 服务器地址(切换正式/测试)
    /**
     * 服务器地址
     */
//    String APP_DOMAIN_TEST = "http://dev.hcb66.com:7300/"; //测试
//    String APP_DOMAIN_TEST = "http://m.jinshw.cn/"; //测试
    String APP_DOMAIN_TEST = "http://mtest.zthuacai.com/"; //测试
    String APP_DOMAIN_RELEASE = "https://m.zthuacai.com/"; //正式
//    String APP_DOMAIN_RELEASE = "http://m.jinshw.cn/"; //正式
    // mtest.zthuacai.com

    //服务器错误码(服务器定义)
    int SUCCESS = 1;

    //自定义
    int SERVER_NOT_DATA = 80000;

    /**
     * 标识Launcher API
     */
    String APP_DOMAIN_HOST_NAME = "hcb-launcher";
}
