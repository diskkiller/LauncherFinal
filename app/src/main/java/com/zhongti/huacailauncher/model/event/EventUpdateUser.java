package com.zhongti.huacailauncher.model.event;

import com.hcb.hcbsdk.service.msgBean.LoginReslut;

/**
 * 用户对象更新了
 * Create by ShuHeMing on 18/6/8
 */
public class EventUpdateUser {
    private LoginReslut.User user;

    public EventUpdateUser(LoginReslut.User user) {
        this.user = user;
    }

    public LoginReslut.User getUser() {
        return user;
    }
}
