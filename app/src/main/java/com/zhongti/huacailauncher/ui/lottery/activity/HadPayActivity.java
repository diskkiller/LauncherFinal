package com.zhongti.huacailauncher.ui.lottery.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.utils.IntentDataHolder;
import com.zhongti.huacailauncher.app.utils.NumUtils;
import com.zhongti.huacailauncher.contract.ScratchContract;
import com.zhongti.huacailauncher.di.component.DaggerScratchComponent;
import com.zhongti.huacailauncher.di.module.ScratchModule;
import com.zhongti.huacailauncher.model.entity.LottiOrLeftEntity;
import com.zhongti.huacailauncher.model.entity.LottiOrListEntity;
import com.zhongti.huacailauncher.model.event.EventDjFinish;
import com.zhongti.huacailauncher.presenter.LotteryPresenter;
import com.zhongti.huacailauncher.ui.lottery.adapter.HadPayListAdapter;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhongti.huacailauncher.widget.errcall.EmptyCallback;
import com.zhongti.huacailauncher.widget.errcall.ErrorCallback;
import com.zhongti.huacailauncher.widget.errcall.LoadingCallback;
import com.zhongti.huacailauncher.widget.errcall.LoadingSmallCallback;
import com.zhongti.huacailauncher.widget.errcall.TransCallBack;
import com.zhongti.huacailauncher.widget.recycler.HadPayListDecoration;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

/**
 * Create by ShuHeMing on 18/6/5
 */
public class HadPayActivity extends BaseSupportActivity<LotteryPresenter> implements ScratchContract.View {

