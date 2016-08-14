package com.halzhang.android.apps.startupnews.presenter;

import dagger.Module;
import dagger.Provides;

/**
 * Module for News List provide newt view
 * Created by Hal on 16/6/12.
 */
@Module
public class NewsListPresenterModule {

    private final NewsListContract.View mView;

    public NewsListPresenterModule(NewsListContract.View view) {
        mView = view;
    }

    @Provides
    NewsListContract.View provideNewsListContractView() {
        return mView;
    }

}
