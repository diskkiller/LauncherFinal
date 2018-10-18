package com.zhongti.huacailauncher.ui.lottery.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.utils.IntentDataHolder;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.contract.ScratchContract;
import com.zhongti.huacailauncher.di.component.DaggerScratchComponent;
import com.zhongti.huacailauncher.di.module.ScratchModule;
import com.zhongti.huacailauncher.model.entity.LottiOrListEntity;
import com.zhongti.huacailauncher.model.event.EventDjFinish;
import com.zhongti.huacailauncher.model.event.EventDjPageClose;
import com.zhongti.huacailauncher.presenter.LotteryPresenter;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.SizeUtils;
import com.zhongti.huacailauncher.utils.code.SpanUtils;
import com.zhongti.huacailauncher.utils.code.StringUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhongti.huacailauncher.widget.sratch.ScratchView;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Create by ShuHeMing on 18/6/7
 */
public class ScratchDJActivity extends BaseSupportActivity<LotteryPresenter> implements ScratchContract.View, View.OnClickListener {
    @BindView(R.id.tv_scratch_dj_lottiName)
    TextView tvLottiName;
    @BindView(R.id.tv_scratch_dj_lottiPrice)
    TextView tvLottiPrice;
    @BindView(R.id.tv_scratch_dj_lottiAward)
    TextView tvLottiAward;
    @BindView(R.id.tv_scratch_dj_lottiNum)
    TextView tvLottiNum;
    @BindView(R.id.tv_scratch_dj_lottiPage)
    TextView tvLottiPage;
    @BindView(R.id.iv_scratch_dj_erCode)
    ImageView ivErCode;
    //    @BindView(R.id.sv_scratch_dj_erCode)
    ScratchView svErCode;
    @BindView(R.id.fl__scratch_dj_container)
    FrameLayout flContainer;
    private List<LottiOrListEntity> dataList;
    private int pos;
    private View llYp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置转场动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition explode = TransitionInflater.from(this).inflateTransition(R.transition.slide);
            Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.fade);
            explode.setInterpolator(new OvershootInterpolator(1.5f));
            explode.setDuration(500);
            getWindow().setEnterTransition(explode);
            getWindow().setReenterTransition(fade);
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        BarUtils.hideSystemUI(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) BarUtils.hideSystemUI(ScratchDJActivity.this);
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerScratchComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .scratchModule(new ScratchModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_scratch_dj;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        try {
            dataList = (List<LottiOrListEntity>) IntentDataHolder.getInstance().getHolderData();
        } catch (Exception e) {
            dataList = new ArrayList<>();
            Timber.e("彩票兑奖页面获取list数据失败!!");
        }
        pos = getIntent().getIntExtra(Constants.INTENT_TO_DJ_DETAIL_POS, 0);
        initViewClick();
        setInfo(pos);
    }

    private void initViewClick() {
        findViewById(R.id.fl_scratch_dj_back).setOnClickListener(this);
        findViewById(R.id.iv_scratch_dj_slide_left).setOnClickListener(this);
        findViewById(R.id.iv_scratch_dj_slide_right).setOnClickListener(this);
        llYp = findViewById(R.id.ll_scratch_dj_yp);
        llYp.setOnClickListener(this);
    }

    private void enableYpBtn(boolean enable) {
        if (enable) {
            llYp.setClickable(true);
            llYp.setFocusable(true);
            llYp.requestFocus();
            llYp.setVisibility(View.VISIBLE);
        } else {
            llYp.setClickable(false);
            llYp.setFocusable(false);
            llYp.setVisibility(View.INVISIBLE);
        }
    }

    private void initScratchView() {
        svErCode.setWatermark(R.drawable.img_gua_mask, false);
        svErCode.setEraseStatusListener(new ScratchView.EraseStatusListener() {
            @Override
            public void onProgress(int percent) {
                Timber.i("点击刮层百分比: " + percent + "%");
                if (percent >= 60) {
                    onHalfGua();
                }
            }

            @Override
            public void onCompleted(View view) {
                Timber.i("点击刮层onCompleted");
            }
        });
    }

    private void onHalfGua() {
        if (svErCode == null) return;
        svErCode.setEnabled(false);
        ViewCompat.animate(svErCode).setDuration(500).alpha(0.f).setListener(new ViewPropertyAnimatorListener() {
            @Override
            public void onAnimationStart(View view) {

            }

            @Override
            public void onAnimationEnd(View view) {
                if (svErCode != null) svErCode.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(View view) {

            }
        }).start();
        if (mPresenter != null && dataList.size() != 0 && pos >= 0 && pos <= dataList.size() - 1)
            mPresenter.lottiDj(dataList.get(pos).getId(), pos);
    }

    @Override
    public void onDjSuccess() {
        enableYpBtn(true);
        long id = getIntent().getLongExtra(Constants.INTENT_TO_DJ_DETAIL_ORID, 0);
        EventBus.getDefault().post(new EventDjFinish(pos, id), EventBusTags.EVENT_DJ_SUCCESS_FOR_H5);
    }

    @Override
    public void onDjErr() {
        enableYpBtn(false);
    }

    @Subscriber(mode = ThreadMode.MAIN, tag = EventBusTags.EVENT_DJ_SUCCESS)
    private void onDjSuccess(EventDjFinish finish) {
        if (dataList.size() == 0) return;
        if (pos < 0 || pos > dataList.size() - 1) return;
        LottiOrListEntity entity = dataList.get(finish.getPos());
        entity.setStatus(2);
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
        finish();
    }

    private Bitmap lastBitmap;

    @SuppressLint("SetTextI18n")
    private void setInfo(int pos) {
        if (dataList.size() == 0 || pos < 0 || pos > dataList.size() - 1) {
            tvLottiName.setText("彩票：");
            tvLottiPrice.setText("面值：");
            tvLottiAward.setText("最高奖金：");
            tvLottiNum.setText("票号: ");
            tvLottiPage.setText("");
            ivErCode.setImageResource(R.drawable.img_place_square);
            enableYpBtn(false);
            removeMask();
            return;
        }
        LottiOrListEntity entity = dataList.get(pos);
        int colorY = ArmsUtils.getColor(this, R.color.main_yellow);
        int colorR = ArmsUtils.getColor(this, R.color.had_pay_red);
        int colorG = ArmsUtils.getColor(this, R.color.black3);
        tvLottiName.setText("彩票：" + (entity == null || TextUtils.isEmpty(entity.getName()) ? "" : entity.getName()));
        String price = getIntent().getStringExtra(Constants.INTENT_TO_DJ_DETAIL_PRICE);
        tvLottiPrice.setText("面值：￥" + (TextUtils.isEmpty(price) ? "0" : price));
        tvLottiAward.setText("最高奖金：" + (entity == null ? 0 : entity.getReward()) / 10000 + "万");
        tvLottiNum.setText(new SpanUtils().append("票号: ").setFontSize(ArmsUtils.getDimens(Utils.getApp(), R.dimen.x28))
                .setForegroundColor(colorG)
                .append(StringUtils.getTickSingleCode((entity == null ? "" : entity.getCode()), 13))
                .setFontSize(ArmsUtils.getDimens(Utils.getApp(), R.dimen.x36)).setForegroundColor(colorY).create());
        tvLottiPage.setText(new SpanUtils().append(String.valueOf(pos + 1)).setForegroundColor(colorR).setFontSize(ArmsUtils.getDimens(Utils.getApp(), R.dimen.x40))
                .append("/" + dataList.size()).setFontSize(28).setForegroundColor(colorG).create());
        if (lastBitmap != null) {
            lastBitmap.recycle();
            lastBitmap = null;
            System.gc();
        }
        Bitmap bitmapCode = UserUtils.createLottErCode(String.valueOf(entity == null || TextUtils.isEmpty(entity.getCode()) ? "" : entity.getCode()), 260, 260);
        if (bitmapCode != null) {
            lastBitmap = bitmapCode;
            ivErCode.setImageBitmap(bitmapCode);
        } else {
            ToastUtils.showShort("二维码加载失败,请关闭页面重新打开");
            ivErCode.setImageResource(R.drawable.img_place_square);
        }
        if (entity == null || entity.getStatus() == 1) {
            newMask();
            enableYpBtn(false);
        } else {
            removeMask();
            enableYpBtn(true);
        }

    }

    private void newMask() {
        try {
            removeMask();
            svErCode = new ScratchView(this);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(SizeUtils.dp2px(260), SizeUtils.dp2px(260));
            svErCode.setLayoutParams(lp);

            flContainer.addView(svErCode);
            initScratchView();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void removeMask() {
        try {
            if (flContainer != null && svErCode != null) {
                flContainer.removeView(svErCode);
            }
            if (svErCode != null) {
                svErCode.releaseBitmap();
                svErCode = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_scratch_dj_back:
                killMyself();
                EventBus.getDefault().post(new EventDjPageClose(), EventBusTags.EVENT_ON_DJ_PAGE_CLOSE);
                break;
            case R.id.iv_scratch_dj_slide_left:
                if (dataList.size() == 0 || pos <= 0 || pos > dataList.size() - 1) {
                    ToastUtils.showShort("没有更多了~");
                    return;
                }
                pos--;
                setInfo(pos);
                break;
            case R.id.iv_scratch_dj_slide_right:
                if (dataList.size() == 0 || pos >= dataList.size() - 1 || pos < 0) {
                    ToastUtils.showShort("没有更多了~");
                    return;
                }
                pos++;
                setInfo(pos);
                break;
            case R.id.ll_scratch_dj_yp:
                //验票
                openCheckTicketPage();
                break;
        }
    }

    private void openCheckTicketPage() {
        if (dataList.size() == 0 || pos > dataList.size() - 1 || pos < 0) {
            ToastUtils.showShort("无法开始验票,请关闭页面重试");
            return;
        }
        LottiOrListEntity entity = dataList.get(pos);
        Intent intent = new Intent(this, CheckTicketSureActivity.class);
        intent.putExtra(Constants.INTENT_CHECK_TICKET_SURE_CODE, entity.getCode());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        EventBus.getDefault().post(new EventDjPageClose(), EventBusTags.EVENT_ON_DJ_PAGE_CLOSE);
    }
}
