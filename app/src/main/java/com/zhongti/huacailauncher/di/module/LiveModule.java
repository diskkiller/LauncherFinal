package com.zhongti.huacailauncher.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.zhongti.huacailauncher.contract.LiveContract;
import com.zhongti.huacailauncher.model.impl.LiveModel;

import dagger.Module;
import dagger.Provides;


@Module
public class LiveModule {
    private LiveContract.View view;

    /**
     * 构建LiveModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public LiveModule(LiveContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    LiveContract.View provideLiveView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    LiveContract.Model provideLiveModel(LiveModel model) {
        return model;
    }
}