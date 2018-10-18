package com.zhongti.huacailauncher.model.entity;

/**
 * Create by ShuHeMing on 18/7/10
 */
public class RecordLiveUrlsEntity {

    /**
     * pushUrl : rtmp://video-center.alivecdn.com/ZthcLive/ZthcLuPiaoLive?vhost=vid.zthuacai.com
     * pullUrlFlv : http://vid.zthuacai.com/ZthcLive/ZthcLuPiaoLive.flv
     * pullUrlStmp : rtmp://vid.zthuacai.com/ZthcLive/ZthcLuPiaoLive
     * pullUrlM3u8 : http://vid.zthuacai.com/ZthcLive/ZthcLuPiaoLive.m3u8
     */

    private String pushUrl;
    private String pullUrlFlv;
    private String pullUrlStmp;
    private String pullUrlM3u8;

    public String getPushUrl() {
        return pushUrl;
    }

    public void setPushUrl(String pushUrl) {
        this.pushUrl = pushUrl;
    }

    public String getPullUrlFlv() {
        return pullUrlFlv;
    }

    public void setPullUrlFlv(String pullUrlFlv) {
        this.pullUrlFlv = pullUrlFlv;
    }

    public String getPullUrlStmp() {
        return pullUrlStmp;
    }

    public void setPullUrlStmp(String pullUrlStmp) {
        this.pullUrlStmp = pullUrlStmp;
    }

    public String getPullUrlM3u8() {
        return pullUrlM3u8;
    }

    public void setPullUrlM3u8(String pullUrlM3u8) {
        this.pullUrlM3u8 = pullUrlM3u8;
    }
}
