package com.zhongti.huacailauncher.presenter;

import android.app.Activity;
import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jess.arms.di.scope.ActivityScope;
import com.jess.arms.http.imageloader.ImageLoader;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.integration.AppManager;
import com.jess.arms.mvp.BasePresenter;
import com.jess.arms.utils.RxLifecycleUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.config.net.img.CustomImgConfigImpl;
import com.zhongti.huacailauncher.app.config.net.img.RoundD;
import com.zhongti.huacailauncher.app.http.exception.ApiException;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObserver;
import com.zhongti.huacailauncher.app.utils.IntentDataHolder;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.contract.ScratchContract;
import com.zhongti.huacailauncher.model.entity.LotteryFunEntity;
import com.zhongti.huacailauncher.model.entity.LotteryRandomEntity;
import com.zhongti.huacailauncher.model.entity.LottiDetailEntity;
import com.zhongti.huacailauncher.model.entity.LottiListEntity;
import com.zhongti.huacailauncher.model.entity.LottiOrLeftEntity;
import com.zhongti.huacailauncher.model.entity.LottiOrListEntity;
import com.zhongti.huacailauncher.model.entity.OderEntity;
import com.zhongti.huacailauncher.model.event.EventDjFinish;
import com.zhongti.huacailauncher.ui.lottery.adapter.HadPayListAdapter;
import com.zhongti.huacailauncher.ui.lottery.adapter.LotterMsgAdapter;
import com.zhongti.huacailauncher.ui.lottery.adapter.LotteryFunListAdapter;
import com.zhongti.huacailauncher.ui.lottery.adapter.ScratchTicketListAdapter;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.StringUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhongti.huacailauncher.widget.progress.ProgressReqDialog;
import com.zhongti.huacailauncher.widget.progress.ProgressReqFrameDialog;
import com.zhongti.huacailauncher.widget.progress.RandomTicketDialog;

import org.simple.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import me.jessyan.rxerrorhandler.core.RxErrorHandler;
import me.jessyan.rxerrorhandler.handler.RetryWithDelay;
import timber.log.Timber;


@ActivityScope
public class LotteryPresenter extends BasePresenter<ScratchContract.Model, ScratchContract.View> {
    @Inject
    RxErrorHandler mErrorHandler;
    @Inject
    Application mApplication;
    @Inject
    ImageLoader mImageLoader;
    @Inject
    AppManager mAppManager;
    private ScratchTicketListAdapter adapterTicket;
    private HadPayListAdapter adapterHadPay;

    @Inject
    public LotteryPresenter(ScratchContract.Model model, ScratchContract.View rootView) {
        super(model, rootView);
    }

    /**
     * 请求彩票详情页数据
     */
    public void reqLottiDetail(long id, boolean isFirst) {
        if (!NetworkUtils.isConnected() && isFirst) {
            mRootView.netWorkUnavailable();
            return;
        }
        mModel.getLottiDetail(id)
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<LottiDetailEntity>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        if (isFirst) mRootView.onLoadLotteryDetailErr();
                    }

