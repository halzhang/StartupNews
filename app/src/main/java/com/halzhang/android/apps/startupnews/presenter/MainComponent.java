package com.halzhang.android.apps.startupnews.presenter;

import com.halzhang.android.apps.startupnews.SnApiComponent;
import com.halzhang.android.apps.startupnews.ui.MainActivity;
import com.halzhang.android.apps.startupnews.utils.ActivityScoped;

import dagger.Component;

/**
 * 主页
 * Created by Hal on 16/8/14.
 */
@ActivityScoped
@Component(dependencies = SnApiComponent.class, modules = {CommentsListPresenterModule.class,
        MainActivityPresenterModule.class})
public interface MainComponent {

    void inject(MainActivity mainActivity);

}
