package com.zhongti.huacailauncher.app.base;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.jess.arms.base.BaseActivity;
import com.jess.arms.di.component.AppComponent;
import com.jess.arms.mvp.IPresenter;
import com.jess.arms.utils.ArmsUtils;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.app.utils.NoHandleTimer;
import com.zhongti.huacailauncher.app.utils.UserUtils;
import com.zhongti.huacailauncher.model.event.EventForceLogout;
import com.zhongti.huacailauncher.model.event.EventUpdateUser;
import com.zhongti.huacailauncher.ui.personal.activity.ForceExitLoginActivity;
import com.zhongti.huacailauncher.utils.code.Utils;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;
import org.simple.eventbus.ThreadMode;

import me.yokeyword.fragmentation.ExtraTransaction;
import me.yokeyword.fragmentation.ISupportActivity;
import me.yokeyword.fragmentation.ISupportFragment;
import me.yokeyword.fragmentation.SupportActivityDelegate;
import me.yokeyword.fragmentation.SupportHelper;
import me.yokeyword.fragmentation.anim.FragmentAnimator;
import timber.log.Timber;

/**
 * 基础Activity 配合{@link BaseSupportFragment 使用}
 * 包含了框架BaseActivity的特性 同时适合一个 Activity 多 Fragment 的使用场景
 * <p>
 * Created by shuheming on 2018/1/9 0009.
 */

public abstract class BaseSupportActivity<P extends IPresenter> extends BaseActivity<P> implements ISupportActivity {
    final SupportActivityDelegate mDelegate = new SupportActivityDelegate(this);

    @Override
    public SupportActivityDelegate getSupportDelegate() {
        return mDelegate;
    }

    /**
     * Perform some extra transactions.
     * 额外的事务：自定义Tag，添加SharedElement动画，操作非回退栈Fragment
     */
    @Override
    public ExtraTransaction extraTransaction() {
        return mDelegate.extraTransaction();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mDelegate.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDelegate.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        mDelegate.onDestroy();
        //add by shuheming 退出的时候停止显示吐司
        super.onDestroy();
    }

    /**
     * Note： return mDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            //手指按下的时候停止计时
//            Timber.e("手指按下");
            NoHandleTimer.pauseTimer();
        } else if (ev.getAction() == MotionEvent.ACTION_UP) {
            //手指抬起的时候开始及时
            Timber.e("手指抬起");
            //登录且栈顶不是强制退出页
            if (UserUtils.isLoginOnly() && filterClass() && !NoHandleTimer.isIsRunning())
                NoHandleTimer.startTimer();
        }
        return mDelegate.dispatchTouchEvent(ev) || super.dispatchTouchEvent(ev);
    }

    private boolean filterClass() {
        if (Utils.getApp() == null) return true;
        AppComponent appComponent = ArmsUtils.obtainAppComponentFromContext(Utils.getApp());
        if (appComponent == null) return true;
        if (appComponent.appManager() == null) return true;
        if (appComponent.appManager().getTopActivity() == null) return true;
        if (appComponent.appManager().getTopActivity().getComponentName() == null) return true;
        String topClassName = ArmsUtils.obtainAppComponentFromContext(Utils.getApp()).appManager().getTopActivity().getComponentName().getClassName();
        return TextUtils.isEmpty(topClassName) || !(TextUtils.equals(topClassName, "com.zhongti.huacailauncher.ui.personal.activity.ForceExitLoginActivity"));
    }


    /**
     * 不建议复写该方法,请使用 {@link #onBackPressedSupport} 代替
     */
    @Override
    final public void onBackPressed() {
        mDelegate.onBackPressed();
    }

    /**
     * 该方法回调时机为,Activity回退栈内Fragment的数量 小于等于1 时,默认finish Activity
     * 请尽量复写该方法,避免复写onBackPress(),以保证SupportFragment内的onBackPressedSupport()回退事件正常执行
     */
    @Override
    public void onBackPressedSupport() {
        mDelegate.onBackPressedSupport();
    }

    /**
     * 获取设置的全局动画 copy
     *
     * @return FragmentAnimator
     */
    @Override
    public FragmentAnimator getFragmentAnimator() {
        return mDelegate.getFragmentAnimator();
    }

    /**
     * Set all fragments animation.
     * 设置Fragment内的全局动画
     */
    @Override
    public void setFragmentAnimator(FragmentAnimator fragmentAnimator) {
        mDelegate.setFragmentAnimator(fragmentAnimator);
    }

