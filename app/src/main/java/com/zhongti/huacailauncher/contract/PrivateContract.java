package com.zhongti.huacailauncher.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.zhongti.huacailauncher.model.entity.GoldRecordEntity;
import com.zhongti.huacailauncher.model.entity.PrivateLottiListEntity;
import com.zhongti.huacailauncher.ui.personal.adapter.PrivateGoldAdapter;
import com.zhongti.huacailauncher.ui.personal.adapter.PrivateLotti1Adapter;
import com.zhongti.huacailauncher.ui.personal.adapter.PrivateLotti2Adapter;
import com.zhongti.huacailauncher.ui.personal.adapter.PrivateLotti3Adapter;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;


public interface PrivateContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        default void setLotti1Adapter(PrivateLotti1Adapter adapterLotti1) {
        }

        default void setLotti2Adapter(PrivateLotti2Adapter adapterLotti2) {
        }

        default void setLotti3Adapter(PrivateLotti3Adapter adapterLotti3) {
        }

        default void setGoldAdapter(PrivateGoldAdapter adapterGold) {
        }

        default void finishLoadLotti1(boolean isRefresh) {
        }

        default void refreshLotti1End(List<PrivateLottiListEntity> data) {
        }

        default void loadLotti1End() {
        }

        default void onRefreshLotti1Err(int i) {
        }

        default void onRefreshGoldErr(int i) {
        }

        default void finishLoadGold(boolean isRefresh) {
        }

        default void refreshGoldEnd(List<GoldRecordEntity> data) {
        }

        default void loadGoldEnd() {
        }
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<List<PrivateLottiListEntity>> getPrivateLottiList(String token, int type, int start, int count);

        Observable<List<GoldRecordEntity>> getGoldRecord(Map<String, String> params);
    }
}
