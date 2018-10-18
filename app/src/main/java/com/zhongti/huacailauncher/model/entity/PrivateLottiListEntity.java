package com.zhongti.huacailauncher.model.entity;

/**
 * Create by ShuHeMing on 18/6/10
 */
public class PrivateLottiListEntity {

    /**
     * code : PN2018060921095116697
     * money : 1
     * createTime : 1528549792000
     * name : 富贵有余
     * count : 1
     * id : 15
     * cashed : 1
     * thumbnail, price
     */

    private String code;
    private int money;
    private long createTime;
    private String name;
    private int count;
    private long id;
    private int type;
    private int cashed;
    private String thumbnail;
    private String price;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCashed() {
        return cashed;
    }

    public void setCashed(int cashed) {
        this.cashed = cashed;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
