package com.zhongti.huacailauncher.widget.errcall;

import android.content.Context;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.kingja.loadsir.callback.Callback;
import com.zhongti.huacailauncher.R;


/**
 * Description:TODO
 * Create Time:2017/9/4 10:22
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */

public class LoadingCallback extends Callback {

    private LottieAnimationView loadingView;

    @Override
    protected int onCreateView() {
        return R.layout.layout_placeholder_loading;
    }

    @Override
    public boolean getSuccessVisible() {
        return super.getSuccessVisible();
    }

    @Override
    protected boolean onReloadEvent(Context context, View view) {
        return true;
    }

    @Override
    public void onAttach(Context context, View view) {
        initAnimView(context, view);
    }

    private void initAnimView(Context context, View view) {
        loadingView = view.findViewById(R.id.place_holder_loading);
        loadingView.setImageAssetsFolder("images/");
        loadingView.playAnimation();
        super.onAttach(context, view);
    }

    @Override
    public void onDetach() {
        if (loadingView != null) {
            loadingView.cancelAnimation();
        }
        super.onDetach();
    }
}
