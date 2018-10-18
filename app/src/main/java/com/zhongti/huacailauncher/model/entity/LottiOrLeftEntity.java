package com.zhongti.huacailauncher.model.entity;

/**
 * Create by ShuHeMing on 18/6/10
 */
public class LottiOrLeftEntity {


    /**
     * reward : 5000000
     * img : http://101.200.52.248/file/15240219831243467.JPEG
     * orderNo : PN2018060921095116697
     * price : 50
     * lotteryName : 富贵有余
     */

    private int reward;
    private String img;
    private String orderNo;
    private String price;
    private String lotteryName;

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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLotteryName() {
        return lotteryName;
    }

    public void setLotteryName(String lotteryName) {
        this.lotteryName = lotteryName;
    }
}
