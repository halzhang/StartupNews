package com.halzhang.android.apps.startupnews.presenter;

import dagger.Module;
import dagger.Provides;

/**
 * Module for Comments provide Comments view
 * Created by Hal on 16/6/12.
 */
@Module
public class CommentsListPresenterModule {

    private final CommentsListContract.View mView;

    public CommentsListPresenterModule(CommentsListContract.View view) {
        mView = view;
    }

    @Provides
    CommentsListContract.View provideCommentsListContractView() {
        return mView;
    }

}
