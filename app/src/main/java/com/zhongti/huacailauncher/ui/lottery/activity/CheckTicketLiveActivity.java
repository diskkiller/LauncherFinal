package com.zhongti.huacailauncher.ui.lottery.activity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
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
import com.zhongti.huacailauncher.app.utils.DataReqHelper;
import com.zhongti.huacailauncher.contract.LiveContract;
import com.zhongti.huacailauncher.di.component.DaggerLiveComponent;
import com.zhongti.huacailauncher.di.module.LiveModule;
import com.zhongti.huacailauncher.model.entity.CLiveCodeListEntity;
import com.zhongti.huacailauncher.model.event.EventCancelCTSuccess;
import com.zhongti.huacailauncher.model.event.EventLadderTimeUp;
import com.zhongti.huacailauncher.presenter.LivePresenter;
import com.zhongti.huacailauncher.ui.lottery.adapter.CheckTicketLiveAdapter;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.SizeUtils;
import com.zhongti.huacailauncher.utils.code.SpanUtils;
import com.zhongti.huacailauncher.utils.code.StringUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhongti.huacailauncher.widget.bamUI.BamImageView;
import com.zhongti.huacailauncher.widget.errcall.EmptyCallback;
import com.zhongti.huacailauncher.widget.errcall.ErrorCallback;
import com.zhongti.huacailauncher.widget.errcall.LoadingCallback;
import com.zhongti.huacailauncher.widget.errcall.LoadingSmallCallback;
import com.zhongti.huacailauncher.widget.errcall.TransCallBack;
import com.zhongti.huacailauncher.widget.recycler.LiveItemDecoration;

import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import timber.log.Timber;

import static com.jess.arms.utils.Preconditions.checkNotNull;

public class CheckTicketLiveActivity extends BaseSupportActivity<LivePresenter> implements LiveContract.View {

