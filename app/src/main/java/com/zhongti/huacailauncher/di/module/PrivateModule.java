package com.zhongti.huacailauncher.di.module;

import com.jess.arms.di.scope.ActivityScope;
import com.zhongti.huacailauncher.contract.PrivateContract;
import com.zhongti.huacailauncher.model.impl.PrivateModel;

import dagger.Module;
import dagger.Provides;


@Module
public class PrivateModule {
    private PrivateContract.View view;

    /**
     * 构建PrivateModule时,将View的实现类传进来,这样就可以提供View的实现类给presenter
     *
     * @param view
     */
    public PrivateModule(PrivateContract.View view) {
        this.view = view;
    }

    @ActivityScope
    @Provides
    PrivateContract.View providePrivateView() {
        return this.view;
    }

    @ActivityScope
    @Provides
    PrivateContract.Model providePrivateModel(PrivateModel model) {
        return model;
    }
}