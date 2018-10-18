package com.zhongti.huacailauncher.model.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Create by ShuHeMing on 18/6/9
 */
public class LottiOrListEntity implements Parcelable {

    public static final int UNOPEN = 1;
    public static final int OPENED = 2;

    /**
     * reward : 5000000
     * code : 356963251452258998
     * money : 50
     * name : 富贵有余
     * id : 81
     * status : 1
     */

    private int reward;
    private String code;
    private int money;
    private String name;
    private long id;
    private int status;

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.reward);
        dest.writeString(this.code);
        dest.writeInt(this.money);
        dest.writeString(this.name);
        dest.writeLong(this.id);
        dest.writeInt(this.status);
    }

    public LottiOrListEntity() {
    }

    protected LottiOrListEntity(Parcel in) {
        this.reward = in.readInt();
        this.code = in.readString();
        this.money = in.readInt();
        this.name = in.readString();
        this.id = in.readLong();
        this.status = in.readInt();
    }

    public static final Parcelable.Creator<LottiOrListEntity> CREATOR = new Parcelable.Creator<LottiOrListEntity>() {
        @Override
        public LottiOrListEntity createFromParcel(Parcel source) {
            return new LottiOrListEntity(source);
        }

        @Override
        public LottiOrListEntity[] newArray(int size) {
            return new LottiOrListEntity[size];
        }
    };
}
