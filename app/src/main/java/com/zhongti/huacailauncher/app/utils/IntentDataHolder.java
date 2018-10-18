package com.zhongti.huacailauncher.app.utils;

/**
 * Create by ShuHeMing on 18/7/26
 * 由于activity之间不能传递大量的数据,以下为解决方法;
 */
public class IntentDataHolder<T> {

    private static IntentDataHolder instance;

    public static IntentDataHolder getInstance() {
        if (instance == null) {
            synchronized (IntentDataHolder.class) {
                if (instance == null) {
                    instance = new IntentDataHolder();
                }
            }
        }
        return instance;
    }

    private T data;

    public T getHolderData() {
        return data;
    }

    public void setHolderData(T fromData) {
        this.data = fromData;
    }
}
