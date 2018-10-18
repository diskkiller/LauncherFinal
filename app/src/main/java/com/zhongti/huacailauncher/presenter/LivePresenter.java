package com.zhongti.huacailauncher.presenter;

import android.app.Activity;
import android.app.Application;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.http.exception.ApiException;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObserver;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.contract.LiveContract;
import com.zhongti.huacailauncher.model.entity.CLiveCodeListEntity;
import com.zhongti.huacailauncher.model.entity.CheckLiveJBEntity;
import com.zhongti.huacailauncher.model.entity.CheckLiveUrlsEntity;
import com.zhongti.huacailauncher.model.event.EventCancelCTSuccess;
import com.zhongti.huacailauncher.ui.lottery.adapter.CheckTicketLiveAdapter;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.widget.progress.ProgressReqFrameDialog;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;


@ActivityScope
public class LivePresenter extends BasePresenter<LiveContract.Model, LiveContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    private CheckTicketLiveAdapter adapterDetail;

    @Inject
    public LivePresenter(LiveContract.Model model, LiveContract.View rootView) {
        super(model, rootView);
    }

    public void getPlayUrl(String code) {
        if (!NetworkUtils.isConnected()) {
            mRootView.netWorkUnavailable();
            return;
        }
        Map<String, String> params = new HashMap<>();
        if (!TextUtils.isEmpty(UserUtils.getToken())) {
            params.put("token", UserUtils.getToken());
        }
        params.put("code", code);
        mModel.getCheckUrls(params)
                .retryWhen(new RetryWithDelay(3, 2))
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<CheckLiveUrlsEntity>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        mRootView.onLoadCheckDetailErr(true, true);
                    }

                    @Override
                    protected void onSuccess(CheckLiveUrlsEntity response) {
                        if (response == null || TextUtils.isEmpty(response.getPullUrlStmp())) {
                            mRootView.onLoadCheckDetailErr(true, true);
                            return;
                        }
                        getCheckDetails(response.getPullUrlStmp(), true, true);
                    }
                });
    }

    public void getCheckDetails(String url, boolean isFirst, boolean isPositive) {
        if (!NetworkUtils.isConnected()) {
            mRootView.netWorkUnavailable();
            return;
        }
        mModel.getLiveCheckDetail(UserUtils.getToken())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<CLiveCodeListEntity>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        if (mRootView != null) mRootView.onLoadCheckDetailErr(isFirst, isPositive);
                    }

                    @Override
                    protected void onSuccess(CLiveCodeListEntity response) {
                        setDetailData(url, response, isPositive, isFirst);
                    }
                });
    }

    private void setDetailData(String url, CLiveCodeListEntity response, boolean isPositive, boolean isFirst) {
        mRootView.setDetailData(url, response.getTotal(), isFirst, isPositive, response.getIsExist());
        if (adapterDetail == null) {
            adapterDetail = new CheckTicketLiveAdapter(R.layout.item_check_ticket_live, null);
            mRootView.setDetailListAdapter(adapterDetail);
        }
        if (response.getTicketList() == null || response.getTicketList().size() == 0) {
            mRootView.onLoadDetailEmpty();
        } else {
            mRootView.onLoadListSuccess(response);
        }
        adapterDetail.setNewData(dealData(response.getTicketList()));
    }

    private List<CLiveCodeListEntity.TicketListBean> dealData(List<CLiveCodeListEntity.TicketListBean> ticketList) {
        for (int i = 0; i < ticketList.size(); i++) {
            ticketList.get(i).setIndex(i + 1);
        }
        return ticketList;
    }

    public void exitCheckTicket(Activity activity, Button btn) {
        if (activity == null || activity.isFinishing()) return;
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.net_unavailable);
            return;
        }
        ProgressReqFrameDialog reqDialog = new ProgressReqFrameDialog(activity);
        btn.setClickable(false);
        mModel.exitCheckTicket(UserUtils.getToken())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<Object>(reqDialog) {
                    @Override
                    protected void onStart(Disposable d) {
                        reqDialog.setTextVisible(View.VISIBLE);
                        reqDialog.setContentText("正在取消...");
                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort(e.getMsg());
                        btn.setClickable(true);
                    }

                    @Override
                    protected void onSuccess(Object response) {
                        btn.setClickable(true);
                        ToastUtils.showShort("取消成功");
                        mRootView.onCancelCTSuccess();
                        EventBus.getDefault().post(new EventCancelCTSuccess(), EventBusTags.EVENT_CANCEL_CT_SUCCESS);
                    }
                });
    }

    public void userJb(String code, String content, Activity activity, Button btn) {
        if (activity == null || activity.isFinishing()) return;
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.net_unavailable);
            return;
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("content", content);
        params.put("token", UserUtils.getToken());
        ProgressReqFrameDialog reqDialog = new ProgressReqFrameDialog(activity);
        btn.setClickable(false);
        mModel.checkTicketJB(params)
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<CheckLiveJBEntity>(reqDialog) {
                    @Override
                    protected void onStart(Disposable d) {
                        reqDialog.setTextVisible(View.VISIBLE);
                        reqDialog.setContentText("正在提交...");
                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort(e.getMsg());
                    }

                    @Override
                    protected void onSuccess(CheckLiveJBEntity response) {
                        if (response != null && !TextUtils.isEmpty(response.getReport())) {
                            ToastUtils.showShort(response.getReport());
                        } else {
                            ToastUtils.showShort("举报成功!");
                        }
                        mRootView.onJuBaoSuccess();
                    }
                });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        this.mErrorHandler = null;
        this.mAppManager = null;
        this.mImageLoader = null;
        this.mApplication = null;
    }
}
