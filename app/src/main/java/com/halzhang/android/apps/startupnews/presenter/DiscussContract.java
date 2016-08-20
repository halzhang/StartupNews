package com.halzhang.android.apps.startupnews.presenter;

import com.halzhang.android.startupnews.data.entity.SNDiscuss;
import com.halzhang.android.startupnews.data.entity.Status;

/**
 * Created by Hal on 16/8/14.
 */
public interface DiscussContract {

    interface Presenter extends BasePresenter {
        void getDiscuss(String url);

        void comment(String message);
    }

    interface View extends BaseView<Presenter> {
        void onGetDiscuss(SNDiscuss snDiscuss);

        void onGetDiscussFailure(Throwable e);

        void onCommentSuccess(Status status);

        void onCommentFailure(Throwable e);

        void onSessionExpired();
    }

}
