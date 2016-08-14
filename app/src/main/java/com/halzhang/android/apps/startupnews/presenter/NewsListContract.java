package com.halzhang.android.apps.startupnews.presenter;

import com.halzhang.android.startupnews.data.entity.SNNew;

import java.util.ArrayList;

/**
 * Created by Hal on 16/8/14.
 */
public interface NewsListContract {

    interface Presenter extends BasePresenter {
        void getFeed(String url);

        void getMoreFeed();
    }

    interface View extends BaseView<NewsListContract.Presenter> {
        void onSuccess(ArrayList<SNNew> snNews);

        void onFailure(Throwable e);

        void onAtEnd();
    }

}
