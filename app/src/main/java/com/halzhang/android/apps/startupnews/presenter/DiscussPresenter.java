package com.halzhang.android.apps.startupnews.presenter;

import android.support.annotation.NonNull;

import com.halzhang.android.startupnews.data.entity.SNDiscuss;
import com.halzhang.android.startupnews.data.entity.Status;
import com.halzhang.android.startupnews.data.net.ISnApi;
import com.halzhang.android.startupnews.data.utils.SessionManager;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Hal on 16/8/14.
 */
public class DiscussPresenter implements DiscussContract.Presenter {

    @NonNull
    private final DiscussContract.View mView;

    @NonNull
    private final ISnApi mSnApi;

    @NonNull
    private final SessionManager mSessionManager;

    private String mFnid;

    @Inject
    public DiscussPresenter(DiscussContract.View view, ISnApi snApi, SessionManager sessionManager) {
        mView = view;
        mSnApi = snApi;
        mSessionManager = sessionManager;
    }

    @Inject
    void setupListener() {
        mView.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void getDiscuss(String url) {
        mSnApi.getDiscuss(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SNDiscuss>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView.isActive()) {
                            mView.onGetDiscussFailure(e);
                        }
                    }

                    @Override
                    public void onNext(SNDiscuss snDiscuss) {
                        if (mView.isActive()) {
                            if (snDiscuss != null) {
                                mFnid = snDiscuss.getFnid();
                            }
                            mView.onGetDiscuss(snDiscuss);
                        }
                    }
                });
    }

    @Override
    public void comment(String message) {
        if (!mSessionManager.isValid()) {
            if (mView.isActive()) {
                mView.onSessionExpired();
            }
            return;
        }
        mSnApi.comment(message, mFnid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Status>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mView.isActive()) {
                            mView.onCommentFailure(e);
                        }
                    }

                    @Override
                    public void onNext(Status status) {
                        if (mView.isActive()) {
                            mView.onCommentSuccess(status);
                        }
                    }
                });
    }
}
