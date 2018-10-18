package com.zhongti.huacailauncher.di.component;

import com.jess.arms.di.component.AppComponent;
import com.jess.arms.di.scope.ActivityScope;
import com.zhongti.huacailauncher.di.module.LiveModule;
import com.zhongti.huacailauncher.ui.lottery.activity.CTLiveJBActivity;
import com.zhongti.huacailauncher.ui.lottery.activity.CancelCheckTicketActivity;
import com.zhongti.huacailauncher.ui.lottery.activity.CheckTicketLiveActivity;

import dagger.Component;

@ActivityScope
@Component(modules = LiveModule.class, dependencies = AppComponent.class)
public interface LiveComponent {
    void inject(CheckTicketLiveActivity activity);

    void inject(CancelCheckTicketActivity activity);

    void inject(CTLiveJBActivity activity);
}