    /**
     * Set all fragments animation.
     * 构建Fragment转场动画
     * <p/>
     * 如果是在Activity内实现,则构建的是Activity内所有Fragment的转场动画,
     * 如果是在Fragment内实现,则构建的是该Fragment的转场动画,此时优先级 > Activity的onCreateFragmentAnimator()
     *
     * @return FragmentAnimator对象
     */
    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return mDelegate.onCreateFragmentAnimator();
    }

    @Override
    public void post(Runnable runnable) {
        mDelegate.post(runnable);
    }

    //****************************************以下为可选方法(Optional methods)******************************************************

    /**
     * 加载根Fragment, 即Activity内的第一个Fragment 或 Fragment内的第一个子Fragment
     *
     * @param containerId 容器id
     * @param toFragment  目标Fragment
     */
    public void loadRootFragment(int containerId, @NonNull ISupportFragment toFragment) {
        mDelegate.loadRootFragment(containerId, toFragment);
    }

    public void loadRootFragment(int containerId, ISupportFragment toFragment, boolean addToBackStack, boolean allowAnimation) {
        mDelegate.loadRootFragment(containerId, toFragment, addToBackStack, allowAnimation);
    }

    /**
     * 加载多个同级根Fragment,类似Wechat, QQ主页的场景
     */
    public void loadMultipleRootFragment(int containerId, int showPosition, ISupportFragment... toFragments) {
        mDelegate.loadMultipleRootFragment(containerId, showPosition, toFragments);
    }

    /**
     * show一个Fragment,hide其他同栈所有Fragment
     * 使用该方法时，要确保同级栈内无多余的Fragment,(只有通过loadMultipleRootFragment()载入的Fragment)
     * <p>
     * 建议使用更明确的{@link #showHideFragment(ISupportFragment, ISupportFragment)}
     *
     * @param showFragment 需要show的Fragment
     */
    public void showHideFragment(ISupportFragment showFragment) {
        mDelegate.showHideFragment(showFragment);
    }

    /**
     * show一个Fragment,hide一个Fragment ; 主要用于类似微信主页那种 切换tab的情况
     */
    public void showHideFragment(ISupportFragment showFragment, ISupportFragment hideFragment) {
        mDelegate.showHideFragment(showFragment, hideFragment);
    }

    /**
     * It is recommended to use {@link BaseSupportFragment#start(ISupportFragment)}.
     */
    public void start(ISupportFragment toFragment) {
        mDelegate.start(toFragment);
    }

    /**
     * It is recommended to use {@link BaseSupportFragment#start(ISupportFragment, int)}.
     *
     * @param launchMode Similar to Activity's LaunchMode.
     */
    public void start(ISupportFragment toFragment, @ISupportFragment.LaunchMode int launchMode) {
        mDelegate.start(toFragment, launchMode);
    }

    /**
     * It is recommended to use {@link BaseSupportFragment#startForResult(ISupportFragment, int)}.
     * Launch an fragment for which you would like a result when it poped.
     */
    public void startForResult(ISupportFragment toFragment, int requestCode) {
        mDelegate.startForResult(toFragment, requestCode);
    }

    /**
     * It is recommended to use {@link BaseSupportFragment#startWithPop(ISupportFragment)}.
     * Start the target Fragment and pop itself
     */
    public void startWithPop(ISupportFragment toFragment) {
        mDelegate.startWithPop(toFragment);
    }

    /**
     * It is recommended to use {@link BaseSupportFragment#replaceFragment(ISupportFragment, boolean)}.
     */
    public void replaceFragment(ISupportFragment toFragment, boolean addToBackStack) {
        mDelegate.replaceFragment(toFragment, addToBackStack);
    }

    /**
     * Pop the fragment.
     */
    public void pop() {
        mDelegate.pop();
    }

    /**
     * Pop the last fragment transition from the manager's fragment
     * back stack.
     * <p>
     * 出栈到目标fragment
     *
     * @param targetFragmentClass   目标fragment
     * @param includeTargetFragment 是否包含该fragment
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment);
    }

    /**
     * If you want to begin another FragmentTransaction immediately after popTo(), use this method.
     * 如果你想在出栈后, 立刻进行FragmentTransaction操作，请使用该方法
     */
    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable);
    }

    public void popTo(Class<?> targetFragmentClass, boolean includeTargetFragment, Runnable afterPopTransactionRunnable, int popAnim) {
        mDelegate.popTo(targetFragmentClass, includeTargetFragment, afterPopTransactionRunnable, popAnim);
    }

    /**
     * 当Fragment根布局 没有 设定background属性时,
     * Fragmentation默认使用Theme的android:windowbackground作为Fragment的背景,
     * 可以通过该方法改变其内所有Fragment的默认背景。
     */
    public void setDefaultFragmentBackground(@DrawableRes int backgroundRes) {
        mDelegate.setDefaultFragmentBackground(backgroundRes);
    }

    /**
     * 得到位于栈顶Fragment
     */
    public ISupportFragment getTopFragment() {
        return SupportHelper.getTopFragment(getSupportFragmentManager());
    }

    /**
     * 获取栈内的fragment对象
     */
    public <T extends ISupportFragment> T findFragment(Class<T> fragmentClass) {
        return SupportHelper.findFragment(getSupportFragmentManager(), fragmentClass);
    }

    /**
     * 强制退出登录
     */
    @Subscriber(tag = EventBusTags.EVENT_FORCE_LOGOUT_TIME_UP, mode = ThreadMode.MAIN)
    private void onForceExitLogin(EventForceLogout forceLogout) {
        UserUtils.logOut();
        try {
            Activity topActivity = ArmsUtils.obtainAppComponentFromContext(this).appManager().getTopActivity();
            if (topActivity != this) return;
            Intent intent = new Intent(BaseSupportActivity.this, ForceExitLoginActivity.class);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                ActivityOptions optionsCompat = ActivityOptions.makeSceneTransitionAnimation(this);
                startActivity(intent, optionsCompat.toBundle());
            } else {
                startActivity(intent);
            }
        } catch (Exception e) {
            //do nothing
        }
        EventBus.getDefault().post(new EventUpdateUser(null), EventBusTags.EVENT_FORCE_LOGIN_OUT);
        //停止计时
        NoHandleTimer.pauseTimer();
        //检查sdk服务是否关闭
        UserUtils.checkSdkServiceIsNice();
    }
}
