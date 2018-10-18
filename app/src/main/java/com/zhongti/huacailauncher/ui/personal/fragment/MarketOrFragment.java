package com.zhongti.huacailauncher.ui.personal.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jess.arms.di.component.AppComponent;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportFragment;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.utils.EnvSwitchUtils;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.contract.PrivateContract;
import com.zhongti.huacailauncher.di.component.DaggerPrivateComponent;
import com.zhongti.huacailauncher.di.module.PrivateModule;
import com.zhongti.huacailauncher.model.event.EvClosePrivatePage;
import com.zhongti.huacailauncher.model.event.EventRefreshH5Urls;
import com.zhongti.huacailauncher.presenter.PrivatePresenter;
import com.zhongti.huacailauncher.ui.web.RH5WebActivity;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.SPUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;

import org.simple.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by ShuHeMing on 18/6/6
 */
public class MarketOrFragment extends BaseSupportFragment<PrivatePresenter> implements PrivateContract.View {

    @BindView(R.id.iv_private_market_er)
    ImageView ivEr;

    public static MarketOrFragment newInstance() {

        Bundle args = new Bundle();

        MarketOrFragment fragment = new MarketOrFragment();
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
        return inflater.inflate(R.layout.fragment_private_market_or, container, false);
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (EnvSwitchUtils.isDebug) {
            ivEr.setImageResource(R.drawable.img_er_market_or_test);
        } else {
            ivEr.setImageResource(R.drawable.img_er_market_or_release);
        }
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
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

    @OnClick({R.id.fl_private_market_close, R.id.btn_private_market_go})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_private_market_close:
                EventBus.getDefault().post(new EvClosePrivatePage());
                break;
            case R.id.btn_private_market_go:
                openGoldMarketPage();
                break;
        }
    }

    private void openGoldMarketPage() {
        String url = SPUtils.getInstance().getString(Constants.LINK_HOME_GOLD);
        if (TextUtils.isEmpty(url) && mPresenter != null) {
            if (!NetworkUtils.isConnected()) {
                ToastUtils.showShort(R.string.net_unavailable);
                return;
            }
            ToastUtils.showShort("页面正在赶来,请稍后重试");
            EventBus.getDefault().post(new EventRefreshH5Urls(), EventBusTags.EVENT_REFRESH_H5_LINKS);
            return;
        }
        Intent intent = new Intent(_mActivity, RH5WebActivity.class);
        intent.putExtra(Constants.INTENT_REMOTE_WEB_URL, UserUtils.getDealH5Url(url));
        intent.putExtra(Constants.INTENT_REMOTE_WEB_LOAD, 2);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(_mActivity);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }
}
