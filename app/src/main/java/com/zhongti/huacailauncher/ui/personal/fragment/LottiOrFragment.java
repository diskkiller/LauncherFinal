package com.zhongti.huacailauncher.ui.personal.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.jess.arms.di.component.AppComponent;
import com.zhongti.huacailauncher.R;
import com.zhongti.huacailauncher.app.base.BaseSupportFragment;
import com.zhongti.huacailauncher.model.event.EvClosePrivatePage;
import com.zhongti.huacailauncher.ui.personal.adapter.PrivatePagerAdapter;
import com.zhongti.huacailauncher.ui.personal.fragment.child.Lotti1Fragment;
import com.zhongti.huacailauncher.ui.personal.fragment.child.Lotti2Fragment;
import com.zhongti.huacailauncher.ui.personal.fragment.child.Lotti3Fragment;
import com.zhongti.huacailauncher.widget.SViewPager;

import org.simple.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Create by ShuHeMing on 18/6/6
 */
public class LottiOrFragment extends BaseSupportFragment {

    @BindView(R.id.rg_private_lotti)
    RadioGroup rgLotti;
    @BindView(R.id.vp_private_lotti)
    SViewPager vpLotti;

    private List<Fragment> mFragments;
    private PrivatePagerAdapter adapter;

    public static LottiOrFragment newInstance() {

        Bundle args = new Bundle();

        LottiOrFragment fragment = new LottiOrFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void setupFragmentComponent(@NonNull AppComponent appComponent) {

    }

    @Override
    public View initView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_private_lotti_or, container, false);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (mFragments == null) {
            mFragments = new ArrayList<>();
            mFragments.add(Lotti1Fragment.newInstance());
            mFragments.add(Lotti2Fragment.newInstance());
            mFragments.add(Lotti3Fragment.newInstance());
        }
        if (adapter == null) {
            vpLotti.setCanScroll(false);
            adapter = new PrivatePagerAdapter(getChildFragmentManager(), null);
            vpLotti.setAdapter(adapter);
        }
        adapter.setNewData(mFragments);
        setUpPage(0, R.id.rb_private_lotti1);
        //选择的监听
        rgLotti.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_private_lotti1:
                    setUpPage(0, checkedId);
                    break;
                case R.id.rb_private_lotti2:
                    setUpPage(1, checkedId);
                    break;
                case R.id.rb_private_lotti3:
                    setUpPage(2, checkedId);
                    break;
            }
        });
    }

    @Override
    public void setData(@Nullable Object data) {

    }

    private void setUpPage(int index, int checkedId) {
        rgLotti.check(checkedId);
        vpLotti.setCurrentItem(index, false);
    }

    @OnClick(R.id.fl_private_lotti_close)
    public void onViewClicked() {
        EventBus.getDefault().post(new EvClosePrivatePage());
    }

}
