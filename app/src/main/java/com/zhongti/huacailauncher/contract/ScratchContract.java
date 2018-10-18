package com.zhongti.huacailauncher.contract;

import android.app.Activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.zhongti.huacailauncher.model.entity.LotteryFunEntity;
import com.zhongti.huacailauncher.model.entity.LotteryRandomEntity;
import com.zhongti.huacailauncher.model.entity.LottiDetailEntity;
import com.zhongti.huacailauncher.model.entity.LottiListEntity;
import com.zhongti.huacailauncher.model.entity.LottiOrLeftEntity;
import com.zhongti.huacailauncher.model.entity.LottiOrListEntity;
import com.zhongti.huacailauncher.model.entity.OderEntity;
import com.zhongti.huacailauncher.ui.lottery.adapter.HadPayListAdapter;
import com.zhongti.huacailauncher.ui.lottery.adapter.LotterMsgAdapter;
import com.zhongti.huacailauncher.ui.lottery.adapter.LotteryFunListAdapter;
import com.zhongti.huacailauncher.ui.lottery.adapter.ScratchTicketListAdapter;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;


public interface ScratchContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {

        default void setTicketAdapter(ScratchTicketListAdapter adapterTicket) {
        }

        default void setHadPayAdapter(HadPayListAdapter adapterHadPay) {
        }

        default void onLoadLotteryDetailErr() {

        }

        default void updateLottiDetailData(LottiDetailEntity response) {
        }

        default void setLottiOrLeftData(LottiOrLeftEntity response) {
        }

        default void clearCheck() {
        }

        default void setLottiOrLeftProgress(int hadDj, int size) {
        }

//        default void setRestNum(String rest, int type) {
//        }

        default void onLockSuccess(BaseQuickAdapter entity, int pos, android.view.View view) {
        }

        default void onUnlockSuccess(boolean needSwitch, int mode) {
        }

        default void onUnLockSingleSuccess(BaseQuickAdapter adapter, int mode, android.view.View view) {
        }

//        default void isShowEmptyView(boolean isShow) {
//        }

        default void onUnlockBackSuccess() {
        }

        default void createOrFail() {
        }

        default void netWorkUnavailable() {
        }

        default void onLoadHadPayLeftErr(int i) {
        }

        default void onLoadHadPayListErr(int i) {
        }

        default void onLoadHadPayListSuccess(boolean isNull) {
        }

        default void onScratchDetailListErr(int i) {
        }

        default void onLoadScratchDetailListEmpty() {
        }

        default void onLoadScratchDetailListSuccess() {
        }

        default void onDjErr() {
        }

        default void onDjSuccess() {
        }

        default void setLotteryFunAdapter(LotteryFunListAdapter adapterFun) {
        }

        default Activity getActivity() {
            return null;
        }

       default void setLotteryMsgAdapter(LotterMsgAdapter adapterMsg){

       }

       default void onLotteryMsgRefreshed(List<String> response){

       }
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<LottiDetailEntity> getLottiDetail(long id);

        Observable<List<LottiListEntity>> getLottiListData(long lotteryId, int type);

        Observable<OderEntity> saveOr(Map<String, String> params);

        Observable<List<LottiOrListEntity>> getLottiOr(String token, long orId);

        Observable<LottiOrLeftEntity> getLottiOrLeft(String token, long orId);

        Observable<Object> lottiDj(String token, long id);

        Observable<Object> lockLotti(String token, String id, int type);

        Observable<Object> unlockLotti(String token, String id, int type);

        Observable<LotteryRandomEntity> getRandomLottery(String token, long id, int type);

        Observable<List<LotteryFunEntity>> getLotteryFunList(String snNo);

        Observable<List<String>> getLotteryMsg();
    }
}
