package com.halzhang.android.startupnews.data;

import android.content.Context;

import com.halzhang.android.startupnews.data.net.ISnApi;
import com.halzhang.android.startupnews.data.net.JsoupConnector;
import com.halzhang.android.startupnews.data.net.SnApiImpl;
import com.halzhang.android.startupnews.data.utils.SessionManager;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hal on 2016/5/30.
 */
@Module
public class SnApiModule {

    @Singleton
    @Provides
    ISnApi provideSnApi(OkHttpClient okHttpClient, Context context, SessionManager sessionManager, JsoupConnector jsoupConnector) {
        return new SnApiImpl(okHttpClient, context, sessionManager, jsoupConnector);
    }

}
