package com.zhongti.huacailauncher.ui.personal.fragment.child;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.jess.arms.di.component.AppComponent;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportFragment;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.contract.PrivateContract;
import com.zhongti.huacailauncher.di.component.DaggerPrivateComponent;
import com.zhongti.huacailauncher.di.module.PrivateModule;
import com.zhongti.huacailauncher.model.entity.PrivateLottiListEntity;
import com.zhongti.huacailauncher.model.event.EventDelLottiSuccess;
import com.zhongti.huacailauncher.model.event.EventDjFinish;
import com.zhongti.huacailauncher.presenter.PrivatePresenter;
import com.zhongti.huacailauncher.ui.lottery.activity.HadPayActivity;
import com.zhongti.huacailauncher.ui.personal.activity.DelLottiSureActivity;
import com.zhongti.huacailauncher.ui.personal.adapter.PrivateLotti1Adapter;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.widget.errcall.EmptyCallback;
import com.zhongti.huacailauncher.widget.errcall.ErrorCallback;
import com.zhongti.huacailauncher.widget.errcall.LoadingCallback300;
import com.zhongti.huacailauncher.widget.errcall.TransCallBack;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;

/**
 * Create by ShuHeMing on 18/6/6
 */
public class Lotti1Fragment extends BaseSupportFragment<PrivatePresenter> implements PrivateContract.View {
    private View rootView;
    @BindView(R.id.refresh_private_lotti1)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rv_private_lotti_child)
    RecyclerView recycler;
    private LoadService loadService;

    public static Lotti1Fragment newInstance() {

        Bundle args = new Bundle();

        Lotti1Fragment fragment = new Lotti1Fragment();
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
        if (rootView == null)
            rootView = inflater.inflate(R.layout.fragment_private_lotti_child, container, false);
        initRetryView();
        return loadService.getLoadLayout();
    }

    private void initRetryView() {
        LoadSir loadSir = new LoadSir.Builder()
                .addCallback(new LoadingCallback300())
                .addCallback(new EmptyCallback())
                .addCallback(new ErrorCallback())
                .addCallback(new TransCallBack())
                .build();
        loadService = loadSir.register(rootView, (Callback.OnReloadListener) v -> {
            if (loadService.getCurrentCallback() == EmptyCallback.class)
                return;
            //网络不可用
            if (!NetworkUtils.isConnected()) {
                onRefreshLotti1Err(1);
                return;
            }
            //重新加载数据的逻辑
            loadService.showCallback(LoadingCallback300.class);
            if (mPresenter != null) mPresenter.reqLottiBuyList(true);
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

    @Override
    protected void initViews() {
        super.initViews();
        initRefreshLayout();
        initRecyclerView();
    }

    private void initRefreshLayout() {
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            if (refreshlayout != null) refreshlayout.setNoMoreData(false);
            if (mPresenter != null) mPresenter.reqLottiBuyList(true);
        });
        refreshLayout.setOnLoadMoreListener(refreshlayout -> {
            if (mPresenter != null) mPresenter.reqLottiBuyList(false);
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(_mActivity, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.addOnItemTouchListener(new MyOnItemChildClickListener());
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        if (rootView != null) {
            rootView.postDelayed(() -> {
                if (loadService != null) loadService.showCallback(LoadingCallback300.class);
                if (refreshLayout != null) refreshLayout.autoRefresh();
            }, 800);
        }

    }

    @Subscriber(mode = ThreadMode.MAIN, tag = EventBusTags.EVENT_DJ_SUCCESS)
    private void onDjSuccess(EventDjFinish finish) {
        if (mPresenter != null) mPresenter.reqLottiBuyList(true);
    }

    @Override
    public void setData(@Nullable Object data) {

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
    public void onSupportVisible() {
        super.onSupportVisible();
    }

    @Override
    public void setLotti1Adapter(PrivateLotti1Adapter adapterLotti1) {
        recycler.setAdapter(adapterLotti1);
    }

    @Override
    public void refreshLotti1End(List<PrivateLottiListEntity> data) {
        if (loadService == null) return;
        if (data == null || data.size() == 0) {
            loadService.showCallback(EmptyCallback.class);
        } else {
            loadService.showSuccess();
        }
    }

    @Override
    public void onRefreshLotti1Err(int i) {
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
    public void loadLotti1End() {
        if (refreshLayout != null) refreshLayout.setNoMoreData(true);
    }

    @Override
    public void finishLoadLotti1(boolean isRefresh) {
        if (isRefresh) {
            if (refreshLayout != null) refreshLayout.finishRefresh();
        } else {
            if (refreshLayout != null) refreshLayout.finishLoadMore();
        }
    }

    private class MyOnItemChildClickListener extends OnItemChildClickListener {
        @Override
        public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
            PrivateLottiListEntity entity = (PrivateLottiListEntity) adapter.getData().get(position);
            switch (view.getId()) {
                case R.id.btn_item_private_buy_del:
                    delItem(entity.getId(), position);
                    break;
                case R.id.btn_item_private_buy_pw:
                    openOrList(entity.getId());
                    break;
            }
        }
    }

    private void delItem(long id, int position) {
        Intent intent = new Intent(_mActivity, DelLottiSureActivity.class);
        intent.putExtra("del_id", id);
        intent.putExtra("del_pos", position);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(_mActivity);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Subscriber(tag = EventBusTags.EVENT_DEL_LOTTI_SUCCESS, mode = ThreadMode.MAIN)
    private void onDelSuccess(EventDelLottiSuccess lottiSuccess) {
        if (mPresenter != null) mPresenter.delLottiOrSuccess(lottiSuccess.getPosition());
    }

    private void openOrList(long id) {
        if (mPresenter == null) return;
        Intent intent = new Intent(_mActivity, HadPayActivity.class);
        intent.putExtra(Constants.INTENT_TO_LOTTI_ORDER_ID, id);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(_mActivity);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }
}
