package com.zhongti.huacailauncher.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongti.huacailauncher.di.module.PrivateModule;
import com.zhongti.huacailauncher.ui.personal.activity.PrivateActivity;
import com.zhongti.huacailauncher.ui.personal.fragment.GoldOrFragment;
import com.zhongti.huacailauncher.ui.personal.fragment.MarketOrFragment;
import com.zhongti.huacailauncher.ui.personal.fragment.child.Lotti1Fragment;
import com.zhongti.huacailauncher.ui.personal.fragment.child.Lotti2Fragment;
import com.zhongti.huacailauncher.ui.personal.fragment.child.Lotti3Fragment;

import dagger.Component;

@ActivityScope
@Component(modules = PrivateModule.class, dependencies = AppComponent.class)
public interface PrivateComponent {
    void inject(PrivateActivity activity);

    void inject(Lotti1Fragment fragment);

    void inject(Lotti2Fragment fragment);

    void inject(Lotti3Fragment fragment);

    void inject(GoldOrFragment fragment);

    void inject(MarketOrFragment fragment);
}