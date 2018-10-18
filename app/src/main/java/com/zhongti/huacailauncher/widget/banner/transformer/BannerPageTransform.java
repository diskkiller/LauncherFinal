package com.zhongti.huacailauncher.widget.banner.transformer;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import timber.log.Timber;

public class BannerPageTransform implements ViewPager.PageTransformer {

    private final float SCALE_MAX = 0.8f;
    private final float ALPHA_MAX = 0.5f;

    @Override
    public void transformPage(@NonNull View page, float position) {
        if ((int) position < -1 || (int) position > 1) {
            return;
        }

        float scale = (position < 0)
                ? ((1 - SCALE_MAX) * position + 1)
                : ((SCALE_MAX - 1) * position + 1);
        float alpha = (position < 0)
                ? ((1 - ALPHA_MAX) * position + 1)
                : ((ALPHA_MAX - 1) * position + 1);
        if (position < 0) {
            ViewCompat.setPivotX(page, page.getWidth());
            ViewCompat.setPivotY(page, page.getHeight() / 2);
        } else {
            ViewCompat.setPivotX(page, 0);
            ViewCompat.setPivotY(page, page.getHeight() / 2);
        }
        Timber.d("position: " + position + ",scale:" + scale);

        ViewCompat.setScaleX(page, scale);
        ViewCompat.setScaleY(page, scale);
        ViewCompat.setAlpha(page, Math.abs(alpha));
    }
}