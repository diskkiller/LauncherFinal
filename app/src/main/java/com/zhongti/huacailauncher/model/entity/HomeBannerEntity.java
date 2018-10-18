package com.zhongti.huacailauncher.model.entity;

/**
 * Create by ShuHeMing on 18/6/8
 */
public class HomeBannerEntity {
    private long id;
    private String url;
    private int type;
    private String redirectUrl;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
}
