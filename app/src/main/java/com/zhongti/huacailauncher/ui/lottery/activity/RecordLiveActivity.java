package com.zhongti.huacailauncher.ui.lottery.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.utils.ArmsUtils;
import com.jess.arms.utils.RxLifecycleUtils;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportActivity;
import com.zhongti.huacailauncher.app.http.exception.ApiException;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObservable;
import com.zhongti.huacailauncher.app.http.observer.HttpRxObserver;
import com.zhongti.huacailauncher.model.api.service.LiveService;
import com.zhongti.huacailauncher.model.entity.RecordLiveUrlsEntity;
import com.zhongti.huacailauncher.utils.code.BarUtils;
import com.zhongti.huacailauncher.utils.code.NetworkUtils;
import com.zhongti.huacailauncher.utils.code.ToastUtils;
import com.zhongti.huacailauncher.utils.code.Utils;

import java.lang.ref.WeakReference;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

/**
 * Create by ShuHeMing on 18/7/10
 */
public class RecordLiveActivity extends BaseSupportActivity {

    @BindView(R.id.surface_record_live)
    SurfaceView mSurfaceView;
    private AliVcMediaPlayer mPlayer;
    private boolean mMute = false;
    private String mUrl;

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

    }

    @Override
    public int initView(@Nullable Bundle savedInstanceState) {
        return R.layout.activity_record_live;
    }

    @OnClick(R.id.fl_record_live_close)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        initSurfaceVeiw();
        initVodPlayer();
        setPlaySource();
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
                Log.d(TAG, "onSurfaceDestroy.");
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

    private void setPlaySource() {
        if (this.isFinishing()) {
            return;
        }
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort("未连接网络,请检查您的网络设置");
            return;
        }
        AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(Utils.getApp());
        HttpRxObservable.getObservable(appComponent.repositoryManager().obtainRetrofitService(LiveService.class)
                .getRecordLiveUrls())
                .compose(RxLifecycleUtils.bindToLifecycle(this))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new HttpRxObserver<RecordLiveUrlsEntity>() {
                    @Override
                    protected void onStart(Disposable d) {

                    }

                    @Override
                    protected void onError(ApiException e) {
                        ToastUtils.showShort(e.getMsg());
                    }

                    @Override
                    protected void onSuccess(RecordLiveUrlsEntity response) {
                        mUrl = response.getPullUrlStmp().trim();
                        prePlay();
                    }
                });
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
    }

    private static class MyPreparedListener implements MediaPlayer.MediaPlayerPreparedListener {

        private WeakReference<RecordLiveActivity> activityWeakReference;

        MyPreparedListener(RecordLiveActivity activity) {
            activityWeakReference = new WeakReference<RecordLiveActivity>(activity);
        }

        @Override
        public void onPrepared() {
            RecordLiveActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onPrepared();
            }
        }

    }

    void onPrepared() {
        showVideoSizeInfo();
    }

    private void showVideoSizeInfo() {
        Timber.i("视频宽: %s", mPlayer.getVideoWidth());
        Timber.i("视频高: %s", mPlayer.getVideoHeight());
    }

    private static class MyFrameInfoListener implements MediaPlayer.MediaPlayerFrameInfoListener {

        private WeakReference<RecordLiveActivity> activityWeakReference;

        MyFrameInfoListener(RecordLiveActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onFrameInfoListener() {
            RecordLiveActivity activity = activityWeakReference.get();
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
            Log.d(TAG + "lfj0914", "open-Stream time =" + time + " , createpts = " + createPts);
            long openPts = (long) Double.parseDouble(time) + createPts;
            Timber.i("open-stream,,openPts: " + openPts + "--->开始传输码流");
        }
    }

    private static class MyErrorListener implements MediaPlayer.MediaPlayerErrorListener {

        private WeakReference<RecordLiveActivity> activityWeakReference;

        MyErrorListener(RecordLiveActivity activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(int i, String s) {
            RecordLiveActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onError(i, s);
            }
        }
    }

    private void onError(int i, String msg) {
        ToastUtils.showShort("播放失败!");
        Timber.e("失败！！！！原因：%s", msg);
    }

    private static class MyPlayerCompletedListener implements MediaPlayer.MediaPlayerCompletedListener {

        private WeakReference<RecordLiveActivity> activityWeakReference;

        public MyPlayerCompletedListener(RecordLiveActivity activity) {
            activityWeakReference = new WeakReference<RecordLiveActivity>(activity);
        }

        @Override
        public void onCompleted() {
            RecordLiveActivity activity = activityWeakReference.get();
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


        private WeakReference<RecordLiveActivity> activityWeakReference;

        MySeekCompleteListener(RecordLiveActivity activity) {
            activityWeakReference = new WeakReference<RecordLiveActivity>(activity);
        }

        @Override
        public void onSeekCompleted() {
            RecordLiveActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onSeekCompleted();
            }
        }
    }

    void onSeekCompleted() {
        Timber.i("seek结束");
    }

    private static class MyStoppedListener implements MediaPlayer.MediaPlayerStoppedListener {

        private WeakReference<RecordLiveActivity> activityWeakReference;

        MyStoppedListener(RecordLiveActivity activity) {
            activityWeakReference = new WeakReference<RecordLiveActivity>(activity);
        }


        @Override
        public void onStopped() {
            RecordLiveActivity activity = activityWeakReference.get();
            if (activity != null) {
                activity.onStopped();
            }
        }
    }

    void onStopped() {
        Timber.i("播放停止");
    }

}
