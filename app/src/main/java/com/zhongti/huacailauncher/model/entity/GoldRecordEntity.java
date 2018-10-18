package com.zhongti.huacailauncher.model.entity;

/**
 * Create by ShuHeMing on 18/7/6
 */
public class GoldRecordEntity {

    /**
     * createTime : 1530844135000
     * price : 50
     * count : 5000
     * name : 充值金豆
     * id : 654
     * type : 2
     */

    private long createTime;
    private int price;
    private int count;
    private String name;
    private String url;
    private int id;
    private int type;

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
