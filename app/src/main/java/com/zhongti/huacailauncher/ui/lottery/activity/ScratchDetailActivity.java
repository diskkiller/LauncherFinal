package com.zhongti.huacailauncher.ui.lottery.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
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
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.common.Constants;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.utils.NumUtils;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.contract.ScratchContract;
import com.zhongti.huacailauncher.di.component.DaggerScratchComponent;
import com.zhongti.huacailauncher.di.module.ScratchModule;
import com.zhongti.huacailauncher.model.entity.LotteryFunEntity;
import com.zhongti.huacailauncher.model.entity.LottiDetailEntity;
import com.zhongti.huacailauncher.model.entity.LottiListEntity;
import com.zhongti.huacailauncher.model.event.EventLH5PageClose;
import com.zhongti.huacailauncher.model.event.EventUpdateUser;
import com.zhongti.huacailauncher.presenter.LotteryPresenter;
import com.zhongti.huacailauncher.ui.lottery.adapter.LotteryFunListAdapter;
import com.zhongti.huacailauncher.ui.lottery.adapter.LotterMsgAdapter;
import com.zhongti.huacailauncher.ui.lottery.adapter.ScratchTicketListAdapter;
import com.zhongti.huacailauncher.ui.web.LHWebActivity;
import com.zhongti.huacailauncher.utils.code.ActivityUtils;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.RegexUtils;
import com.zhongti.huacailauncher.utils.code.SpanUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhongti.huacailauncher.widget.auto.AutoSmartRefreshLayout;
import com.zhongti.huacailauncher.widget.errcall.EmptyCallback;
import com.zhongti.huacailauncher.widget.errcall.ErrorCallback;
import com.zhongti.huacailauncher.widget.errcall.LoadingCallback;
import com.zhongti.huacailauncher.widget.errcall.LoadingSmallCallback;
import com.zhongti.huacailauncher.widget.errcall.TransCallBack;
import com.zhongti.huacailauncher.widget.recycler.AutoPollRecyclerView;
import com.zhongti.huacailauncher.widget.recycler.LotteryFunDecoration;
import com.zhongti.huacailauncher.widget.recycler.LotteryMsgDecoration;
import com.zhongti.huacailauncher.widget.recycler.TicketsListDecoration;
import com.zhy.autolayout.AutoLinearLayout;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class ScratchDetailActivity extends BaseSupportActivity<LotteryPresenter> implements ScratchContract.View {

    @BindView(R.id.iv_scratch_detail_rule_positive)
    ImageView ivThumb;
    @BindView(R.id.tv_scratch_detail_info_name)
    TextView tvInfoName;
    @BindView(R.id.tv_scratch_detail_info_price)
    TextView tvInfoPrice;
    @BindView(R.id.tv_scratch_detail_info_award)
    TextView tvInfoAward;
    @BindView(R.id.tv_scratch_detail_info_sale_num)
    TextView tvSaleNum;
    @BindView(R.id.tv_scratch_detail_num_per_package)
    TextView tvPerPackage;
    @BindView(R.id.tv_scratch_detail_buy_all)
    Button tvBuyAll;
    @BindView(R.id.tv_scratch_detail_buy_one)
    Button tvBuyOne;
    @BindView(R.id.refresh_scratch_tickets)
    AutoSmartRefreshLayout refreshList;
    @BindView(R.id.rv_scratch_tickets)
    RecyclerView rvTickets;
    @BindView(R.id.tv_scratch_detail_count_time)
    TextView tvCountTime;
    @BindView(R.id.tv_scratch_detail_selected)
    TextView tvSelected;
    @BindView(R.id.btn_scratch_detail_pay)
    Button btnPay;
    @BindView(R.id.rl__scratch_rule_root)
    RelativeLayout rlRoot;
    private LoadService loadService;
    private LoadService loadServiceList;

    @BindView(R.id.rl_scratch_detail_fun_play_shrink)
    RelativeLayout rlShrink;
    @BindView(R.id.iv_scratch_detail_fun_expand)
    ImageView ivExpand;
    @BindView(R.id.rv_scratch_detail_fun_play)
    RecyclerView rvFun;
    @BindView(R.id.rv_scratch_detail_msgs)
    AutoPollRecyclerView rvMsg;

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
        if (hasFocus) BarUtils.hideSystemUI(ScratchDetailActivity.this);
    }

    @Override
    public Activity getActivity() {
        return this;
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
        return R.layout.activity_scratch_detail1; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    private boolean isFirst;

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initRetryView();
        initTimer();
        initRecycler();
        initFunRecyclerView();
        initMsgRecyclerView();
        setDef();
        if (rlRoot != null) {
            rlRoot.postDelayed(() -> {
                if (loadService != null) loadService.showCallback(LoadingCallback.class);
                if (loadServiceList != null)
                    loadServiceList.showCallback(LoadingSmallCallback.class);
                if (mPresenter != null) {
                    long detailId = getIntent().getLongExtra(Constants.INTENT_TO_LOTTI_DETAIL, -1);
                    mPresenter.reqLottiDetail(detailId, true);
                    mPresenter.getLotteryMsgData();
                }
            }, 500);
        }
        isFirst = true;
    }

    private void initMsgRecyclerView() {
        rvMsg.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvMsg.addItemDecoration(new LotteryMsgDecoration());
    }

    private void initRetryView() {
        loadService = LoadSir.getDefault().register(rlRoot, (Callback.OnReloadListener) v -> {
            if (!NetworkUtils.isConnected()) {
                netWorkUnavailable();
                return;
            }
            loadService.showCallback(LoadingCallback.class);
            if (mPresenter != null) {
                long detailId = getIntent().getLongExtra(Constants.INTENT_TO_LOTTI_DETAIL, -1);
                mPresenter.reqLottiDetail(detailId, true);
                mPresenter.getLotteryMsgData();
            }
        });

        LoadSir loadSir = new LoadSir.Builder()
                .addCallback(new LoadingSmallCallback())
                .addCallback(new EmptyCallback())
                .addCallback(new ErrorCallback())
                .addCallback(new TransCallBack())
                .build();
        loadServiceList = loadSir.register(refreshList, (Callback.OnReloadListener) v -> {
            if (loadServiceList.getCurrentCallback() == EmptyCallback.class)
                return;
            if (!NetworkUtils.isConnected()) {
                onScratchDetailListErr(1);
                return;
            }
            loadServiceList.showCallback(LoadingSmallCallback.class);
            if (mPresenter != null) {
                long detailId = getIntent().getLongExtra(Constants.INTENT_TO_LOTTI_DETAIL, -1);
                mPresenter.reqTicketListData(detailId, currentMode);
            }
        });

        loadService.setCallBack(LoadingCallback.class, (context, view) -> {
            LottieAnimationView lottieAnimationView = view.findViewById(R.id.place_holder_loading);
            lottieAnimationView.setAnimation("LottieAnim/lottery_loading.json");
            AutoLinearLayout.LayoutParams layoutParams = (AutoLinearLayout.LayoutParams) lottieAnimationView.getLayoutParams();
            layoutParams.leftMargin = ArmsUtils.getDimens(Utils.getApp(), R.dimen.x800);
            layoutParams.gravity = Gravity.START;
            TextView tvLoad = view.findViewById(R.id.tv_place_holder_loading);
            tvLoad.setVisibility(View.VISIBLE);
            tvLoad.setTextColor(ArmsUtils.getColor(Utils.getApp(), R.color.white));
            tvLoad.setText("一大波彩票正在赶来...");
        });

        loadServiceList.setCallBack(LoadingSmallCallback.class, (context, view) -> {
            LottieAnimationView lottieAnimationView = view.findViewById(R.id.place_holder_loading);
            lottieAnimationView.setAnimation("LottieAnim/lottery_loading.json");
            TextView tvLoad = view.findViewById(R.id.tv_place_holder_loading);
            tvLoad.setVisibility(View.VISIBLE);
            tvLoad.setText("正在努力加载...");
        });

        loadServiceList.setCallBack(EmptyCallback.class, (context, view) -> {
            TextView tvEmpty = view.findViewById(R.id.tv_call_empty);
            tvEmpty.setTextColor(ArmsUtils.getColor(Utils.getApp(), R.color.black2));
            tvEmpty.setText("彩票被抢光了，快去看看其他彩票~");
        });

        loadService.showCallback(TransCallBack.class);
        loadServiceList.showCallback(TransCallBack.class);
    }

    private void initRecycler() {
        GridLayoutManager manager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        rvTickets.setLayoutManager(manager);
        rvTickets.setHasFixedSize(true);
        rvTickets.addItemDecoration(new TicketsListDecoration());
        rvTickets.addOnItemTouchListener(new MyOnItemClickListener());
    }

    private Timer timer;
    private int colorG;
    private int colorR;

    private void initTimer() {
        timer = new Timer();
        colorR = ArmsUtils.getColor(this, R.color.scratch_detail_rule_info_tip);
        colorG = ArmsUtils.getColor(this, R.color.black3);
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
    public void setTicketAdapter(ScratchTicketListAdapter adapterTicket) {
        rvTickets.setAdapter(adapterTicket);
        adapterTicket.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapterTicket.isFirstOnly(false);
    }

    @Override
    public void netWorkUnavailable() {
        if (loadService != null) {
            loadService.setCallBack(ErrorCallback.class, (context, view) -> {
                TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                tvTips.setText(R.string.net_unavailable);
                TextView tvRetry = view.findViewById(R.id.tv_err_call_retry);
                tvTips.setTextColor(Color.WHITE);
                tvRetry.setVisibility(View.INVISIBLE);
            });
            loadService.showCallback(ErrorCallback.class);
        }
    }

    @Override
    public void onLoadLotteryDetailErr() {
        if (loadService != null) {
            loadService.setCallBack(ErrorCallback.class, (context, view) -> {
                TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                tvTips.setText(R.string.data_null_and_retry);
                TextView tvRetry = view.findViewById(R.id.tv_err_call_retry);
                tvTips.setTextColor(Color.WHITE);
                tvRetry.setTextColor(Color.WHITE);
            });
            loadService.showCallback(ErrorCallback.class);
        }
    }

    @Override
    public void onScratchDetailListErr(int i) {
        if (loadServiceList == null) return;
        switch (i) {
            case 1:
                loadServiceList.setCallBack(ErrorCallback.class, (context, view) -> {
                    TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                    tvTips.setText(R.string.net_unavailable);
                    TextView tvRetry = view.findViewById(R.id.tv_err_call_retry);
                    tvRetry.setVisibility(View.INVISIBLE);
                });
                break;
            case 2:
                loadServiceList.setCallBack(ErrorCallback.class, (context, view) -> {
                    TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                    tvTips.setText(R.string.data_null_and_retry);
                });
                break;
        }
        loadServiceList.showCallback(ErrorCallback.class);
    }

    private boolean isLoadDataSuccess;

    @Override
    public void onLoadScratchDetailListSuccess() {
        if (loadServiceList != null) loadServiceList.showSuccess();
        if (isFirst) {
            executeShrinkAnim(0);
            if (rlShrink != null) rlShrink.setVisibility(View.VISIBLE);
            letExpandBtnShow(false);
            executeExpandAnim();
            isFirst = false;
        }
        isLoadDataSuccess = true;
    }

    @Override
    public void onLoadScratchDetailListEmpty() {
        if (loadServiceList != null)
            loadServiceList.showCallback(EmptyCallback.class);
    }

    private String price = "0";

    private int numPerPkg;

    private ArrayList<String> bigImg1 = new ArrayList<>();
    private ArrayList<String> bigImg2 = new ArrayList<>();

    private String lottiName;

    @SuppressLint("SetTextI18n")
    @Override
    public void updateLottiDetailData(LottiDetailEntity response) {
        if (mPresenter != null)
            mPresenter.loadRadiusImg(response.getImg(), ivThumb, ArmsUtils.getDimens(Utils.getApp(), R.dimen.x10));
        tvInfoName.setText(TextUtils.isEmpty(response.getName()) ? "" : response.getName());
        lottiName = response.getName();
        tvInfoPrice.setText(new SpanUtils().append("面值").setFontSize(ArmsUtils.getDimens(Utils.getApp(), R.dimen.x20))
                .append(" ¥" + (TextUtils.isEmpty(response.getPrice()) ? "0" : response.getPrice())).setFontSize(ArmsUtils.getDimens(Utils.getApp(), R.dimen.x24))
                .setForegroundColor(ArmsUtils.getColor(this, R.color.scratch_detail_rule_info_tip))
                .create());
        price = TextUtils.isEmpty(response.getPrice()) ? "0" : response.getPrice();
        tvInfoAward.setText(new SpanUtils().append("最高奖金: ").setFontSize(ArmsUtils.getDimens(Utils.getApp(), R.dimen.x20))
                .append(" ¥" + NumUtils.getMoneyDou(String.valueOf(response.getReward()))).setFontSize(ArmsUtils.getDimens(Utils.getApp(), R.dimen.x24))
                .setForegroundColor(ArmsUtils.getColor(this, R.color.scratch_detail_rule_info_tip))
                .create());
        tvSaleNum.setText(new SpanUtils().append("销量")
                .append(String.valueOf(response.getSales()))
                .setForegroundColor(ArmsUtils.getColor(this, R.color.scratch_detail_rule_info_tip))
                .append("张").create());
        tvPerPackage.setText(response.getStandard() + " 张/包");
        numPerPkg = response.getStandard();
        if (bigImg1 != null && bigImg1.size() != 0) bigImg1.clear();
        if (bigImg2 != null && bigImg2.size() != 0) bigImg2.clear();
        bigImg1.add(response.getFrontImg());
        bigImg1.add(response.getBackImg());
        bigImg2.add("http://img.zthuacai.com/file/icon/dj.png");
        if (loadService != null) loadService.showSuccess();
        if (mPresenter != null) mPresenter.getLotteryFunList();
    }

    private static final int MODE_ALL = 1;
    private static final int MODE_SINGLE = 2;
    private int currentMode = 2;

    private void switchListStatus(int mode) {
        currentMode = mode;
        if (loadServiceList != null) loadServiceList.showCallback(LoadingSmallCallback.class);
        long id = getIntent().getLongExtra(Constants.INTENT_TO_LOTTI_DETAIL, -1);
        switch (mode) {
            case MODE_ALL:
                tvBuyAll.setSelected(true);
                tvBuyOne.setSelected(false);
                if (mPresenter != null)
                    mPresenter.reqTicketListData(id, currentMode);
                break;
            case MODE_SINGLE:
                tvBuyAll.setSelected(false);
                tvBuyOne.setSelected(true);
                if (mPresenter != null)
                    mPresenter.reqTicketListData(id, currentMode);
                break;
        }
        justChangeBottomState();
    }

    private void setDef() {
        tvBuyAll.setSelected(false);
        tvBuyOne.setSelected(true);
        justChangeBottomState();
    }

    private void justChangeBottomState() {
        pauseTimer();
        letBottomIsEnable(false);
        clearCheck();
    }

    @OnClick({R.id.fl_scratch_detail_back, R.id.btn_scratch_detail_pay, R.id.tv_scratch_detail_buy_all,
            R.id.tv_scratch_detail_buy_one, R.id.btn_scratch_detail_djway, R.id.btn_scratch_detail_cpty,
            R.id.iv_scratch_detail_fun_expand, R.id.iv_scratch_detail_fun_shrink})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_scratch_detail_back:
                if (checkedTemp.size() != 0 && mPresenter != null) {
                    mPresenter.unLockLottiBack(checkedTemp, currentMode, ScratchDetailActivity.this);
                } else {
                    killMyself();
                }
                break;
            case R.id.tv_scratch_detail_buy_all:
                if (currentMode == MODE_ALL) return;
                if (checkedTemp.size() == 0) {
                    switchListStatus(MODE_ALL);
                } else {
                    if (mPresenter != null)
                        mPresenter.unLockLotti(checkedTemp, currentMode, true, ScratchDetailActivity.this);
                }
                break;
            case R.id.tv_scratch_detail_buy_one:
                if (currentMode == MODE_SINGLE) return;
                if (checkedTemp.size() == 0) {
                    switchListStatus(MODE_SINGLE);
                } else {
                    if (mPresenter != null)
                        mPresenter.unLockLotti(checkedTemp, currentMode, true, ScratchDetailActivity.this);
                }
                break;
            case R.id.btn_scratch_detail_pay:
                if (currentMode == MODE_ALL && (mPresenter == null || mPresenter.getPkgGoodsNum() == 0)) {
                    ToastUtils.showShort("彩票被抢光了,快去看看其他彩票~");
                    return;
                }
                if (currentMode == MODE_SINGLE && (mPresenter == null || mPresenter.getSingleGoodsNum() == 0)) {
                    ToastUtils.showShort("彩票被抢光了,快去看看其他彩票~");
                    return;
                }
                if (checkedTemp.size() == 0) {
                    loadShakeAnim();
                    ToastUtils.showShort("请至少选择一" + (currentMode == MODE_ALL ? "包" : "张"));
                    return;
                }
                if (UserUtils.isLoginOnly()) {
                    if (!checkCanSaveOr()) {
                        return;
                    }
                    saveOr();
                } else {
                    UserUtils.isBindAndLogin(UserUtils::isLogin, ScratchDetailActivity.this, ScratchDetailActivity.this);
                }
                break;
            case R.id.btn_scratch_detail_djway:
                openLotteryRules(bigImg2);
                break;
            case R.id.btn_scratch_detail_cpty:
                openLotteryRules(bigImg1);
                break;
            case R.id.iv_scratch_detail_fun_expand:
                if (isAnimExecuting) return;
                letExpandBtnShow(false);
                executeExpandAnim();
                break;
            case R.id.iv_scratch_detail_fun_shrink:
                if (isAnimExecuting) return;
                executeShrinkAnim();
                break;
        }
    }

    private boolean checkCanSaveOr() {
        if (currentMode == MODE_ALL && checkedTemp.size() * numPerPkg > 60) {
            ToastUtils.showShort("整包购买每次不能超过60张哦!");
            return false;
        }
        if (currentMode == MODE_SINGLE && checkedTemp.size() > 10) {
            ToastUtils.showShort("单张购买每次不能超过10张哦!");
            return false;
        }
        return true;
    }


    private void openLotteryRules(ArrayList<String> imgs) {
        Intent intent = new Intent(this, BigLottiImgActivity.class);
        intent.putExtra("big_imgs_pos", 0);
        intent.putExtra("big_imgs", imgs);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private boolean isPageShow = false;

    @Override
    protected void onResume() {
        super.onResume();
        setNewsListCanRun(true);
        if (!isPageShow) startNewsScroll();
        isPageShow = true;
        if (mPresenter != null) mPresenter.getLotteryMsgData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPageShow = false;
        setNewsListCanRun(false);
        stopNewsScroll();
    }

    private void openOrList() {
        if (mPresenter == null) return;
        if (mPresenter.getOrId() == 0) return;
        Intent intent = new Intent(this, HadPayActivity.class);
        intent.putExtra(Constants.INTENT_TO_LOTTI_ORDER_ID, mPresenter.getOrId());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this, (Pair<View, String>[]) null);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void loadShakeAnim() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.anim_btn_shake);
        btnPay.startAnimation(shake);
    }

    @SuppressLint("UseSparseArrays")
    private Map<Integer, String> checkedTemp = new HashMap<>();

    private class MyOnItemClickListener extends OnItemClickListener {
        @Override
        public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
            if (UserUtils.isLoginOnly()) {
                if (!isAnimExecuting) {
                    executeShrinkAnim();
                }
                switchItemCheckStatus(adapter, position, view);
            } else {
                UserUtils.isBindAndLogin(UserUtils::isLogin, ScratchDetailActivity.this, ScratchDetailActivity.this);
            }
        }
    }

    private void switchItemCheckStatus(BaseQuickAdapter adapter, int position, View view) {
        List data = adapter.getData();
        LottiListEntity entity = (LottiListEntity) data.get(position);
        String code = null;
        for (Map.Entry<Integer, String> entry : checkedTemp.entrySet()) {
            if (TextUtils.equals(String.valueOf(entity.getId()), entry.getValue())) {
                code = entry.getValue();
            }
        }
        if (TextUtils.isEmpty(code)) {
            if (checkIsChosenMore()) return;
            String id = null;
            if (currentMode == MODE_ALL) {
                id = entity.getCode();
            } else if (currentMode == MODE_SINGLE) {
                id = String.valueOf(entity.getId());
            }
            if (mPresenter != null)
                mPresenter.lockLotti(ScratchDetailActivity.this, id, currentMode, adapter, position, view);
        } else {
            String id = null;
            if (currentMode == MODE_ALL) {
                id = entity.getCode();
            } else if (currentMode == MODE_SINGLE) {
                id = String.valueOf(entity.getId());
            }
            if (mPresenter != null)
                mPresenter.unLockLotti(ScratchDetailActivity.this, id, currentMode, adapter, position, view);
        }

    }

    private boolean checkIsChosenMore() {
        if (currentMode == MODE_ALL && checkedTemp.size() * numPerPkg >= 60) {
            ToastUtils.showShort("整包购买每次不能超过60张哦!");
            return true;
        }
        if (currentMode == MODE_SINGLE && checkedTemp.size() >= 10) {
            ToastUtils.showShort("单张购买每次不能超过10张哦!");
            return true;
        }
        return false;
    }

    @Override
    public void onUnlockSuccess(boolean needSwitch, int mode) {
        if (!ActivityUtils.isActivityExistsInStack(getClass())) {
            return;
        }
        if (needSwitch) {
            //切换
            switchListStatus(mode == 1 ? MODE_SINGLE : MODE_ALL);
            Timber.i("解锁成功(左右切换) ---- 当前选中: %s", (mode == MODE_ALL ? "整包" : "单张"));
        } else {
            //倒计时结束
            long detailId = getIntent().getLongExtra(Constants.INTENT_TO_LOTTI_DETAIL, -1);
            if (loadServiceList != null)
                loadServiceList.showCallback(LoadingSmallCallback.class);
            if (mPresenter != null)
                mPresenter.reqTicketListData(detailId, currentMode);
            Timber.i("解锁成功(倒计时结束) ---- 当前选中: %s", (mode == MODE_ALL ? "整包" : "单张"));
        }
    }

    @Override
    public void onLockSuccess(BaseQuickAdapter adapter, int position, View view) {
        if (!ActivityUtils.isActivityExistsInStack(getClass())) {
            return;
        }
        if (adapter == null) return;
        if (view != null) view.setClickable(true);
        changeItemState(adapter, position);
        Timber.i("单张锁定成功 ---- 当前选中: %s", (currentMode == MODE_ALL ? "整包" : "单张"));
    }

    @Override
    public void onUnLockSingleSuccess(BaseQuickAdapter adapter, int position, View view) {
        if (!ActivityUtils.isActivityExistsInStack(getClass())) {
            return;
        }
        if (adapter == null) return;
        if (view != null) view.setClickable(true);
        changeItemState(adapter, position);
        Timber.i("单张解锁成功 ---- 当前选中: %s", (currentMode == MODE_ALL ? "整包" : "单张"));
    }

    public void changeItemState(BaseQuickAdapter adapter, int position) {
        if (adapter.getData().size() - 1 < position) return;
        LottiListEntity entity = (LottiListEntity) adapter.getData().get(position);
        if (entity.isCheck()) {
            entity.setCheck(false);
            checkedTemp.remove(position);
        } else {
            entity.setCheck(true);
            checkedTemp.put(position, String.valueOf(entity.getId()));
        }
        adapter.notifyItemChanged(position);
        if (checkedTemp.size() != 0) {
            totalTime = 3 * 60;
            letBottomIsEnable(true);
            startTimer();
        } else {
            noOneItemLocked();
        }
    }

    private void noOneItemLocked() {
        pauseTimer();
        clearCheck();
        letBottomIsEnable(false);
    }

    @Override
    public void onUnlockBackSuccess() {
        killMyself();
    }

    @Override
    public void clearCheck() {
        checkedTemp.clear();
    }


    private TimerTask timerTask;
    private boolean bStartTimer;
    private int totalTime = 3 * 60;

    protected void startTimer() {
        if (!bStartTimer) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    totalTime--;
                    if (totalTime < 0) totalTime = 0;
                    //设置文字
                    updateCount();
                    if (totalTime <= 0) {
                        onTimeFinish();
                    }
                }
            };
            if (timer != null) timer.schedule(timerTask, 1000, 1000);
            bStartTimer = true;
        }
    }

    private void letBottomIsEnable(boolean enable) {
        if (tvCountTime == null || tvSelected == null || btnPay == null) return;
        if (enable) {
            if (tvCountTime.getVisibility() == View.INVISIBLE)
                tvCountTime.setVisibility(View.VISIBLE);
            if (tvSelected.getVisibility() == View.INVISIBLE)
                tvSelected.setVisibility(View.VISIBLE);
            btnPay.setBackgroundResource(R.drawable.bg_btn_scratch_detail_pay);
        } else {
            tvCountTime.setText("");
            tvSelected.setText("");
            if (tvCountTime.getVisibility() == View.VISIBLE)
                tvCountTime.setVisibility(View.INVISIBLE);
            if (tvSelected.getVisibility() == View.VISIBLE)
                tvSelected.setVisibility(View.INVISIBLE);
            btnPay.setBackgroundResource(R.drawable.bg_btn_scratch_detail_pay_n);
        }
    }

    private void updateCount() {
        if (tvCountTime == null) return;
        if (!ActivityUtils.isActivityExistsInStack(ScratchDetailActivity.this.getClass())) {
            return;
        }
        tvCountTime.post(() -> {
            if (tvCountTime != null)
                tvCountTime.setText(new SpanUtils().append("请在").setForegroundColor(colorG)
                        .append("00: " + dealTimeNum(totalTime / 60) + ": " + dealTimeNum(totalTime % 60))
                        .setForegroundColor(colorR)
                        .append("内完成付款").setForegroundColor(colorG).create());
        });
        refreshBottomStatus();
    }

    private String dealTimeNum(int num) {
        if (num <= 9) {
            return "0" + num;
        }
        return String.valueOf(num);
    }

    private void onTimeFinish() {
        Timber.i("买票倒计时结束了~~~~");
        pauseTimer();
        if (tvCountTime != null) tvCountTime.post(() -> {
            letBottomIsEnable(false);
            if (checkedTemp.size() != 0 && mPresenter != null) {
                mPresenter.unLockLotti(checkedTemp, currentMode, false, ScratchDetailActivity.this);
            }
            clearCheck();
        });
    }

    private void pauseTimer() {
        Timber.i("停止买票倒计时");
        if (bStartTimer) {
            if (timerTask != null) timerTask.cancel();
            if (timer != null) timer.purge();
            bStartTimer = false;
            totalTime = 0;
        }
    }

    private void refreshBottomStatus() {
        if (!NumUtils.isInteger(price)) return;
        if (tvSelected != null)
            tvSelected.post(() -> {
                if (currentMode == MODE_ALL) {
                    tvSelected.setText(new SpanUtils().append("已选 ").setForegroundColor(colorG)
                            .append(String.valueOf(checkedTemp.size())).setForegroundColor(colorR)
                            .append(" 包 " + (TextUtils.isEmpty(lottiName) ? "" : lottiName) + "   需支付 ").setForegroundColor(colorG)
                            .append("¥" + checkedTemp.size() * Integer.parseInt(price) * numPerPkg).setForegroundColor(colorR)
                            .create());
                } else if (currentMode == MODE_SINGLE) {
                    tvSelected.setText(new SpanUtils().append("已选 ").setForegroundColor(colorG)
                            .append(String.valueOf(checkedTemp.size())).setForegroundColor(colorR)
                            .append(" 张 " + (TextUtils.isEmpty(lottiName) ? "" : lottiName) + "   需支付 ").setForegroundColor(colorG)
                            .append("¥" + checkedTemp.size() * Integer.parseInt(price)).setForegroundColor(colorR)
                            .create());
                }
            });
    }

    private void saveOr() {
        //停止计时
        pauseTimer();
        long id = getIntent().getLongExtra(Constants.INTENT_TO_LOTTI_DETAIL, -1);
        if (mPresenter != null) {
            String money = "";
            if (currentMode == MODE_ALL) {
                money = String.valueOf(checkedTemp.size() * Integer.parseInt(price) * numPerPkg);
            } else if (currentMode == MODE_SINGLE) {
                money = String.valueOf(checkedTemp.size() * Integer.parseInt(price));
            }
            mPresenter.saveOr(currentMode, id, money, checkedTemp, btnPay, this);
        }
        Timber.i("买票下单");
    }

    @Override
    public void createOrFail() {
        totalTime = 3 * 60;
        letBottomIsEnable(true);
        startTimer();
    }

    @Subscriber(tag = EventBusTags.EVENT_PAY_SUCCESS, mode = ThreadMode.MAIN)
    private void onPaySuccess(EventUpdateUser updateUser) {
        openOrList();
        afterPay();
        Timber.i("支付成功: 订单号: %s", mPresenter == null ? "" : mPresenter.getOrId());
    }

    @Subscriber(tag = EventBusTags.EVENT_PAY_CANCEL, mode = ThreadMode.MAIN)
    private void onPayCancel(EventUpdateUser updateUser) {
        afterPay();
        Timber.i("支付取消: 订单号: %s", mPresenter == null ? "" : mPresenter.getOrId());
    }

    private void afterPay() {
        pauseTimer();
        letBottomIsEnable(false);
        clearCheck();
        if (mPresenter != null) {
            mPresenter.setOrId(0);
            if (loadServiceList != null)
                loadServiceList.showCallback(LoadingSmallCallback.class);
            long detailId = getIntent().getLongExtra(Constants.INTENT_TO_LOTTI_DETAIL, -1);
            mPresenter.reqTicketListData(detailId, currentMode);
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (checkedTemp.size() != 0 && mPresenter != null) {
            mPresenter.unLockLottiBack(checkedTemp, currentMode, ScratchDetailActivity.this);
        } else {
            super.onBackPressedSupport();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseTimer();
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void initFunRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvFun.setLayoutManager(layoutManager);
        rvFun.addItemDecoration(new LotteryFunDecoration());
        rvFun.addOnItemTouchListener(new FunOnItemClickListener());
    }

    private class FunOnItemClickListener extends OnItemClickListener {
        @Override
        public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
            if (!isLoadDataSuccess) return;
            if (isAnimExecuting) return;
            if (UserUtils.isLoginOnly()) {
                if (view != null) {
                    view.postDelayed(() -> {
                        LotteryFunEntity funEntity = (LotteryFunEntity) adapter.getData().get(position);
                        openLotteryFunWebPage(funEntity.getUrl(), funEntity.getId());
                    }, 100);
                }
            } else {
                UserUtils.isBindAndLogin(UserUtils::isLogin, ScratchDetailActivity.this, ScratchDetailActivity.this);
            }
        }
    }

    private void openLotteryFunWebPage(String url, long id) {
        if (TextUtils.isEmpty(url) || !RegexUtils.isURL(url) || TextUtils.equals(price, "0")) {
            ToastUtils.showShort("暂未开放");
            return;
        }
        Intent intent = new Intent(this, LHWebActivity.class);
        intent.putExtra(Constants.INTENT_REMOTE_WEB_URL, getDealH5Url(url, id));
        intent.putExtra(Constants.INTENT_REMOTE_WEB_LOAD, 1);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    public String getDealH5Url(String url, long id) {
        long lotteryId = getIntent().getLongExtra(Constants.INTENT_TO_LOTTI_DETAIL, -1);
        if (TextUtils.isEmpty(url)) {
            return "";
        } else if (!TextUtils.isEmpty(url) && url.contains("?")) {
            return url.endsWith("?")
                    ? url.substring(0, url.length() - 1) + "?token=" + UserUtils.getToken() + "&deviceNo=" + UserUtils.getSNNum()
                    : url + "&token=" + UserUtils.getToken() + "&deviceNo=" + UserUtils.getSNNum()
                    + "&lotteryId=" + lotteryId + "&amount=" + price + "&typeId=" + id;
        } else {
            return url + "?token=" + UserUtils.getToken() + "&deviceNo=" + UserUtils.getSNNum()
                    + "&lotteryId=" + lotteryId + "&amount=" + price + "&typeId=" + id;
        }
    }

    private boolean isAnimExecuting;
    private boolean hasExpanded;

    private void executeExpandAnim() {
        if (hasExpanded) {
            return;
        }
        if (mPresenter != null) mPresenter.getLotteryFunList();
        ViewCompat.animate(rlShrink)
                .translationX(0)
                .setDuration(1000)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        isAnimExecuting = true;
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        isAnimExecuting = false;
                        hasExpanded = true;
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        isAnimExecuting = false;
                    }
                })
                .start();
    }

    private void executeShrinkAnim() {
        if (!hasExpanded) {
            return;
        }
        int translateX = ArmsUtils.getDimens(this, R.dimen.x1547);
        ViewCompat.animate(rlShrink)
                .translationX(-translateX)
                .setDuration(1000)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        isAnimExecuting = true;
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        isAnimExecuting = false;
                        hasExpanded = false;
                        letExpandBtnShow(true);
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        isAnimExecuting = false;
                    }
                })
                .start();
    }

    private void executeShrinkAnim(long duration) {
        int translateX = ArmsUtils.getDimens(this, R.dimen.x1547);
        ViewCompat.animate(rlShrink)
                .translationX(-translateX)
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(View view) {
                        isAnimExecuting = true;
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        isAnimExecuting = false;
                        hasExpanded = false;
                    }

                    @Override
                    public void onAnimationCancel(View view) {
                        isAnimExecuting = false;
                    }
                })
                .start();
    }

    @Subscriber(tag = EventBusTags.EVENT_ON_LOTTERY_PAGE_CLOSE, mode = ThreadMode.MAIN)
    private void onH5PageClose(EventLH5PageClose pageClose) {
        if (loadServiceList != null)
            loadServiceList.showCallback(LoadingSmallCallback.class);
        long detailId = getIntent().getLongExtra(Constants.INTENT_TO_LOTTI_DETAIL, -1);
        if (mPresenter != null) mPresenter.reqTicketListData(detailId, currentMode);
    }

    private void letExpandBtnShow(boolean isShow) {
        if (ivExpand == null) return;
        if (isShow) {
            ivExpand.setVisibility(View.VISIBLE);
            ivExpand.setClickable(true);
            ivExpand.setFocusable(true);
            ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(ivExpand).alpha(1).setDuration(300);
            animatorCompat.setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {

                }

                @Override
                public void onAnimationEnd(View view) {
                    if (ivExpand != null) ivExpand.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(View view) {

                }
            });
            animatorCompat.start();
        } else {
            ivExpand.setClickable(false);
            ivExpand.setFocusable(false);
            ViewPropertyAnimatorCompat animatorCompat = ViewCompat.animate(ivExpand).alpha(0).setDuration(100);
            animatorCompat.setListener(new ViewPropertyAnimatorListener() {
                @Override
                public void onAnimationStart(View view) {

                }

                @Override
                public void onAnimationEnd(View view) {
                    if (ivExpand != null) ivExpand.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(View view) {

                }
            });
            animatorCompat.start();
        }
    }

    @Override
    public void setLotteryFunAdapter(LotteryFunListAdapter adapterFun) {
        rvFun.setAdapter(adapterFun);
    }

    @Override
    public void setLotteryMsgAdapter(LotterMsgAdapter adapterMsg) {
        rvMsg.setAdapter(adapterMsg);
        rvMsg.setPagerCount(adapterMsg.getData().size());
    }

    @Override
    public void onLotteryMsgRefreshed(List<String> response) {
        rvMsg.setPagerCount(response.size());
        if (response.size() > 0) {
            startNewsScroll();
        } else {
            stopNewsScroll();
        }
    }

    private void startNewsScroll() {
        if (rvMsg != null) rvMsg.start();
    }

    private void stopNewsScroll() {
        if (rvMsg != null) rvMsg.stop();
    }

    private void setNewsListCanRun(boolean isCanRun) {
        if (rvMsg != null) {
            rvMsg.setCanRun(isCanRun);
        }
    }
}
