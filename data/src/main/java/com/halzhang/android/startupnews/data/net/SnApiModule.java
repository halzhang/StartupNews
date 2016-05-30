package com.halzhang.android.startupnews.data.net;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by zhanghanguo@yy.com on 2016/5/30.
 */
@Module
public class SnApiModule {

    @Singleton
    @Provides
    ISnApi provideSnApi(OkHttpClient okHttpClient, Context context) {
        return new SnApiImpl(okHttpClient, context);
    }

}
