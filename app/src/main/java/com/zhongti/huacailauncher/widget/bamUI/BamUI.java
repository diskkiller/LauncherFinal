package com.zhongti.huacailauncher.widget.bamUI;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.animation.OvershootInterpolator;

/**
 * 点击动画效果工具类
 * <p>
 * 由于是点击效果，所以使用这些效果的控件必须要设置OnClick事件！谨记！
 *
 * @author Bamboy
 */
public class BamUI {

    /**
     * 动画执行速度
     */
    public static int animSpeed = 300;

    /**
     * 旋转角度
     */
    private static float potationValue = 5.5f;

    /**
     * 变速器
     */
    private static OvershootInterpolator interpolator = new OvershootInterpolator();

    /**
     * 缩放比例
     */
    private static float scaleEnd = 0.88f;

    /**
     * 启动按压动画
     *
     * @param view   执行动画的View
     * @param superb 效果类型【true：华丽效果】【false：缩放效果】
     * @param x      触点X坐标
     * @param y      触点Y坐标
     * @return 动画执行顶点
     */
    public static int startAnimDown(View view, boolean superb, float x, float y) {
        int pivot = 0;
        float scaleNow;
        // 缩放效果
        if (false == superb) {
            pivot = 0;
            scaleNow = view.getScaleX();
            // 执行缩小动画【缩放效果】
            froBig_ToSmall(view, scaleNow);
            return pivot;
        }

        // 华丽效果
        int w = view.getWidth();
        int h = view.getHeight();

        if ((w / 5 * 2) < x && x < (w / 5 * 3) && (h / 5 * 2) < y && y < (h / 5 * 3)) {
            pivot = 0;
        } else if (x < w / 2 && y < h / 2) { // 第一象限
            if (x / (w / 2) > y / (h / 2)) {
                pivot = 1;
            } else {
                pivot = 4;
            }
        } else if (x < w / 2 && y >= h / 2) { // 第四象限
            if ((w - x) / (w / 2) > y / (h / 2)) {
                pivot = 4;
            } else {
                pivot = 3;
            }
        } else if (x >= w / 2 && y >= h / 2) { // 第三象限
            if ((w - x) / (w / 2) > (h - y) / (h / 2)) {
                pivot = 3;
            } else {
                pivot = 2;
            }
        } else { // 第二象限
            if (x / (w / 2) > (h - y) / (h / 2)) {
                pivot = 2;
            } else {
                pivot = 1;
            }
        }

        String anim = "";
        switch (pivot) {
            case 0:
                view.setPivotX(w / 2);
                view.setPivotY(h / 2);
                scaleNow = view.getScaleX();
                // 执行缩小动画【缩放效果】
                froBig_ToSmall(view, scaleNow);
                return pivot;
            case 1:
                anim = "rotationX";
                view.setPivotX(w / 2);
                view.setPivotY(h);
                break;
            case 2:
                anim = "rotationY";
                view.setPivotX(0);
                view.setPivotY(h / 2);
                break;
            case 3:
                anim = "rotationX";
                view.setPivotX(w / 2);
                view.setPivotY(0);
                break;
            case 4:
                anim = "rotationY";
                view.setPivotX(w);
                view.setPivotY(h / 2);
                break;

            default:
                break;
        }

        // 执行缩小动画【华丽效果】
        froBig_ToSmall(view, pivot, anim);
        return pivot;
    }

    /**
     * 启动抬起动画
     *
     * @param view  执行动画的View
     * @param pivot 动画执行顶点
     */
    public static void startAnimUp(View view, int pivot) {
        if (pivot == 0) {
            // 执行放大动画【缩放效果】
            froSmall_ToBig(view, view.getScaleX());
        } else {
            String anim = "";
            switch (pivot) {
                case 1:
                case 3:
                    anim = "rotationX";
                    break;
                case 2:
                case 4:
                    anim = "rotationY";
                    break;
            }
            // 执行放大动画【华丽效果】
            froSmall_ToBig(view, pivot, anim);
        }
    }

    /**
     * 【华丽效果】从大过渡到小
     */
    public static void froBig_ToSmall(View view, int pivot, String anim) {

        float potationEnd = 0;
        if (pivot == 3 || pivot == 4) {
            potationEnd = 0 - potationValue;
        } else {
            potationEnd = potationValue;
        }

        int potationStart = 0;
        if (pivot == 2 || pivot == 4) {
            potationStart = (int) view.getRotationY();
        } else {
            potationStart = (int) view.getRotationX();
        }

        ObjectAnimator animObject = ObjectAnimator.ofFloat(view, anim, potationStart, potationEnd)
                .setDuration(animSpeed);
        animObject.setInterpolator(interpolator);
        animObject.start();
    }

    /**
     * 【华丽效果】从小过渡到大
     */
    public static void froSmall_ToBig(View view, int pivot, String anim) {
        int potation = 0;
        if (pivot == 2 || pivot == 4) {
            potation = (int) view.getRotationY();
        } else {
            potation = (int) view.getRotationX();
        }
        ObjectAnimator animObject = ObjectAnimator.ofFloat(view, anim, potation, 0).setDuration(animSpeed);
        animObject.setInterpolator(interpolator);
        animObject.start();
    }

    /**
     * 【缩放效果】从大过渡到小
     */
    public static void froBig_ToSmall(View view, float scaleNow) {

        /**
         * 控件的宽变小
         */
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX", scaleNow, scaleEnd);
        /**
         * 控件的高变小
         */
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY", scaleNow, scaleEnd);

        /**
         * 动画集合，所有动画一起播放
         */
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX, pvhY).setDuration(animSpeed);
        animator.setInterpolator(interpolator);
        animator.start();
    }

    /**
     * 【缩放效果】从小过渡到大
     */
    public static void froSmall_ToBig(View view, float scaleNow) {

        /**
         * 控件的宽变大
         */
        PropertyValuesHolder pvhX_2 = PropertyValuesHolder.ofFloat("scaleX", scaleNow, 1);
        /**
         * 控件的高变大
         */
        PropertyValuesHolder pvhY_2 = PropertyValuesHolder.ofFloat("scaleY", scaleNow, 1);

        /**
         * 动画集合，所有动画一起播放
         */
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, pvhX_2, pvhY_2).setDuration(animSpeed);
        animator.setInterpolator(interpolator);
        animator.start();
    }

}
