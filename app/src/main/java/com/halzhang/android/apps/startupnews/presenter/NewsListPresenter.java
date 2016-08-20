package com.halzhang.android.apps.startupnews.presenter;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.halzhang.android.startupnews.data.entity.SNFeed;
import com.halzhang.android.startupnews.data.net.ISnApi;

import javax.inject.Inject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 最新文章列表
 * Created by Hal on 2016/5/12.
 */
public class NewsListPresenter implements NewsListContract.Presenter {

    @NonNull
    private final ISnApi mSnApi;

    @NonNull
    private final NewsListContract.View mView;

    private SNFeed mSNFeed;

    @Inject
    public NewsListPresenter(ISnApi snApi, NewsListContract.View view) {
        mSnApi = snApi;
        mView = view;
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
    public void getFeed(String url) {
        mSnApi.getSNFeed(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SNFeed>() {
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
                    public void onNext(SNFeed snFeed) {
                        if (snFeed != null) {
                            mSNFeed = snFeed;
                            if (mView.isActive()) {
                                mView.onSuccess(mSNFeed.getSnNews());
                            }
                        }
                    }
                });
    }

    @Override
    public void getMoreFeed() {
        if (mSNFeed != null && !TextUtils.isEmpty(mSNFeed.getMoreUrl())) {
            mSnApi.getSNFeed(mSNFeed.getMoreUrl())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SNFeed>() {
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
                        public void onNext(SNFeed snFeed) {
                            if (snFeed != null) {
                                if (mSNFeed == null) {
                                    mSNFeed = snFeed;
                                } else {
                                    mSNFeed.setMoreUrl(snFeed.getMoreUrl());
                                    mSNFeed.addNews(snFeed.getSnNews());
                                }
                                if (mView.isActive()) {
                                    mView.onSuccess(mSNFeed.getSnNews());
                                }
                            }
                        }
                    });
        } else {
            if (mView.isActive()) {
                mView.onAtEnd();
            }
        }
    }
}