                    @Override
                    protected void onSuccess(LottiDetailEntity response) {
                        if (response == null) {
                            if (isFirst) mRootView.onLoadLotteryDetailErr();
                            return;
                        }
                        mRootView.updateLottiDetailData(response);
                        if (isFirst) reqTicketListData(id, 2);
                    }
                });
    }


    private Disposable taskTicket;

    /**
     * 请求彩票列表
     *
     * @param lottiId     彩种id
     * @param currentMode 当前模式
     */
    public void reqTicketListData(long lottiId, int currentMode) {
        if (!NetworkUtils.isConnected()) {
            mRootView.onScratchDetailListErr(1);
//            mRootView.setRestNum("0", type);
            return;
        }
        //取消上次任务
        if (taskTicket != null && !taskTicket.isDisposed()) {
            taskTicket.dispose();
        }
        //清空数据
        if (adapterTicket != null) {
            adapterTicket.getData().clear();
            adapterTicket.notifyDataSetChanged();
        }
//        if (isAll) {
//            type = currentMode == 1 ? 2 : 1;
//        }
        //清空选中的状态
        mRootView.clearCheck();
        mModel.getLottiListData(lottiId, currentMode)
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<List<LottiListEntity>>() {
                    @Override
                    protected void onStart(Disposable d) {
                        taskTicket = d;
                    }

                    @Override
                    protected void onError(ApiException e) {
//                        mRootView.setRestNum("0", finalType);
                        mRootView.onScratchDetailListErr(2);
                    }

                    @Override
                    protected void onSuccess(List<LottiListEntity> response) {
                        setTicketAdapterAndData(response, currentMode);
                    }
                });
    }

    private int pkgGoodsNum;
    private int singleGoodsNum;

    /**
     * 设置彩票列表数据
     */
    private void setTicketAdapterAndData(List<LottiListEntity> data, int currentMode) {
        if (adapterTicket == null) {
            adapterTicket = new ScratchTicketListAdapter(R.layout.item_scratch_detail_tickets,
                    null);
            mRootView.setTicketAdapter(adapterTicket);
        }
        //设置库存
        int rest = 0;
        if (data != null && data.size() != 0) {
            rest = data.size();
        }
        setRestNum(rest, currentMode);
//        mRootView.setRestNum(rest, type);
        //刷新列表数据
        if (currentMode == 0 || currentMode == 1) {
//            currentMode = 1;
            adapterTicket.setMode(1);
        } else {
            adapterTicket.setMode(2);
        }
        //设置列表数据
//        if (currentMode == type) {
        adapterTicket.setNewData(data);
        mRootView.onLoadScratchDetailListSuccess();
//        }
        //判断是否加载数据为空
        if ((data == null || data.size() == 0)) {
            mRootView.onLoadScratchDetailListEmpty();
        }
//        if (isAll) {
//            reqTicketListData(lottiId, currentMode, currentMode, false);
//        }

    }

    private void setRestNum(int rest, int type) {
        switch (type) {
            case 1:
                pkgGoodsNum = rest;
                break;
            case 2:
                singleGoodsNum = rest;
                break;
        }
    }

    public int getPkgGoodsNum() {
        return pkgGoodsNum;
    }


    public int getSingleGoodsNum() {
        return singleGoodsNum;
    }

    private long orDerId;

    /**
     * 微信下单
     */
    public void saveOr(int mode, long lotteryId, String amount, Map<Integer, String> checkedTemp,
                       Button btnPay, Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort("未连接网络,请检查您的网络设置");
            return;
        }
        btnPay.setClickable(false);
        ProgressReqFrameDialog reqDialog = new ProgressReqFrameDialog(activity);
        Map<String, String> params = new HashMap<>();
        params.put("token", UserUtils.getToken());
        StringBuilder sb = new StringBuilder();
        if (mode == 1) {
            for (Map.Entry<Integer, String> entry : checkedTemp.entrySet()) {
                LottiListEntity lottiListEntity = adapterTicket.getData().get(entry.getKey());
                sb.append(lottiListEntity.getCode()).append(",");
            }
            String tickets = sb.toString().substring(0, sb.toString().length() - 1);
            params.put("packages", tickets);
        } else if (mode == 2) {
            for (Map.Entry<Integer, String> entry : checkedTemp.entrySet()) {
                LottiListEntity lottiListEntity = adapterTicket.getData().get(entry.getKey());
                sb.append(lottiListEntity.getId()).append(",");
            }
            String tickets = sb.toString().substring(0, sb.toString().length() - 1);
            params.put("singles", tickets);
        }
        params.put("amount", amount);
        params.put("snNo", UserUtils.getSNNum());
        params.put("lotteryId", String.valueOf(lotteryId));
        mModel.saveOr(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .doFinally(() -> {
                    Timber.i("saveOr...doFinally");
                })
                .subscribe(new HttpRxObserver<OderEntity>(reqDialog) {
                    @Override
                    protected void onStart(Disposable d) {
                        reqDialog.setTextVisible(View.VISIBLE);
                        reqDialog.setContentText("正在生成订单...");
                    }

                    @Override
                    protected void onError(ApiException e) {
                        btnPay.setClickable(true);
                        ToastUtils.showShort(e.getMsg());
                        mRootView.createOrFail();
                    }

                    @Override
                    protected void onSuccess(OderEntity response) {
                        btnPay.setClickable(true);
                        orDerId = response.getOrderId();
                        UserUtils.openPayPage(response.getOrderId(), response.getUrl());
                    }
                });

    }

    /**
     * 锁定单张票
     */
    public void lockLotti(Activity activity, String id, int currentMode, BaseQuickAdapter adapter,
                          int pos, View view) {
        if (activity == null || activity.isFinishing()) return;
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.net_unavailable);
            return;
        }
        view.setClickable(false);
        ProgressReqDialog progressReqDialog = new ProgressReqDialog(activity);
        mModel.lockLotti(UserUtils.getToken(), id, currentMode)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<Object>(progressReqDialog) {
                    @Override
                    protected void onStart(Disposable d) {
                        progressReqDialog.setTextVisible(View.VISIBLE);
                        progressReqDialog.setContentText("正在锁定...");
                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort(e.getMsg());
                        mRootView.onLockSuccess(null, 0, null);
                    }

                    @Override
                    protected void onSuccess(Object response) {
                        mRootView.onLockSuccess(adapter, pos, view);
                    }
                });
    }

    /**
     * 解锁 切换/倒计时结束
     */
    public void unLockLotti(Map<Integer, String> checkedTemp, int mode, boolean needSwitch,
                            Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        StringBuilder sb = new StringBuilder();
        if (mode == 1) {
            for (Map.Entry<Integer, String> entry : checkedTemp.entrySet()) {
                LottiListEntity lottiListEntity = adapterTicket.getData().get(entry.getKey());
                sb.append(lottiListEntity.getCode()).append(",");
            }
        } else if (mode == 2) {
            for (Map.Entry<Integer, String> entry : checkedTemp.entrySet()) {
                LottiListEntity lottiListEntity = adapterTicket.getData().get(entry.getKey());
                sb.append(lottiListEntity.getId()).append(",");
            }
        }
        String id = sb.toString().substring(0, sb.toString().length() - 1);
        ProgressReqDialog progressReqDialog = new ProgressReqDialog(activity);
        mModel.unlockLotti(UserUtils.getToken(), id, mode)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<Object>(progressReqDialog) {
                    @Override
                    protected void onStart(Disposable d) {
                        progressReqDialog.setTextVisible(View.VISIBLE);
                        progressReqDialog.setContentText("正在清空已选票...");
                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort(e.getMsg());
                        mRootView.onUnlockSuccess(needSwitch, mode);
                    }

                    @Override
                    protected void onSuccess(Object response) {
                        mRootView.onUnlockSuccess(needSwitch, mode);
                    }
                });
    }

    /**
     * 关闭页面的时候解锁票
     */
    public void unLockLottiBack(Map<Integer, String> checkedTemp, int mode, Activity activity) {
        if (activity == null || activity.isFinishing()) return;
        StringBuilder sb = new StringBuilder();
        if (mode == 1) {
            for (Map.Entry<Integer, String> entry : checkedTemp.entrySet()) {
                LottiListEntity lottiListEntity = adapterTicket.getData().get(entry.getKey());
                sb.append(lottiListEntity.getCode()).append(",");
            }
        } else if (mode == 2) {
            for (Map.Entry<Integer, String> entry : checkedTemp.entrySet()) {
                LottiListEntity lottiListEntity = adapterTicket.getData().get(entry.getKey());
                sb.append(lottiListEntity.getId()).append(",");
            }
        }
        String id = sb.toString().substring(0, sb.toString().length() - 1);
        ProgressReqDialog progressReqDialog = new ProgressReqDialog(activity);
        mModel.unlockLotti(UserUtils.getToken(), id, mode)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<Object>(progressReqDialog) {

                    @Override
                    protected void onStart(Disposable d) {
                        progressReqDialog.setTextVisible(View.VISIBLE);
                        progressReqDialog.setContentText("正在清空已选,请稍后...");
                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort("无法清空已选票,请再次点击关闭按钮");
                    }

                    @Override
                    protected void onSuccess(Object response) {
                        mRootView.onUnlockBackSuccess();
                    }
                });
    }

    /**
     * 解锁单张
     */
    public void unLockLotti(Activity activity, String id, int currentMode, BaseQuickAdapter adapter,
                            int pos, View view) {
        if (activity == null || activity.isFinishing()) return;
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.net_unavailable);
            return;
        }
        view.setClickable(false);
        ProgressReqDialog progressReqDialog = new ProgressReqDialog(activity);
        mModel.unlockLotti(UserUtils.getToken(), id, currentMode)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<Object>(progressReqDialog) {
                    @Override
                    protected void onStart(Disposable d) {
                        progressReqDialog.setTextVisible(View.VISIBLE);
                        progressReqDialog.setContentText("正在解锁...");
                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort(e.getMsg());
                    }

                    @Override
                    protected void onSuccess(Object response) {
                        mRootView.onUnLockSingleSuccess(adapter, pos, view);
                    }
                });
    }

    public long getOrId() {
        return this.orDerId;
    }

    public void setOrId(long orId) {
        this.orDerId = orId;
    }

    private String hadPayPrice;

    public void reqHadPayListLeft(long orId) {
        if (!NetworkUtils.isConnected()) {
            mRootView.onLoadHadPayLeftErr(1);
            return;
        }
        mModel.getLottiOrLeft(UserUtils.getToken(), orId)
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<LottiOrLeftEntity>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        mRootView.onLoadHadPayLeftErr(2);
                    }

                    @Override
                    protected void onSuccess(LottiOrLeftEntity response) {
                        hadPayPrice = response.getPrice();
                        mRootView.setLottiOrLeftData(response);
                        reqHadPayList(orId);
                    }
                });

    }

    public String getHadPayPrice() {
        return this.hadPayPrice;
    }


