package com.halzhang.android.apps.startupnews.presenter;

import com.halzhang.android.apps.startupnews.SnApiComponent;
import com.halzhang.android.apps.startupnews.ui.LoginActivity;
import com.halzhang.android.apps.startupnews.utils.ActivityScoped;
import com.halzhang.android.apps.startupnews.utils.FragmentScoped;

import dagger.Component;

/**
 * Dagger component for login
 * Created by Hal on 16/6/12.
 */
@ActivityScoped
@Component(dependencies = SnApiComponent.class, modules = LoginPresenterModule.class)
public interface LoginComponent {

    void inject(LoginActivity loginActivity);

}
