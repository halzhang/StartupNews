package com.halzhang.android.apps.startupnews.presenter;

import dagger.Module;
import dagger.Provides;

/**
 * Module for login provide login view
 * Created by Hal on 16/6/12.
 */
@Module
public class LoginPresenterModule {

    private final LoginContract.View mView;

    public LoginPresenterModule(LoginContract.View view) {
        mView = view;
    }

    @Provides
    LoginContract.View provideLoginContractView() {
        return mView;
    }

}
