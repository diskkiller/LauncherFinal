package com.zhongti.huacailauncher.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.zhongti.huacailauncher.contract.ScratchContract;
import com.zhongti.huacailauncher.model.impl.ScratchModel;

import dagger.Module;
import dagger.Provides;


@Module
public class ScratchModule {
    private ScratchContract.View view;

    /**
     * 构建ScratchModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public ScratchModule(ScratchContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    ScratchContract.View provideScratchView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    ScratchContract.Model provideScratchModel(ScratchModel model) {
        return model;
    }
}