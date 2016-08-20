package com.halzhang.android.apps.startupnews.presenter;

import android.support.annotation.NonNull;

import com.halzhang.android.startupnews.data.net.ISnApi;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Presenter for Login
 * Created by Hal on 16/6/12.
 */
public class LoginPresenter implements LoginContract.Presenter {

    @NonNull
    private final LoginContract.View mView;

    @NonNull
    private final ISnApi mSnApi;

    @Inject
    public LoginPresenter(@NonNull LoginContract.View view, @NonNull ISnApi snApi) {
        mView = view;
        mSnApi = snApi;
    }

    @Inject
    void setupListener() {
        mView.setPresenter(this);
    }

    @Override
    public void login(final String username, final String password) {
        mView.addSubscription(
                mSnApi.getFnid()
                        .flatMap(new Func1<String, Observable<String>>() {
                            @Override
                            public Observable<String> call(String s) {
                                return mSnApi.login(s, username, password);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<String>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                if (mView.isActive()) {
                                    mView.onLoginError(e);
                                }
                            }

                            @Override
                            public void onNext(String s) {
                                if (mView.isActive()) {
                                    mView.onLoginResult(s);
                                }
                            }
                        })
        );
    }

    /* no-op */
    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

}
