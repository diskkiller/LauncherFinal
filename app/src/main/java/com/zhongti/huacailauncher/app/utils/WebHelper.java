package com.zhongti.huacailauncher.app.utils;

import android.annotation.SuppressLint;
import android.os.Build;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import timber.log.Timber;

/**
 * Create by ShuHeMing on 18/7/12
 */
public class WebHelper {
    public static void hookWebView() {
        int sdkInt = Build.VERSION.SDK_INT;
        try {
            @SuppressLint("PrivateApi") Class<?> factoryClass = Class.forName("android.webkit.WebViewFactory");
            Field field = factoryClass.getDeclaredField("sProviderInstance");
            field.setAccessible(true);
            Object sProviderInstance = field.get(null);
            if (sProviderInstance != null) {
                Timber.d("sProviderInstance isn't null");
                return;
            }
            Method getProviderClassMethod;
            if (sdkInt > 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
            } else if (sdkInt == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
            } else {
                Timber.d("Don't need to Hook WebView");
                return;
            }
            getProviderClassMethod.setAccessible(true);
            Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
            @SuppressLint("PrivateApi") Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
            Constructor<?> providerConstructor = providerClass.getConstructor(delegateClass);
            if (providerConstructor != null) {
                providerConstructor.setAccessible(true);
                Constructor<?> declaredConstructor = delegateClass.getDeclaredConstructor();
                declaredConstructor.setAccessible(true);
                sProviderInstance = providerConstructor.newInstance(declaredConstructor.newInstance());
                Timber.d("sProviderInstance:{}");
                field.set("sProviderInstance", sProviderInstance);
            }
            Timber.d("Hook done!");
        } catch (Throwable e) {
            Timber.d(e);
        }
    }
}
