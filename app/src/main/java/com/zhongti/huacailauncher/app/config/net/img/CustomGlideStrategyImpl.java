package com.zhongti.huacailauncher.app.config.net.img;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jess.arms.http.imageloader.BaseImageLoaderStrategy;
import com.jess.arms.http.imageloader.glide.GlideAppliesOptions;
import com.jess.arms.http.imageloader.glide.GlideArms;
import com.jess.arms.http.imageloader.glide.GlideRequest;
import com.jess.arms.http.imageloader.glide.GlideRequests;
import com.makeramen.roundedimageview.RoundedDrawable;
import com.zhongti.huacailauncher.utils.code.Utils;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * 自定义Glide加载图片策略
 * <p>
 * Created by shuheming on 2018/1/12 0012.
 */

public class CustomGlideStrategyImpl implements BaseImageLoaderStrategy<CustomImgConfigImpl>, GlideAppliesOptions {

    @Override
    public void loadImage(Context ctx, CustomImgConfigImpl config) {
        if (ctx == null) throw new NullPointerException("Context is required");
        if (config == null) throw new NullPointerException("ImageConfigImpl is required");
        if (TextUtils.isEmpty(config.getUrl())) throw new NullPointerException("Url is required");
        if (config.getImageView() == null) throw new NullPointerException("Imageview is required");

        GlideRequests requests = GlideArms.with(ctx);//如果context是activity则自动使用Activity的生命周期
        //判断加载那种类型的图片
        switch (config.loadShape()) {
            case CustomImgConfigImpl.CIRCLE:
                loadCircle(requests, config);
                break;

            case CustomImgConfigImpl.ROUNDED:
                loadRounded(requests, config);
                break;

            default:
                loadNormal(requests, config);
                break;

        }
    }

    /**
     * 加载圆形的图片
     */
    private void loadCircle(GlideRequests requests, CustomImgConfigImpl config) {
        GlideRequest<Bitmap> bitmapGlideRequest = requests.asBitmap().load(config.getUrl().trim())
                .centerCrop();
        normalConfig(config, bitmapGlideRequest);
        bitmapGlideRequest.into(new BitmapImageViewTarget(config.getImageView()) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(Utils.getApp().getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                config.getImageView().setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    /**
     * 加载圆角图片
     */
    private void loadRounded(GlideRequests requests, CustomImgConfigImpl config) {
        GlideRequest<Bitmap> bitmapGlideRequest = requests.asBitmap().load(config.getUrl().trim())
                .centerCrop();
        normalConfig(config, bitmapGlideRequest);
        bitmapGlideRequest.into(new BitmapImageViewTarget(config.getImageView()) {
            @Override
            protected void setResource(Bitmap resource) {
                if (resource != null) {
                    RoundedDrawable roundedDrawable = RoundedDrawable.fromBitmap(resource)
                            .setScaleType(ImageView.ScaleType.CENTER_CROP)
                            .setCornerRadius(config.getRounded().roundTopLeft,
                                    config.getRounded().roundTopRight,
                                    config.getRounded().roundBottomRight,
                                    config.getRounded().roundBottomLeft);
                    config.getImageView().setImageDrawable(roundedDrawable);
                }

            }
        });
    }

    /**
     * 普通加载
     */
    private void loadNormal(GlideRequests requests, CustomImgConfigImpl config) {
        GlideRequest<Drawable> glideRequest = requests.load(config.getUrl().trim())
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop();
        normalConfig(config, glideRequest);
        glideRequest.into(config.getImageView());
    }

    /**
     * 无论加载什么形状的图片,通用的配置
     */
    @SuppressLint("CheckResult")
    private <T> void normalConfig(CustomImgConfigImpl config, GlideRequest<T> glideRequest) {
        switch (config.getCacheStrategy()) {//缓存策略
            case 0:
                glideRequest.diskCacheStrategy(DiskCacheStrategy.ALL);
                break;
            case 1:
                glideRequest.diskCacheStrategy(DiskCacheStrategy.NONE);
                break;
            case 2:
                glideRequest.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                break;
            case 3:
                glideRequest.diskCacheStrategy(DiskCacheStrategy.DATA);
                break;
            case 4:
                glideRequest.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                break;
        }
        if (config.getTransformation() != null) {//glide用它来改变图形的形状
            glideRequest.transform(config.getTransformation());
        }


        if (config.getPlaceholder() != 0)//设置占位符
            glideRequest.placeholder(config.getPlaceholder());

        if (config.getErrorPic() != 0)//设置错误的图片
            glideRequest.error(config.getErrorPic());

        if (config.getFallback() != 0)//设置请求 url 为空图片
            glideRequest.fallback(config.getFallback());
    }

    @Override
    public void clear(Context ctx, CustomImgConfigImpl config) {
        if (ctx == null) throw new NullPointerException("Context is required");
        if (config == null) throw new NullPointerException("ImageConfigImpl is required");

        if (config.getImageViews() != null && config.getImageViews().length > 0) {//取消在执行的任务并且释放资源
            for (ImageView imageView : config.getImageViews()) {
                GlideArms.get(ctx).getRequestManagerRetriever().get(ctx).clear(imageView);
            }
        }

        if (config.isClearDiskCache()) {//清除本地缓存
            Observable.just(0)
                    .observeOn(Schedulers.io())
                    .subscribe(integer -> Glide.get(ctx).clearDiskCache());
        }

        if (config.isClearMemory()) {//清除内存缓存
            Observable.just(0)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(integer -> Glide.get(ctx).clearMemory());
        }
    }

    @Override
    public void applyGlideOptions(Context context, GlideBuilder builder) {
        Timber.w("applyGlideOptions");
    }
}
