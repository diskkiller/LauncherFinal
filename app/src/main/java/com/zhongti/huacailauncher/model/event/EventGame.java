package com.zhongti.huacailauncher.model.event;

/**
 * 用户对象更新了
 * Create by ShuHeMing on 18/6/8
 */
public class EventGame {
    /**
     * 充值
     */
    public static final int CHARGE = 1;
    /**
     * 消耗
     */
    public static final int USE = 2;
    /**
     * 增加/减少
     */
    public static final int ADD_LESS = 3;
    private int witch;

    public EventGame(int witch) {
        this.witch = witch;
    }

    public int getWitch() {
        return witch;
    }

    public void setWitch(int witch) {
        this.witch = witch;
    }
}
