package com.zhongti.huacailauncher.ui.personal.fragment.child;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jess.arms.di.component.AppComponent;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportFragment;
import com.zhongti.huacailauncher.contract.PrivateContract;
import com.zhongti.huacailauncher.di.component.DaggerPrivateComponent;
import com.zhongti.huacailauncher.di.module.PrivateModule;
import com.zhongti.huacailauncher.presenter.PrivatePresenter;
import com.zhongti.huacailauncher.ui.personal.adapter.PrivateLotti2Adapter;

import butterknife.BindView;

/**
 * Create by ShuHeMing on 18/6/6
 */
public class Lotti2Fragment extends BaseSupportFragment<PrivatePresenter> implements PrivateContract.View {
    private View rootView;
    @BindView(R.id.rv_private_lotti_child)
    RecyclerView recycler;

    public static Lotti2Fragment newInstance() {

        Bundle args = new Bundle();

        Lotti2Fragment fragment = new Lotti2Fragment();
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
        return rootView;
    }

    @Override
    protected void initViews() {
        super.initViews();
        initRecyclerView();
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(_mActivity, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
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
        if (mPresenter != null) mPresenter.reqLottiCheckList();
    }

    @Override
    public void setLotti2Adapter(PrivateLotti2Adapter adapterLotti2) {
        recycler.setAdapter(adapterLotti2);
    }
}
