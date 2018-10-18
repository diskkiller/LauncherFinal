package com.zhongti.huacailauncher.model.entity;

/**
 * Create by ShuHeMing on 18/6/8
 */
public class HomeLottiEntity {
    /**
     * reward : 5000000
     * img : http://101.200.52.248/file/15240219831243467.JPEG
     * price : 50
     * name : 富贵有余
     * id : 35
     */

    private int reward;
    private String img;
    private String homeImg;
    private String price;
    private String name;
    private long id;
    private String sales;

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getHomeImg() {
        return homeImg;
    }

    public void setHomeImg(String homeImg) {
        this.homeImg = homeImg;
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

    public String getSales() {
        return sales;
    }

    public void setSales(String sales) {
        this.sales = sales;
    }
}
