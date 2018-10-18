package com.zhongti.huacailauncher.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongti.huacailauncher.di.module.ScratchModule;
import com.zhongti.huacailauncher.ui.lottery.activity.ScratchDJActivity;
import com.zhongti.huacailauncher.ui.lottery.activity.ScratchDetailActivity;
import com.zhongti.huacailauncher.ui.lottery.activity.HadPayActivity;

import dagger.Component;

@ActivityScope
@Component(modules = ScratchModule.class, dependencies = AppComponent.class)
public interface ScratchComponent {
    void inject(ScratchDetailActivity activity);

    void inject(HadPayActivity activity);

    void inject(ScratchDJActivity activity);
}