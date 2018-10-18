package com.zhongti.huacailauncher.model.entity;

/**
 * Create by ShuHeMing on 18/7/2
 */
public class HomeGameEntity {

    /**
     * imgUrl : url1
     * playInfo : 抛羞羞的玩法
     * appid : ZTHC2132A135D352C3S4
     * name : 抛羞羞
     * gameUrl : http://h5.afocus.net/G_TF/Egg_New/index.html
     * id : 1
     * introduction : 抛羞羞的说明
     */

    private String imgUrl;
    private String playInfo;
    private String appid;
    private String name;
    private String gameUrl;
    private int id;
    private String introduction;
    private int type;

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getPlayInfo() {
        return playInfo;
    }

    public void setPlayInfo(String playInfo) {
        this.playInfo = playInfo;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGameUrl() {
        return gameUrl;
    }

    public void setGameUrl(String gameUrl) {
        this.gameUrl = gameUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
