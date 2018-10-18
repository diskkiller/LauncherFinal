package com.zhongti.huacailauncher.model.entity;

/**
 * Create by ShuHeMing on 18/6/9
 */
public class OderEntity {

    /**
     * orderId : 14
     * type : 1
     * url : weixin://wxpay/bizpayurl?pr=sKJzsV5
     */

    private long orderId;
    private int type;
    private String url;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
