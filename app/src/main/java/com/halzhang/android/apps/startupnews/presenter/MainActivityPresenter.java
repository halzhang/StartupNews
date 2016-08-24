package com.halzhang.android.apps.startupnews.presenter;

import android.support.annotation.NonNull;

import com.halzhang.android.startupnews.data.entity.Status;
import com.halzhang.android.startupnews.data.net.ISnApi;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hal on 16/8/23.
 */
public class MainActivityPresenter implements MainActivityContract.Presenter {

    @NonNull
    private final ISnApi mSnApi;
    @NonNull
    private final MainActivityContract.View mView;

    @Inject
    public MainActivityPresenter(ISnApi snApi, MainActivityContract.View view) {
        mSnApi = snApi;
        mView = view;
    }

    @Inject
    void setupListener() {
        mView.setPresenter(this);
    }

    @Override
    public void logout() {
        mSnApi.logout().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (mView.isActive()) {
                            mView.onLogoutResult(aBoolean);
                        }
                    }
                });
    }

    @Override
    public void upVote(String postId) {
        mSnApi.upVote(postId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Status>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView.isActive()) {
                            mView.onUpVoteFailure(e);
                        }
                    }

                    @Override
                    public void onNext(Status status) {
                        if (mView.isActive()) {
                            mView.onUpVoteSuccess(status);
                        }
                    }
                });
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
