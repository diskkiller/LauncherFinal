package com.zhongti.huacailauncher.app.config.net.img;

import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.jess.arms.http.imageloader.ImageConfig;

/**
 * 这里存放图片请求的配置信息,可以一直扩展字段,如果外部调用时想让图片加载框架
 * 做一些操作,比如清除缓存或者切换缓存策略,则可以定义一个 int 类型的变量,内部根据 switch(int) 做不同的操作
 * 其他操作同理
 * <p>
 * Created by shuheming on 2018/1/12 0012.
 */

public class CustomImgConfigImpl extends ImageConfig {

    public static final int CIRCLE = 1;//加载为圆形图片
    public static final int ROUNDED = 2;//加载为圆角图片

    private int cacheStrategy;//0对应DiskCacheStrategy.all,1对应DiskCacheStrategy.NONE,2对应DiskCacheStrategy.SOURCE,3对应DiskCacheStrategy.RESULT
    private int fallback; //请求 url 为空,则使用此图片作为占位符
    private BitmapTransformation transformation;//glide用它来改变图形的形状
    private ImageView[] imageViews;
    private boolean isClearMemory;//清理内存缓存
    private boolean isClearDiskCache;//清理本地缓存
    private int loadShape;//加载的图片的形状
    private RoundD rounded; //圆角

    private CustomImgConfigImpl(Builder builder) {
        this.url = builder.url;
        this.imageView = builder.imageView;
        this.placeholder = builder.placeholder;
        this.errorPic = builder.errorPic;
        this.fallback = builder.fallback;
        this.cacheStrategy = builder.cacheStrategy;
        this.transformation = builder.transformation;
        this.imageViews = builder.imageViews;
        this.isClearMemory = builder.isClearMemory;
        this.isClearDiskCache = builder.isClearDiskCache;
        this.loadShape = builder.loadShape;
        this.rounded = builder.rounded;
    }

    public int getCacheStrategy() {
        return cacheStrategy;
    }

    public BitmapTransformation getTransformation() {
        return transformation;
    }

    public ImageView[] getImageViews() {
        return imageViews;
    }

    public boolean isClearMemory() {
        return isClearMemory;
    }

    public boolean isClearDiskCache() {
        return isClearDiskCache;
    }

    public int loadShape() {
        return loadShape;
    }

    public int getFallback() {
        return fallback;
    }

    public RoundD getRounded() {
        return rounded;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private String url;
        private ImageView imageView;
        private int placeholder;
        private int errorPic;
        private int fallback; //请求 url 为空,则使用此图片作为占位符
        private int cacheStrategy;//0对应DiskCacheStrategy.all,1对应DiskCacheStrategy.NONE,2对应DiskCacheStrategy.SOURCE,3对应DiskCacheStrategy.RESULT
        private BitmapTransformation transformation;//glide用它来改变图形的形状
        private ImageView[] imageViews;
        private boolean isClearMemory;//清理内存缓存
        private boolean isClearDiskCache;//清理本地缓存
        private int loadShape; // 加载图片的形状
        private RoundD rounded; //圆角

        private Builder() {
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder placeholder(int placeholder) {
            this.placeholder = placeholder;
            return this;
        }

        public Builder errorPic(int errorPic) {
            this.errorPic = errorPic;
            return this;
        }

        public Builder fallback(int fallback) {
            this.fallback = fallback;
            return this;
        }

        public Builder imageView(ImageView imageView) {
            this.imageView = imageView;
            return this;
        }

        public Builder cacheStrategy(int cacheStrategy) {
            this.cacheStrategy = cacheStrategy;
            return this;
        }

        public Builder transformation(BitmapTransformation transformation) {
            this.transformation = transformation;
            return this;
        }

        public Builder imageViews(ImageView... imageViews) {
            this.imageViews = imageViews;
            return this;
        }

        public Builder isClearMemory(boolean isClearMemory) {
            this.isClearMemory = isClearMemory;
            return this;
        }

        public Builder isClearDiskCache(boolean isClearDiskCache) {
            this.isClearDiskCache = isClearDiskCache;
            return this;
        }

        public Builder loadShape(int loadShape) {
            this.loadShape = loadShape;
            return this;
        }

        public Builder rounded(RoundD rounded) {
            this.rounded = rounded;
            return this;
        }

        public CustomImgConfigImpl build() {
            return new CustomImgConfigImpl(this);
        }
    }
}
