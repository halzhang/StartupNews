package com.halzhang.android.apps.startupnews.presenter;

import com.halzhang.android.startupnews.data.entity.Status;

/**
 * Created by Hal on 16/8/23.
 */
public interface MainActivityContract {

    interface Presenter extends BasePresenter {
        void logout();

        /**
         * 投票，顶！d=====(￣▽￣*)b
         *
         * @param postId
         */
        void upVote(String postId);
    }

    interface View extends BaseView<Presenter> {

        /**
         * 注销登录
         *
         * @param result true 成功
         */
        void onLogoutResult(boolean result);

        void onUpVoteFailure(Throwable e);

        void onUpVoteSuccess(Status status);

    }

}
