package com.halzhang.android.startupnews.data;

import android.content.Context;

import com.halzhang.android.startupnews.data.utils.OkHttpClientHelper;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hal on 2016/5/30.
 */
@Module
public class OkHttpClientModule {

    @Singleton
    @Provides
    OkHttpClient provideOkHttpClient(Context context, OkHttpClientHelper.CookieFactory cookieFactory) {
        OkHttpClientHelper okHttpClientHelper = new OkHttpClientHelper(cookieFactory, context);
        return okHttpClientHelper.getOkHttpClient();
    }

}
