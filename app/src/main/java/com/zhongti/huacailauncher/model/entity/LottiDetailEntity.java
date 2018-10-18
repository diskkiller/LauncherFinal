package com.zhongti.huacailauncher.model.entity;

/**
 * Create by ShuHeMing on 18/6/8
 */
public class LottiDetailEntity {
    /**
     * reward : 5000000
     * standard : 12
     * backImg : http://101.200.52.248/file/15240219831243467.JPEG
     * playInfo : 666
     * frontImg : http://101.200.52.248/file/15240219831243467.JPEG
     * price : 50
     * name : 富贵有余
     * id : 35
     * sales : 0
     */

    private int reward;
    private int standard;
    private String backImg;
    private String playInfo;
    private String frontImg;
    private String img;
    private String price;
    private String name;
    private long id;
    private int sales;

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public int getStandard() {
        return standard;
    }

    public void setStandard(int standard) {
        this.standard = standard;
    }

    public String getBackImg() {
        return backImg;
    }

    public void setBackImg(String backImg) {
        this.backImg = backImg;
    }

    public String getPlayInfo() {
        return playInfo;
    }

    public void setPlayInfo(String playInfo) {
        this.playInfo = playInfo;
    }

    public String getFrontImg() {
        return frontImg;
    }

    public void setFrontImg(String frontImg) {
        this.frontImg = frontImg;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSales() {
        return sales;
    }

    public void setSales(int sales) {
        this.sales = sales;
    }

}
