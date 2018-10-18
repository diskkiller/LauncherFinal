package com.zhongti.huacailauncher.model.entity;

/**
 * Create by ShuHeMing on 18/6/20
 */
public class HomeGameTestEntity {
    private int resId;
    private String name;
    private String url;

    public HomeGameTestEntity(int resId, String name, String url) {
        this.resId = resId;
        this.name = name;
        this.url = url;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
