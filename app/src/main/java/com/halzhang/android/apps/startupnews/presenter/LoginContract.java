package com.halzhang.android.apps.startupnews.presenter;

import rx.Subscription;

/**
 * Contract for login presenter and view
 * Created by Hal on 16/6/12.
 */
public interface LoginContract {

    interface Presenter extends BasePresenter {
        void login(String username, String password);
    }

    interface View extends BaseView<Presenter> {

        void onLoginError(Throwable e);

        void onLoginResult(String user);

        void addSubscription(Subscription subscription);
    }


}
