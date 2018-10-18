package com.zhongti.huacailauncher.model.event;

/**
 * Create by ShuHeMing on 18/6/10
 */
public class EventDjFinish {
    private int pos;

    private long orId;

    public EventDjFinish(int pos) {
        this.pos = pos;
    }

    public EventDjFinish(int pos, long orId) {
        this.pos = pos;
        this.orId = orId;
    }

    public long getOrId() {
        return orId;
    }

    public void setOrId(long orId) {
        this.orId = orId;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }
}
