package com.zhongti.huacailauncher.ui.lottery.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jess.arms.http.imageloader.glide.GlideArms;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.common.EventBusTags;
import com.zhongti.huacailauncher.model.event.EventBigImgClose;
import com.zhongti.huacailauncher.utils.code.Utils;
import com.zhongti.slauncher.photoview.PhotoView;
import com.zhongti.slauncher.photoview.PhotoViewAttacher;

import org.simple.eventbus.EventBus;

public class ImageFragment extends Fragment {
    private static final String IMAGE_URL = "image";
    PhotoView image;
    private String imageUrl;

    public ImageFragment() {
    }

    public static ImageFragment newInstance(String param1) {
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_URL, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(IMAGE_URL);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_big_image, container, false);
        image = view.findViewById(R.id.image_big_photo);
        loadImg(imageUrl, image);
        image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                EventBus.getDefault().post(new EventBigImgClose(), EventBusTags.EVENT_BIG_IMG_CLOSE);
            }

            @Override
            public void onOutsidePhotoTap() {

            }
        });
        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public void loadImg(String url, ImageView iv) {
        if (!TextUtils.isEmpty(url) && iv != null) {
            GlideArms.with(Utils.getApp())
                    .asBitmap()
                    .load(url)
                    .error(R.drawable.img_place_lotti_pw)
                    .fallback(R.drawable.img_place_lotti_pw)
                    .into(iv);
        } else {
            if (iv != null) iv.setImageResource(R.drawable.img_place_lotti_pw);
        }

    }
}
