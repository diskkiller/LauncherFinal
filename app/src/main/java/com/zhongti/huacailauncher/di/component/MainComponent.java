package com.zhongti.huacailauncher.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongti.huacailauncher.di.module.MainModule;
import com.zhongti.huacailauncher.ui.MainActivity;
import com.zhongti.huacailauncher.ui.home.fragment.LotteryFragment;
import com.zhongti.huacailauncher.ui.home.fragment.GameFragment;

import dagger.Component;

@ActivityScope
@Component(modules = MainModule.class, dependencies = AppComponent.class)
public interface MainComponent {
    void inject(MainActivity activity);

    void inject(LotteryFragment fragment);

    void inject(GameFragment fragment);
}