//    private ArrayList<LottiOrListEntity> dataHadList;


    /**
     * 查询用户彩票订单列表
     */
    public void reqHadPayList(long orId) {
        if (!NetworkUtils.isConnected()) {
            mRootView.onLoadHadPayListErr(1);
            return;
        }
        mModel.getLottiOr(UserUtils.getToken(), orId)
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<List<LottiOrListEntity>>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        mRootView.onLoadHadPayListErr(2);
                    }

                    @Override
                    protected void onSuccess(List<LottiOrListEntity> response) {
                        setHadPayListAdapter(response);
                        IntentDataHolder.getInstance().setHolderData(response);
                    }
                });

    }

    private int hadDj;

    private void setHadPayListAdapter(List<LottiOrListEntity> data) {
        if (adapterHadPay == null) {
            adapterHadPay = new HadPayListAdapter(null);
            mRootView.setHadPayAdapter(adapterHadPay);
        }
        adapterHadPay.setNewData(data);
        mRootView.onLoadHadPayListSuccess(data == null || data.size() == 0);
        //设置刮奖进度
        if (data != null && data.size() != 0) {
            for (LottiOrListEntity entity : data) {
                if (entity.getStatus() == 2) {
                    hadDj++;
                }
            }
            mRootView.setLottiOrLeftProgress(hadDj, data.size());
        }
    }

    /**
     * 左边刮奖进度++
     */
    public void onDjPlusPlus() {
        hadDj++;
        if (hadDj > adapterHadPay.getData().size()) {
            hadDj = adapterHadPay.getData().size();
        }
        mRootView.setLottiOrLeftProgress(hadDj, adapterHadPay.getData().size());
    }