    @BindView(R.id.rl_check_ticket_live_root)
    RelativeLayout rootView;
    @BindView(R.id.tv_check_ticket_live_area)
    TextView tvArea;
    @BindView(R.id.surface_check_ticket_live)
    SurfaceView mSurfaceView;
    @BindView(R.id.tv_check_ticket_live_f_status)
    TextView tvFStatus;
    @BindView(R.id.tv_check_ticket_live_f_name)
    TextView tvFName;
    @BindView(R.id.tv_check_ticket_live_f_code)
    TextView tvFCode;
    @BindView(R.id.tv_check_ticket_live_f_price)
    TextView tvFPrice;
    @BindView(R.id.refresh_check_ticket_live)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.rv_check_ticket_live_list)
    RecyclerView recycler;
    @BindView(R.id.tv_check_ticket_live_wait)
    TextView tvWait;
    @BindView(R.id.iv_check_ticket_live_cancel)
    BamImageView ivCancel;
    private AliVcMediaPlayer mPlayer;
    private boolean mMute = false;
    private String mUrl;
    private LoadService loadService;
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
        if (hasFocus) BarUtils.hideSystemUI(this);
    }

    @Override
    public void setupActivityComponent(@NonNull AppComponent appComponent) {
        DaggerLiveComponent //如找不到该类,请编译一下项目
                .builder()
                .appComponent(appComponent)
                .liveModule(new LiveModule(this))
                .build()
                .inject(this);
    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_check_ticket_live; //如果你不需要框架帮你设置 setContentView(id) 需要自行设置,请返回 0
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initSurfaceVeiw();
        initVodPlayer();
        initRetryView();
        initRecyclerView();

        //请求数据
        if (rootView != null) {
            rootView.postDelayed(() -> {
                if (loadService != null) loadService.showCallback(LoadingCallback.class);
                if (loadServiceList != null)
                    loadServiceList.showCallback(LoadingSmallCallback.class);
                if (mPresenter != null) {
                    String code = getIntent().getStringExtra(Constants.INTENT_CHECK_TICKET_SURE_CODE);
                    mPresenter.getPlayUrl(code);
                }
            }, 500);
        }
    }

    private void initRetryView() {
        loadService = LoadSir.getDefault().register(rootView, (Callback.OnReloadListener) v -> {
            if (!NetworkUtils.isConnected()) {
                netWorkUnavailable();
                return;
            }
            loadService.showCallback(LoadingCallback.class);
            if (mPresenter != null) {
                String code = getIntent().getStringExtra(Constants.INTENT_CHECK_TICKET_SURE_CODE);
                mPresenter.getPlayUrl(code);
            }
        });

        LoadSir loadSir = new LoadSir.Builder()
                .addCallback(new LoadingSmallCallback())
                .addCallback(new EmptyCallback())
                .addCallback(new ErrorCallback())
                .addCallback(new TransCallBack())
                .build();
        loadServiceList = loadSir.register(refreshLayout, (Callback.OnReloadListener) v -> {
            if (loadServiceList.getCurrentCallback() == EmptyCallback.class) return;
            loadServiceList.showCallback(LoadingSmallCallback.class);
            if (mPresenter != null) {
                mPresenter.getCheckDetails(null, false, true);
            }
        });

        loadService.setCallBack(LoadingCallback.class, (context, view) -> {
            LottieAnimationView lottieAnimationView = view.findViewById(R.id.place_holder_loading);
            lottieAnimationView.setAnimation("LottieAnim/lottery_loading.json");
            TextView tvLoad = view.findViewById(R.id.tv_place_holder_loading);
            tvLoad.setVisibility(View.VISIBLE);
            tvLoad.setText("正在努力加载...");
        });

        loadServiceList.setCallBack(LoadingSmallCallback.class, (context, view) -> {
            LottieAnimationView lottieAnimationView = view.findViewById(R.id.place_holder_loading);
            lottieAnimationView.setAnimation("LottieAnim/lottery_loading.json");
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lottieAnimationView.getLayoutParams();
            layoutParams.width = SizeUtils.dp2px(240);
            layoutParams.height = SizeUtils.dp2px(240);
            TextView tvLoad = view.findViewById(R.id.tv_place_holder_loading);
            tvLoad.setVisibility(View.VISIBLE);
            tvLoad.setText("正在努力加载...");
        });

        loadServiceList.setCallBack(EmptyCallback.class, (context, view) -> {
            TextView tvEmpty = view.findViewById(R.id.tv_call_empty);
            LinearLayout llRoot = view.findViewById(R.id.ll_empty);
            llRoot.setBackgroundColor(Color.WHITE);
            tvEmpty.setTextColor(ArmsUtils.getColor(Utils.getApp(), R.color.main_yellow));
            tvEmpty.setText("当前没有需要待验的票");
        });

        loadService.showCallback(TransCallBack.class);
    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(manager);
        recycler.addItemDecoration(new LiveItemDecoration(ArmsUtils.getColor(Utils.getApp(), R.color.black5)));
    }

    @Override
    public void netWorkUnavailable() {
        if (loadService != null) {
            loadService.setCallBack(ErrorCallback.class, (context, view) -> {
                TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                tvTips.setText(R.string.net_unavailable);
            });
            loadService.showCallback(ErrorCallback.class);
        }
    }

    @Override
    public void onLoadCheckDetailErr(boolean isFirst, boolean isPositive) {
        if (isFirst && loadService != null) {
            loadService.setCallBack(ErrorCallback.class, (context, view) -> {
                TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                tvTips.setText(R.string.data_null_and_retry);
            });
            loadService.showCallback(ErrorCallback.class);
        }
        if (isPositive && loadServiceList != null) {
            loadServiceList.setCallBack(ErrorCallback.class, (context, view) -> {
                TextView tvTips = view.findViewById(R.id.tv_err_call_text);
                tvTips.setText(R.string.data_null_and_retry);
            });
            loadServiceList.showCallback(ErrorCallback.class);
        }
    }

    @Override
    public void setDetailListAdapter(CheckTicketLiveAdapter adapterDetail) {
        if (recycler != null) recycler.setAdapter(adapterDetail);
    }

    @Override
    public void setDetailData(String url, int total, boolean first, boolean isPositive, int isExist) {
        if (!TextUtils.isEmpty(url) && first && isPositive) {
            if (loadService != null) loadService.showSuccess();
            mUrl = url;
            prePlay();
        }
        if (isExist == 1) {
            tvWait.setText(new SpanUtils().append("您前方还有 ").setForegroundColor(ArmsUtils.getColor(Utils.getApp(), R.color.black3))
                    .append(total == 0 ? "0" : String.valueOf(total - 1)).setForegroundColor(ArmsUtils.getColor(Utils.getApp(), R.color.main_yellow))
                    .append(" 人等待验票").setForegroundColor(ArmsUtils.getColor(Utils.getApp(), R.color.black3)).create());
            if (ivCancel != null) ivCancel.setVisibility(View.VISIBLE);
        } else if (isExist == 2) {
            tvWait.setText("");
            if (ivCancel != null) ivCancel.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onLoadListSuccess(CLiveCodeListEntity response) {
        CLiveCodeListEntity.TicketListBean ticketListBean = response.getTicketList().get(0);
        tvFStatus.setText("正在验票");
        tvFName.setText("种类：" + (TextUtils.isEmpty(ticketListBean.getSort()) ? "" : ticketListBean.getSort()));
        tvFCode.setText("票号：" + (TextUtils.isEmpty(ticketListBean.getCode()) ? "" : StringUtils.getTickSingleCode(ticketListBean.getCode(), 13)));
        tvFPrice.setText("面值：" + (TextUtils.isEmpty(ticketListBean.getMoney()) ? "" : ticketListBean.getMoney()) + "元");
        if (loadServiceList != null) loadServiceList.showSuccess();
        startTask();
    }

    private void startTask() {
        DataReqHelper.startTask(EventBusTags.EVENT_CHECK_TICKET_LIST);
    }

    private long testIndex;

    @Subscriber(tag = EventBusTags.EVENT_CHECK_TICKET_LIST, mode = ThreadMode.MAIN)
    private void onLoopTaskUp(EventLadderTimeUp data) {
        testIndex++;
        Timber.i("轮询: " + testIndex + "次");
        if (mPresenter != null) mPresenter.getCheckDetails(null, false, false);
    }

    @Override
    public void onLoadDetailEmpty() {
        if (loadServiceList != null) loadServiceList.showCallback(EmptyCallback.class);
        tvFStatus.setText("当前没有需要待验的票");
        tvFName.setText("");
        tvFCode.setText("");
        tvFPrice.setText("");
        //无排队
        tvWait.setText("");
        if (ivCancel != null) ivCancel.setVisibility(View.GONE);
    }


    @Subscriber(tag = EventBusTags.EVENT_CANCEL_CT_SUCCESS, mode = ThreadMode.MAIN)
    private void onCancelCtSuccess(EventCancelCTSuccess eventCancelCTSuccess) {
        if (ivCancel != null) ivCancel.setVisibility(View.GONE);
        if (tvWait != null) tvWait.setText("");
        DataReqHelper.pauseTask();
        killMyself();
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

    @OnClick({R.id.fl_check_ticket_live_jb, R.id.iv_check_ticket_live_cancel, R.id.fl_check_ticket_live_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_check_ticket_live_jb:
                openJuBaoPage();
                break;
            case R.id.iv_check_ticket_live_cancel:
                openExitPage();
                break;
            case R.id.fl_check_ticket_live_close:
                killMyself();
                break;
        }
    }

    private void openJuBaoPage() {
        Intent intent = new Intent(this, CTLiveJBActivity.class);
        String code = getIntent().getStringExtra(Constants.INTENT_CHECK_TICKET_SURE_CODE);
        intent.putExtra(Constants.INTENT_CHECK_TICKET_SURE_CODE, code);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }


    private void openExitPage() {
        Intent intent = new Intent(this, CancelCheckTicketActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
            startActivity(intent, optionsCompat.toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void initSurfaceVeiw() {
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
                holder.setKeepScreenOn(true);
                Timber.i("AlivcPlayer onSurfaceCreated.%s", mPlayer);
                if (mPlayer != null) {
                    mPlayer.setVideoSurface(mSurfaceView.getHolder().getSurface());
                }

                Timber.i("AlivcPlayeron SurfaceCreated over.");
            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                Timber.i("onSurfaceChanged is valid ? %s", holder.getSurface().isValid());
                if (mPlayer != null) {
                    mPlayer.setSurfaceChanged();
                }
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                Timber.i("onSurfaceDestroy.");
            }
        });
    }

    private void initVodPlayer() {
        mPlayer = new AliVcMediaPlayer(this, mSurfaceView);

        mPlayer.setPreparedListener(new MyPreparedListener(this));
        mPlayer.setFrameInfoListener(new MyFrameInfoListener(this));
        mPlayer.setErrorListener(new MyErrorListener(this));
        mPlayer.setCompletedListener(new MyPlayerCompletedListener(this));
        mPlayer.setSeekCompleteListener(new MySeekCompleteListener(this));
        mPlayer.setStoppedListener(new MyStoppedListener(this));
        mPlayer.enableNativeLog();
        /////
        mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        mPlayer.setRenderRotate(MediaPlayer.VideoRotate.ROTATE_0);
        mPlayer.setRenderMirrorMode(MediaPlayer.VideoMirrorMode.VIDEO_MIRROR_MODE_NONE);
    }

    private void prePlay() {
        Timber.i("准备请求码流");
        setMaxBufferDuration();

        replay();

        if (mMute) {
            mPlayer.setMuteMode(mMute);
        }
        Timber.i("开始请求播放");
    }


    private void setMaxBufferDuration() {
        if (mPlayer != null) {
            mPlayer.setMaxBufferDuration(8000);
        }
    }

    private void replay() {
        stop();
        startPlay();
    }

    private void stop() {
        if (mPlayer != null) {
            mPlayer.stop();
        }
    }

    private void startPlay() {
        if (mPlayer != null) {
            mPlayer.prepareAndPlay(mUrl);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        resume();
    }

    private void resume() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.play();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        savePlayerState();
    }

    private void savePlayerState() {
        if (mPlayer.isPlaying()) {
            pause();
        }
    }

    private void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onDestroy() {
        stop();
        destroy();
        super.onDestroy();
    }

    private void destroy() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.destroy();
        }
        DataReqHelper.pauseTask();
    }

    private static class MyPreparedListener implements MediaPlayer.MediaPlayerPreparedListener {

        private WeakReference<CheckTicketLiveActivity> activityWeakReference;

        MyPreparedListener(CheckTicketLiveActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onPrepared() {
            CheckTicketLiveActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onPrepared();
            }
        }

    }

    void onPrepared() {
        Timber.i("准备成功!");
        showVideoSizeInfo();
    }

    private void showVideoSizeInfo() {
        Timber.i("视频宽: %s", mPlayer.getVideoWidth());
        Timber.i("视频高: %s", mPlayer.getVideoHeight());
    }

    private static class MyFrameInfoListener implements MediaPlayer.MediaPlayerFrameInfoListener {

        private WeakReference<CheckTicketLiveActivity> activityWeakReference;

        MyFrameInfoListener(CheckTicketLiveActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onFrameInfoListener() {
            CheckTicketLiveActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onFrameInfoListener();
            }
        }
    }

    private void onFrameInfoListener() {
        Map<String, String> debugInfo = mPlayer.getAllDebugInfo();
        long createPts = 0;
        if (debugInfo.get("create_player") != null) {
            String time = debugInfo.get("create_player");
            createPts = (long) Double.parseDouble(time);
            Timber.i("create_player,,createPts: " + createPts + "--->播放创建成功");
        }
        if (debugInfo.get("open-url") != null) {
            String time = debugInfo.get("open-url");
            long openPts = (long) Double.parseDouble(time) + createPts;
            Timber.i("open-url,,openPts: " + openPts + "--->url请求成功");
        }
        if (debugInfo.get("find-stream") != null) {
            String time = debugInfo.get("find-stream");
            Timber.i("lfj0914" + "find-Stream time =" + time + " , createpts = " + createPts);
            long findPts = (long) Double.parseDouble(time) + createPts;
            Timber.i("find-stream,,findPts: " + findPts + "--->请求流成功");
        }
        if (debugInfo.get("open-stream") != null) {
            String time = debugInfo.get("open-stream");
            Timber.i("lfj0914 open-Stream time =" + time + " , createpts = " + createPts);
            long openPts = (long) Double.parseDouble(time) + createPts;
            Timber.i("open-stream,,openPts: " + openPts + "--->开始传输码流");
        }
    }

    private static class MyErrorListener implements MediaPlayer.MediaPlayerErrorListener {

        private WeakReference<CheckTicketLiveActivity> activityWeakReference;

        MyErrorListener(CheckTicketLiveActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(int i, String s) {
            CheckTicketLiveActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onError(i, s);
            }
        }
    }

    private void onError(int i, String msg) {
        ToastUtils.showMyShort("播放失败!", ArmsUtils.getDimens(Utils.getApp(), R.dimen.x500), 0);
        Timber.e("失败！！！！原因：%s", msg);
    }

    private static class MyPlayerCompletedListener implements MediaPlayer.MediaPlayerCompletedListener {

        private WeakReference<CheckTicketLiveActivity> activityWeakReference;

        public MyPlayerCompletedListener(CheckTicketLiveActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onCompleted() {
            CheckTicketLiveActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onCompleted();
            }
        }

    }

    private boolean isCompleted = false;

    private void onCompleted() {
        Timber.i("onCompleted--- ");
        isCompleted = true;
    }

    private static class MySeekCompleteListener implements MediaPlayer.MediaPlayerSeekCompleteListener {


        private WeakReference<CheckTicketLiveActivity> activityWeakReference;

        MySeekCompleteListener(CheckTicketLiveActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSeekCompleted() {
            CheckTicketLiveActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onSeekCompleted();
            }
        }
    }

    void onSeekCompleted() {
        Timber.i("seek结束");
    }

    private static class MyStoppedListener implements MediaPlayer.MediaPlayerStoppedListener {

        private WeakReference<CheckTicketLiveActivity> activityWeakReference;

        MyStoppedListener(CheckTicketLiveActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }


        @Override
        public void onStopped() {
            CheckTicketLiveActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onStopped();
            }
        }
    }

    void onStopped() {
        Timber.i("播放停止");
    }
}
