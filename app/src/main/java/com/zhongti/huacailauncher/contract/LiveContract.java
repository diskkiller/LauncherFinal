package com.zhongti.huacailauncher.contract;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.zhongti.huacailauncher.model.entity.CLiveCodeListEntity;
import com.zhongti.huacailauncher.model.entity.CheckLiveJBEntity;
import com.zhongti.huacailauncher.model.entity.CheckLiveUrlsEntity;
import com.zhongti.huacailauncher.ui.lottery.adapter.CheckTicketLiveAdapter;

import java.util.Map;

import io.reactivex.Observable;


public interface LiveContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        default void netWorkUnavailable() {
        }

        default void onLoadCheckDetailErr(boolean isFirst, boolean isPositive) {
        }

        default void onLoadDetailEmpty() {
        }

        default void setDetailData(String url, int total, boolean first, boolean isPositive, int isExist) {
        }

        default void setDetailListAdapter(CheckTicketLiveAdapter adapterDetail) {
        }

        default void onLoadListSuccess(CLiveCodeListEntity response) {
        }

        default void onCancelCTSuccess() {
        }

        default void onJuBaoSuccess() {
        }
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<CheckLiveUrlsEntity> getCheckUrls(Map<String, String> params);

        Observable<CLiveCodeListEntity> getLiveCheckDetail(String token);

        Observable<Object> exitCheckTicket(String token);

        Observable<CheckLiveJBEntity> checkTicketJB(Map<String, String> params);
    }
}
