package com.halzhang.android.apps.startupnews.presenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hal on 16/8/14.
 */
@Module
public class DiscussPresenterModule {

    private DiscussContract.View mView;

    public DiscussPresenterModule(DiscussContract.View view) {
        mView = view;
    }

    @Provides
    public DiscussContract.View provideDiscussContractView() {
        return mView;
    }
}
