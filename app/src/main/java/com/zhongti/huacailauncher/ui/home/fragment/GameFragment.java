package com.zhongti.huacailauncher.ui.home.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jess.arms.di.component.AppComponent;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportFragment;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.contract.MainContract;
import com.zhongti.huacailauncher.di.component.DaggerMainComponent;
import com.zhongti.huacailauncher.di.module.MainModule;
import com.zhongti.huacailauncher.model.entity.HomeGameEntity;
import com.zhongti.huacailauncher.model.event.EventNetWorkChange;
import com.zhongti.huacailauncher.presenter.MainPresenter;
import com.zhongti.huacailauncher.ui.home.adapter.HomeGameAdapter;
import com.zhongti.huacailauncher.ui.web.LHWebActivity;
import com.zhongti.huacailauncher.ui.web.RH5WebActivity;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.widget.recycler.MainGameDecoration;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Create by ShuHeMing on 18/7/29
 */
public class GameFragment extends BaseSupportFragment<MainPresenter> implements MainContract.View {

    @BindView(R.id.rv_main_games)
    RecyclerView rvGame;

    public static GameFragment newInstance() {

        Bundle args = new Bundle();

        GameFragment fragment = new GameFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {
        DaggerMainComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInceState) {
        return inflater.inflate(R.layout.fragment_main_game, container, false);
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        initGameRecycler();
        refreshData();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        refreshData();
    }

    @Subscriber(tag = EventBusTags.EVENT_NET_CONNECTED, mode = ThreadMode.MAIN)
    private void onNetConnected(EventNetWorkChange netWorkChange) {
        Timber.i("网络状态: 已连接------- 刷新游戏列表数据");
        refreshData();
    }

    public void refreshData() {
        if (mPresenter != null)
            mPresenter.reqGameListData(true);
    }

    private void initGameRecycler() {
        rvGame.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayout.HORIZONTAL, false));
        rvGame.addItemDecoration(new MainGameDecoration(50));
        rvGame.setNestedScrollingEnabled(false);
        rvGame.addOnItemTouchListener(new OnGameItemClickListener());
    }

    private class OnGameItemClickListener extends OnItemClickListener {
        @Override
        public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
            HomeGameEntity entity = (HomeGameEntity) adapter.getData().get(position);
            if (UserUtils.isLoginOnly()) {
                if (rvGame != null)
                    rvGame.postDelayed(() -> openGamePage(entity.getGameUrl(), entity.getType()), 100);
            } else {
                UserUtils.isBindAndLogin(UserUtils::isLogin, GameFragment.this, GameFragment.this.getActivity());
            }
        }
    }

    private void openGamePage(String url, int type) {
        if (isDetached()) return;
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort(R.string.net_unavailable);
            return;
        }
        if (TextUtils.isEmpty(url)) {
            ToastUtils.showShort("暂未开放!");
            return;
        }
        Intent intent;
        if (type == 1) {
            intent = new Intent(_mActivity, RH5WebActivity.class);
        } else if (type == 2) {
            intent = new Intent(_mActivity, LHWebActivity.class);
        } else {
            intent = new Intent(_mActivity, RH5WebActivity.class);
        }
        intent.putExtra(Constants.INTENT_REMOTE_WEB_URL, UserUtils.getDealH5Url(url));
        intent.putExtra(Constants.INTENT_REMOTE_WEB_LOAD, 1);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this.getActivity());
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void setGameListAdapter(HomeGameAdapter adapterGame) {
        rvGame.setAdapter(adapterGame);
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
}
