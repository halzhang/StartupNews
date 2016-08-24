package com.halzhang.android.apps.startupnews.presenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hal on 16/8/23.
 */
@Module
public class MainActivityPresenterModule {

    private MainActivityContract.View mView;

    public MainActivityPresenterModule(MainActivityContract.View view) {
        mView = view;
    }

    @Provides
    public MainActivityContract.View provideMainActivityContractView() {
        return mView;
    }
}
