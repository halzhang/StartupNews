package com.halzhang.android.startupnews.data.net;

import android.content.Context;

import com.halzhang.android.startupnews.data.entity.SNFeed;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import rx.Observable;

/**
 * api impl
 * Created by zhanghanguo@yy.com on 2016/5/30.
 */
@Singleton
public class SnApiImpl implements ISnApi {

    private OkHttpClient mOkHttpClient;
    private Context mContext;

    public SnApiImpl(OkHttpClient okHttpClient, Context context) {
        mOkHttpClient = okHttpClient;
        mContext = context;
    }

    @Override
    public Observable<SNFeed> getSNFeed(String url) {
        return null;
    }

    @Override
    public Observable<String> getFnid() {
        return null;
    }

    @Override
    public Observable<String> login(String username, String password) {
        return null;
    }
}
