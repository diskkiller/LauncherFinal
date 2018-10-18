package com.zhongti.huacailauncher.ui.lottery.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;

import com.jess.arms.di.component.AppComponent;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.model.event.EventBigImgClose;
import com.zhongti.huacailauncher.ui.lottery.adapter.BigPicAdapter;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.widget.HackyViewPager;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by ShuHeMing on 18/6/11
 */
public class BigLottiImgActivity extends BaseSupportActivity {

    @BindView(R.id.vp_big_img_loader)
    HackyViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BarUtils.hideSystemUI(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) BarUtils.hideSystemUI(this);
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_lotti_bigimg;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        int position = getIntent().getIntExtra("big_imgs_pos", 0);
        ArrayList<String> imgs = getIntent().getStringArrayListExtra("big_imgs");
        viewPager.setAdapter(new BigPicAdapter(getSupportFragmentManager(), imgs));
        viewPager.setCurrentItem(position);

    }

    @OnClick(R.id.fl_lotti_bigImg_back)
    public void onViewClicked() {
        finish();
    }

    @Subscriber(tag = EventBusTags.EVENT_BIG_IMG_CLOSE, mode = ThreadMode.MAIN)
    private void onPageClose(EventBigImgClose eventBigImgClose) {
        finish();
    }
}
