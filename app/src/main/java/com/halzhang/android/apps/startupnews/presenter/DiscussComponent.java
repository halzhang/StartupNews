package com.halzhang.android.apps.startupnews.presenter;

import com.halzhang.android.apps.startupnews.SnApiComponent;
import com.halzhang.android.apps.startupnews.ui.DiscussActivity;
import com.halzhang.android.apps.startupnews.utils.ActivityScoped;

import dagger.Component;

/**
 * Created by Hal on 16/8/14.
 */
@ActivityScoped
@Component(dependencies = SnApiComponent.class, modules = DiscussPresenterModule.class)
public interface DiscussComponent {

    void inject(DiscussActivity discussActivity);

}
