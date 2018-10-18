package com.zhongti.huacailauncher.contract;

import android.app.Activity;

import com.jess.arms.mvp.IModel;
import com.jess.arms.mvp.IView;
import com.zhongti.huacailauncher.model.entity.HomeBannerEntity;
import com.zhongti.huacailauncher.model.entity.HomeDeviceInfoEntity;
import com.zhongti.huacailauncher.model.entity.HomeGameEntity;
import com.zhongti.huacailauncher.model.entity.HomeH5AddrEntity;
import com.zhongti.huacailauncher.model.entity.HomeLottiEntity;
import com.zhongti.huacailauncher.ui.home.adapter.HomeGameAdapter;
import com.zhongti.huacailauncher.ui.home.adapter.HomeLotteryAdapter;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;


public interface MainContract {
    //对于经常使用的关于UI的方法可以定义到IView中,如显示隐藏进度条,和显示文字消息
    interface View extends IView {
        //        RxPermissions getRxPermissions();

//        void showPermissionSetting();

        Activity getActivity();

        default void setLottiListAdapter(HomeLotteryAdapter adapterLotti) {
        }

        default void setGameListAdapter(HomeGameAdapter adapterGame) {
        }

//        void setFistLottiData(HomeLottiEntity homeLottiEntity);

        default void setBottomData(HomeDeviceInfoEntity response) {
        }

        //
        default void setBannerData(List<HomeBannerEntity> response) {
        }

//        void onRefreshMarquee(List<HomeTipsEntity> response);

        default void openDeviceBindPage() {
        }

        default void bannerTurn2H5Page(String redirectUrl) {
        }

        default void bannerTurn2LotteryPage(String lotteryId) {
        }

        default void bannerTurn2GamePage(String gameId) {
        }

//        void gameDataIsNull(boolean isNull);
    }

    //Model层定义接口,外部只需关心Model返回的数据,无需关心内部细节,即是否使用缓存
    interface Model extends IModel {
        Observable<List<HomeBannerEntity>> getHomeBannerData(String snNo, boolean isUpdate);

        Observable<List<HomeLottiEntity>> getHomeLottiData(String snNo, boolean isUpdate);

        //        Observable<List<HomeTipsEntity>> getHomeTips(String snNo, boolean isUpdate);

        Observable<Integer> checkIsBind(String snNo);

        Observable<HomeDeviceInfoEntity> getHomeDeviceInfo(String snNo, boolean isUpdate);

        Observable<List<HomeGameEntity>> getHomeGames(Map<String, String> params, boolean isUpdate);

        Observable<HomeH5AddrEntity> getH5Address(boolean isUpdate);
    }
}