//    public ArrayList<LottiOrListEntity> getHadPayList() {
//        return this.dataHadList;
//    }

    /**
     * 点击二维码兑奖
     *
     * @param id 彩票id
     */
    public void lottiDj(long id, int pos) {
        mModel.lottiDj(UserUtils.getToken(), id)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<Object>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        mRootView.onDjErr();
                    }

                    @Override
                    protected void onSuccess(Object response) {
                        Timber.i("刮奖成功:%s", id);
                        EventBus.getDefault().post(new EventDjFinish(pos), EventBusTags.EVENT_DJ_SUCCESS);
                        mRootView.onDjSuccess();
                    }
                });
    }

    /**
     * 兑奖成功
     */
    public void onDjSuccess(int pos) {
        if (pos > adapterHadPay.getData().size() - 1) return;
        adapterHadPay.getData().get(pos).setStatus(2);
        adapterHadPay.notifyDataSetChanged();
    }

    /**
     * 加载普通的图片
     */
    public void loadImg(String url, ImageView iv) {
        if (iv == null) return;
        if (!TextUtils.isEmpty(url)) {
//            mImageLoader.loadImage(Utils.getApp(),
//                    CustomImgConfigImpl
//                            .builder()
//                            .url(url)
//                            .placeholder(R.drawable.img_place_lotti_pw)
//                            .errorPic(R.drawable.img_place_lotti_pw)
//                            .imageView(iv)
//                            .build()
//            );
            GlideArms.with(Utils.getApp())
                    .asBitmap()
                    .load(url)
                    .error(R.drawable.img_place_lotti_pw)
                    .placeholder(R.drawable.img_place_lotti_pw)
                    .fallback(R.drawable.img_place_lotti_pw)
                    .into(iv);
        } else {
            Timber.e("图片加载err: %s", url);
            iv.setImageResource(R.drawable.img_place_lotti_pw);
        }
    }

    /**
     * 加载圆角图片
     */
    public void loadRadiusImg(String url, ImageView iv, int radius) {
        if (iv == null) return;
        if (!TextUtils.isEmpty(url)) {
            mImageLoader.loadImage(Utils.getApp(),
                    CustomImgConfigImpl
                            .builder()
                            .url(url)
                            .loadShape(CustomImgConfigImpl.ROUNDED)
                            .rounded(new RoundD(radius, radius, radius, radius))
                            .placeholder(R.drawable.img_place_square)
                            .errorPic(R.drawable.img_place_square)
                            .imageView(iv)
                            .build()
            );
        } else {
            Timber.e("图片加载err: %s", url);
            // TODO: 18/6/5 错误占位图片
            iv.setImageResource(R.drawable.img_place_square);
        }
    }

    /**
     * 随机来一张/包
     *
     * @param id   彩种id
     * @param type 包/张  1/2
     */
    public void getRandomTicket(Activity activity, long id, int type, int amount) {
        if (activity == null || activity.isFinishing()) return;
        RandomTicketDialog progressReqDialog = new RandomTicketDialog(activity);
        progressReqDialog.show();
        progressReqDialog.setTextVisible(View.VISIBLE);
        progressReqDialog.setContentText("努力抢票中...");
        mModel.getRandomLottery(UserUtils.getToken(), id, type)
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<LotteryRandomEntity>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort(e.getMsg());
                        progressReqDialog.dismiss();
                    }

                    @Override
                    protected void onSuccess(LotteryRandomEntity response) {
                        if (response.getId() == 0) {
                            ToastUtils.showShort("彩票被抢光了，快去看看其他彩票~");
                        } else {
                            //有票生成订单
                            if (activity.isFinishing()) return;
                            randomTicketOr(type, response, amount, id, progressReqDialog);
                        }
                    }
                });
    }

    /**
     * 单张票生成订单
     */
    private void randomTicketOr(int type, LotteryRandomEntity lotteryEntity, int amount, long id,
                                RandomTicketDialog progressReqDialog) {
        if (progressReqDialog != null) progressReqDialog.setContentText("正在生成订单...");
        Map<String, String> params = new HashMap<>();
        params.put("token", UserUtils.getToken());
        if (type == 1) {
            params.put("packages", lotteryEntity.getCode());
        } else if (type == 2) {
            params.put("singles", String.valueOf(lotteryEntity.getId()));
        }
        params.put("amount", String.valueOf(amount));
        params.put("snNo", UserUtils.getSNNum());
        params.put("lotteryId", String.valueOf(id));
        mModel.saveOr(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<OderEntity>(progressReqDialog) {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort(e.getMsg());
                    }

                    @Override
                    protected void onSuccess(OderEntity response) {
                        orDerId = response.getOrderId();
                        String code = null;
                        switch (type) {
                            case 1:
                                code = lotteryEntity.getCode();
                                break;
                            case 2:
                                code = StringUtils.getTickSingleCode(lotteryEntity.getCode(), 13);
                                break;
                        }
                        UserUtils.openPayPage(response.getOrderId(), response.getUrl(), code, type);
                    }
                });
    }

    private LotteryFunListAdapter adapterFun;

    /**
     * 获取趣味玩法彩票列表
     */
    public void getLotteryFunList() {
        mModel.getLotteryFunList(UserUtils.getSNNum())
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<List<LotteryFunEntity>>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {

                    }

                    @Override
                    protected void onSuccess(List<LotteryFunEntity> response) {
                        setLotteryFunListData(response);
                    }
                });
    }

    private void setLotteryFunListData(List<LotteryFunEntity> response) {
        if (adapterFun == null) {
            adapterFun = new LotteryFunListAdapter(R.layout.item_lottery_fun_play, null);
            mRootView.setLotteryFunAdapter(adapterFun);
        }
        adapterFun.setNewData(response);
    }

    private LotterMsgAdapter adapterMsg;

    /**
     * 获取彩票详情右下角消息列表数据
     */
    public void getLotteryMsgData() {
        mModel.getLotteryMsg()
                .retryWhen(new RetryWithDelay(3, 2))
                .observeOn(AndroidSchedulers.mainThread())
                .compose(RxLifecycleUtils.bindToLifecycle(mRootView))
                .subscribe(new HttpRxObserver<List<String>>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {

                    }

                    @Override
                    protected void onSuccess(List<String> response) {
                        setLotteryMsgDA(response);
                    }
                });
    }

    private void setLotteryMsgDA(List<String> response) {
        if (adapterMsg == null) {
            adapterMsg = new LotterMsgAdapter(mRootView.getActivity());
            mRootView.setLotteryMsgAdapter(adapterMsg);
        }
        adapterMsg.setNewData(response);
        mRootView.onLotteryMsgRefreshed(response);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onActivityDestroy() {

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
