package com.halzhang.android.apps.startupnews.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.halzhang.android.startupnews.data.entity.SNComments;
import com.halzhang.android.startupnews.data.net.ISnApi;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hal on 16/8/14.
 */
public class CommentsListPresenter implements CommentsListContract.Presenter {

    @NonNull
    private final ISnApi mSnApi;
    @NonNull
    private final CommentsListContract.View mView;

    private SNComments mComments;

    @Inject
    public CommentsListPresenter(@NonNull ISnApi snApi, @NonNull CommentsListContract.View view) {
        mSnApi = snApi;
        mView = view;
    }

    @Inject
    void setupListener() {
        mView.setPresenter(this);
    }

    @Override
    public void getComments(String url) {
        mSnApi.getSNComments(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SNComments>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView.isActive()) {
                            mView.onFailure(e);
                        }
                    }

                    @Override
                    public void onNext(SNComments snComments) {
                        if (snComments != null) {
                            mComments = snComments;
                            if (mView.isActive()) {
                                mView.onSuccess(mComments.getSnComments());
                            }
                        }
                    }
                });
    }

    @Override
    public void getMoreComments() {
        if (mComments == null || TextUtils.isEmpty(mComments.getMoreURL())) {
            if (mView.isActive()) {
                mView.onAtEnd();
            }
        } else {
            mSnApi.getSNComments(mComments.getMoreURL())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SNComments>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (mView.isActive()) {
                                mView.onFailure(e);
                            }
                        }

                        @Override
                        public void onNext(SNComments snComments) {
                            if (snComments != null) {
                                if (mComments == null) {
                                    mComments = snComments;
                                } else {
                                    mComments.addComments(snComments.getSnComments());
                                    mComments.setMoreURL(snComments.getMoreURL());
                                }
                                if (mView.isActive()) {
                                    mView.onSuccess(mComments.getSnComments());
                                }
                            }
                        }
                    });
        }
    }

    /* no-op */
    @Override
    public void start() {
    }

    @Override
    public void stop() {

    }
}