    @BindView(R.id.refresh_had_pay_list)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rl_had_pay_root)
    RelativeLayout rlRoot;
    @BindView(R.id.rv_had_pay_list)
    RecyclerView recycler;
    @BindView(R.id.iv_had_pay_thumb)
    ImageView ivThumb;
    @BindView(R.id.tv_had_pay_lotti_name)
    TextView tvName;
    @BindView(R.id.tv_had_pay_lotti_price)
    TextView tvPrice;
    @BindView(R.id.tv_had_pay_lotti_award)
    TextView tvAward;
    @BindView(R.id.tv_had_pay_orderNo)
    TextView tvOrderNo;
    @BindView(R.id.tv_had_pay_progress)
    TextView tvProgress;
    private LoadService loadServiceLeft;
    private LoadService loadServiceList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition explode = TransitionInflater.from(this).inflateTransition(R.transition.slide);
            Transition fade = TransitionInflater.from(this).inflateTransition(R.transition.fade);
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
        if (hasFocus) BarUtils.hideSystemUI(HadPayActivity.this);
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
        return R.layout.activity_had_pay;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initRetryView();
        initRecycler();
        if (rlRoot != null) {
            rlRoot.postDelayed(() -> {
                if (loadServiceLeft != null) loadServiceLeft.showCallback(LoadingCallback.class);
                if (loadServiceList != null)
                    loadServiceList.showCallback(LoadingSmallCallback.class);
                long id = getIntent().getLongExtra(Constants.INTENT_TO_LOTTI_ORDER_ID, 0);
                if (mPresenter != null && id != 0) {
                    mPresenter.reqHadPayListLeft(id);
                }
            }, 500);
        }
    }

    private void initRetryView() {
        loadServiceLeft = LoadSir.getDefault().register(rlRoot, (Callback.OnReloadListener) v -> {
            if (!NetworkUtils.isConnected()) {
                onLoadHadPayLeftErr(1);
                return;
            }
            loadServiceLeft.showCallback(LoadingCallback.class);
            if (mPresenter != null) {
                long id = getIntent().getLongExtra(Constants.INTENT_TO_LOTTI_ORDER_ID, 0);
                mPresenter.reqHadPayListLeft(id);
            }
        });

        LoadSir loadSir = new LoadSir.Builder()
                .addCallback(new LoadingSmallCallback())
                .addCallback(new EmptyCallback())
                .addCallback(new ErrorCallback())
                .addCallback(new TransCallBack())
                .build();

        loadServiceList = loadSir.register(refreshLayout, (Callback.OnReloadListener) v -> {
            if (loadServiceList.getCurrentCallback() == EmptyCallback.class)
                return;
            if (!NetworkUtils.isConnected()) {
                onLoadHadPayListErr(1);
                return;
            }
            loadServiceList.showCallback(LoadingSmallCallback.class);
            if (mPresenter != null) {
                long id = getIntent().getLongExtra(Constants.INTENT_TO_LOTTI_ORDER_ID, 0);
                mPresenter.reqHadPayList(id);
            }
        });

        loadServiceLeft.setCallBack(LoadingCallback.class, (context, view) -> {
            LottieAnimationView lottieAnimationView = view.findViewById(R.id.place_holder_loading);
            lottieAnimationView.setAnimation("LottieAnim/lottery_loading.json");
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lottieAnimationView.getLayoutParams();
            layoutParams.leftMargin = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x800);
            layoutParams.gravity = Gravity.START;
            TextView tvLoad = view.findViewById(R.id.tv_place_holder_loading);
            tvLoad.setVisibility(View.VISIBLE);
            tvLoad.setTextColor(ArmsUtils.getColor(Utils.getApp(), R.color.white));
            tvLoad.setText("正在努力加载...");
        });
        loadServiceList.setCallBack(LoadingSmallCallback.class, (context, view) -> {
            LottieAnimationView lottieAnimationView = view.findViewById(R.id.place_holder_loading);
            lottieAnimationView.setAnimation("LottieAnim/lottery_loading.json");
            TextView tvLoad = view.findViewById(R.id.tv_place_holder_loading);
            tvLoad.setVisibility(View.VISIBLE);
            tvLoad.setText("正在努力加载...");
        });
        loadServiceLeft.showCallback(TransCallBack.class);
        loadServiceList.showCallback(TransCallBack.class);
    }

    private void initRecycler() {
        GridLayoutManager manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.addItemDecoration(new HadPayListDecoration());
        recycler.addOnItemTouchListener(new MyOnItemClickListener());
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showMessage(@NonNull String message) {
        checkNotNull(message);
        ArmsUtils.snackbarText(message);
    }

    @Override
    public void launchActivity(@NonNull Intent intent) {
        checkNotNull(intent);
        ArmsUtils.startActivity(intent);
    }

    @Override
    public void killMyself() {
        finish();
    }

    @Override
    public void setHadPayAdapter(HadPayListAdapter adapterHadPay) {
        recycler.setAdapter(adapterHadPay);
        //设置动画
        adapterHadPay.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapterHadPay.isFirstOnly(false);
    }

    private String thumb;

    @SuppressLint("SetTextI18n")
    @Override
    public void setLottiOrLeftData(LottiOrLeftEntity response) {
        if (mPresenter != null) mPresenter.loadImg(response.getImg(), ivThumb);
        tvName.setText(TextUtils.isEmpty(response.getLotteryName()) ? "" : response.getLotteryName());
        tvPrice.setText("面值 ¥" + (TextUtils.isEmpty(response.getPrice()) ? "0" : response.getPrice()));
        tvAward.setText("最高奖金 ¥ " + NumUtils.getMoneyDou(String.valueOf(response.getReward())));
        tvOrderNo.setText("订单号: " + (TextUtils.isEmpty(response.getOrderNo()) ? "" : response.getOrderNo()));
        thumb = response.getImg();
        if (loadServiceLeft != null) loadServiceLeft.showSuccess();
    }

    @Override
    public void onLoadHadPayLeftErr(int i) {
        if (loadServiceLeft == null) return;
        if (i == 1) {
            loadServiceLeft.setCallBack(ErrorCallback.class, (context, view) -> {
                TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                tvTips.setText(R.string.net_unavailable);
                TextView tvRetry = view.findViewById(R.id.tv_err_call_retry);
                tvTips.setTextColor(Color.WHITE);
                tvRetry.setVisibility(View.INVISIBLE);
            });
        } else if (i == 2) {
            loadServiceLeft.setCallBack(ErrorCallback.class, (context, view) -> {
                TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                tvTips.setText(R.string.data_null_and_retry);
                TextView tvRetry = view.findViewById(R.id.tv_err_call_retry);
                tvTips.setTextColor(Color.WHITE);
                tvRetry.setTextColor(Color.WHITE);
            });
        }
        loadServiceLeft.showCallback(ErrorCallback.class);
    }

    @Override
    public void onLoadHadPayListErr(int i) {
        if (loadServiceList == null) return;
        if (i == 1) {
            loadServiceList.setCallBack(ErrorCallback.class, (context, view) -> {
                TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                tvTips.setText(R.string.net_unavailable);
                TextView tvRetry = view.findViewById(R.id.tv_err_call_retry);
                tvTips.setTextColor(Color.WHITE);
                tvRetry.setVisibility(View.INVISIBLE);
            });
        } else if (i == 2) {
            loadServiceList.setCallBack(ErrorCallback.class, (context, view) -> {
                TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                tvTips.setText(R.string.data_null_and_retry);
                TextView tvRetry = view.findViewById(R.id.tv_err_call_retry);
                tvTips.setTextColor(Color.WHITE);
                tvRetry.setTextColor(Color.WHITE);
            });
        }
        loadServiceList.showCallback(ErrorCallback.class);
    }

    @Override
    public void onLoadHadPayListSuccess(boolean isNull) {
        if (loadServiceList == null) return;
        if (isNull) {
            loadServiceList.showCallback(EmptyCallback.class);
        } else {
            loadServiceList.showSuccess();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void setLottiOrLeftProgress(int hadDj, int size) {
        tvProgress.setText("刮奖进度 " + hadDj + "/" + size);
    }

    @Subscriber(mode = ThreadMode.MAIN, tag = EventBusTags.EVENT_DJ_SUCCESS)
    private void onDjSuccess(EventDjFinish finish) {
        if (mPresenter != null) {
            mPresenter.onDjSuccess(finish.getPos());
            mPresenter.onDjPlusPlus();
        }
    }

    @OnClick({R.id.fl_had_pay_back, R.id.iv_had_pay_thumb})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_had_pay_back:
                killMyself();
                break;
            case R.id.iv_had_pay_thumb:
                openBigImgPage();
                break;
        }
    }

    private class MyOnItemClickListener extends OnItemClickListener {
        @Override
        public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
            openDjPage(position);
        }
    }

    @SuppressWarnings("unchecked")
    private void openDjPage(int position) {
        if (mPresenter == null) return;
        List<LottiOrListEntity> holderData;
        try {
            holderData = (List<LottiOrListEntity>) IntentDataHolder.getInstance().getHolderData();
        } catch (Exception e) {
            holderData = null;
            Timber.e("can't turn to duijiang page,because the list can't obtain!!");
        }
        if (holderData == null || holderData.size() == 0) {
            ToastUtils.showShort("彩票数据不存在~");
            return;
        }
        Intent intent = new Intent(this, ScratchDJActivity.class);
        intent.putExtra(Constants.INTENT_TO_DJ_DETAIL_POS, position);
        intent.putExtra(Constants.INTENT_TO_DJ_DETAIL_PRICE, mPresenter.getHadPayPrice());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void openBigImgPage() {
        if (TextUtils.isEmpty(thumb)) return;
        ArrayList<String> bigImg = new ArrayList<>();
        bigImg.add(thumb);
        Intent intent = new Intent(this, BigLottiImgActivity.class);
        intent.putExtra("big_imgs_pos", 0);
        intent.putExtra("big_imgs", bigImg);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, ivThumb, "big_img");
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }
}
