package com.zhongti.huacailauncher.ui.personal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportFragment;
import com.zhongti.huacailauncher.contract.PrivateContract;
import com.zhongti.huacailauncher.di.component.DaggerPrivateComponent;
import com.zhongti.huacailauncher.di.module.PrivateModule;
import com.zhongti.huacailauncher.model.entity.GoldRecordEntity;
import com.zhongti.huacailauncher.model.event.EvClosePrivatePage;
import com.zhongti.huacailauncher.presenter.PrivatePresenter;
import com.zhongti.huacailauncher.ui.personal.adapter.PrivateGoldAdapter;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.widget.errcall.EmptyCallback;
import com.zhongti.huacailauncher.widget.errcall.ErrorCallback;
import com.zhongti.huacailauncher.widget.errcall.LoadingCallback300;
import com.zhongti.huacailauncher.widget.errcall.TransCallBack;
import com.zhongti.huacailauncher.widget.recycler.PrivateGoldItemDecoration;

import org.simple.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by ShuHeMing on 18/6/6
 */
public class GoldOrFragment extends BaseSupportFragment<PrivatePresenter> implements PrivateContract.View {

    @BindView(R.id.refresh_private_gold)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.rv_private_gold_or)
    RecyclerView recycler;

    @BindView(R.id.rg_private_gold)
    RadioGroup rgGold;
    private LoadService loadService;

    public static GoldOrFragment newInstance() {

        Bundle args = new Bundle();

        GoldOrFragment fragment = new GoldOrFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerPrivateComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .privateModule(new PrivateModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_private_gold_or, container, false);
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        initRecyclerView();
        initRadio();
        rgGold.check(R.id.rb_private_gold1);
        initRetryView();
        initRefreshLayout();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        refreshData();
    }

    private void refreshData() {
        if (loadService != null) loadService.showCallback(LoadingCallback300.class);
        if (mPresenter != null) mPresenter.reqLottiGoldList(true);
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(_mActivity, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.addItemDecoration(new PrivateGoldItemDecoration(ArmsUtils.getColor(_mActivity, R.color.black5)));
    }

    private void initRadio() {
        rgGold.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_private_gold1:
                    break;
                case R.id.rb_private_gold2:
                    break;
                case R.id.rb_private_gold3:
                    break;
            }
        });
    }

    private void initRetryView() {
        LoadSir loadSir = new LoadSir.Builder()
                .addCallback(new LoadingCallback300())
                .addCallback(new EmptyCallback())
                .addCallback(new ErrorCallback())
                .addCallback(new TransCallBack())
                .build();
        loadService = loadSir.register(refreshLayout, (Callback.OnReloadListener) v -> {
            if (loadService.getCurrentCallback() == EmptyCallback.class)
                return;
            //网络未连接
            if (!NetworkUtils.isConnected()) {
                onRefreshGoldErr(1);
                return;
            }
            //重新加载数据的逻辑
            loadService.showCallback(LoadingCallback300.class);
            if (mPresenter != null) mPresenter.reqLottiGoldList(true);
        });
        loadService.setCallBack(LoadingCallback300.class, (context, view) -> {
            LottieAnimationView lottieAnimationView = view.findViewById(R.id.place_holder_loading);
            lottieAnimationView.setAnimation("LottieAnim/lottery_loading.json");
            TextView tvLoad = view.findViewById(R.id.tv_place_holder_loading);
            tvLoad.setVisibility(View.VISIBLE);
            tvLoad.setText("努力加载中...");
        });
        loadService.showCallback(TransCallBack.class);
    }

    private void initRefreshLayout() {
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            if (refreshlayout != null) refreshlayout.setNoMoreData(false);
            if (mPresenter != null) mPresenter.reqLottiGoldList(true);
        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            if (mPresenter != null) mPresenter.reqLottiGoldList(false);
        });
    }

    @Override
    public void refreshGoldEnd(List<GoldRecordEntity> data) {
        if (loadService == null) return;
        if (data == null || data.size() == 0) {
            loadService.showCallback(EmptyCallback.class);
        } else {
            loadService.showSuccess();
        }
    }

    @Override
    public void onRefreshGoldErr(int i) {
        switch (i) {
            case 1:
                loadService.setCallBack(ErrorCallback.class, (context, view) -> {
                    TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                    tvTips.setText(R.string.net_unavailable);
                    TextView tvRetry = view.findViewById(R.id.tv_err_call_retry);
                    tvRetry.setVisibility(View.INVISIBLE);
                });
                break;
            case 2:
                loadService.setCallBack(ErrorCallback.class, (context, view) -> {
                    TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                    tvTips.setText(R.string.data_null_and_retry);
                });
                break;
            case 3:
                break;
        }
        loadService.showCallback(ErrorCallback.class);
    }

    @Override
    public void loadGoldEnd() {
        if (refreshLayout != null) refreshLayout.setNoMoreData(true);
    }

    @Override
    public void finishLoadGold(boolean isRefresh) {
        if (isRefresh) {
            if (refreshLayout != null) refreshLayout.finishRefresh();
        } else {
            if (refreshLayout != null) refreshLayout.finishLoadMore();
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {

    }

    @Override
    public void launchActivity(@NonNull Intent intent) {

    }

    @Override
    public void killMyself() {

    }

    @Override
    public void setGoldAdapter(PrivateGoldAdapter adapterGold) {
        recycler.setAdapter(adapterGold);
    }

    @OnClick(R.id.fl_private_gold_close)
    public void onViewClicked() {
        EventBus.getDefault().post(new EvClosePrivatePage());
    }
}
