package com.zhongti.huacailauncher.model.event;

/**
 * Create by ShuHeMing on 18/6/12
 */
public class EventDelLottiSuccess {
    private int position;

    public EventDelLottiSuccess(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
