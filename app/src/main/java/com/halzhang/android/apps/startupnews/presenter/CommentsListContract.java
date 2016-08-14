package com.halzhang.android.apps.startupnews.presenter;

import com.halzhang.android.startupnews.data.entity.SNComment;

import java.util.ArrayList;

/**
 * Created by Hal on 16/8/14.
 */
public interface CommentsListContract {

    interface Presenter extends BasePresenter {
        void getComments(String url);

        void getMoreComments();
    }

    interface View extends BaseView<Presenter> {
        void onSuccess(ArrayList<SNComment> snComments);

        void onFailure(Throwable e);

        void onAtEnd();
    }
}
