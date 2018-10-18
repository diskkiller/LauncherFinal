package com.zhongti.huacailauncher.ui.lottery.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zhongti.huacailauncher.ui.lottery.fragment.ImageFragment;

import java.util.ArrayList;
import java.util.List;

public class BigPicAdapter extends FragmentPagerAdapter {

    private final List<String> mDatas;

    public BigPicAdapter(FragmentManager supportFragmentManager, ArrayList<String> imgs) {
        super(supportFragmentManager);
        this.mDatas = imgs;
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Fragment getItem(int position) {
        String url = mDatas.get(position);
        return ImageFragment.newInstance(url);
    }
}
