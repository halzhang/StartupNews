package com.halzhang.android.startupnews.data;

import android.content.Context;

import com.halzhang.android.startupnews.data.net.JsoupConnector;
import com.halzhang.android.startupnews.data.utils.SessionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hal on 16/5/30.
 */
@Module
public class JsoupConnectorModule {

    @Singleton
    @Provides
    JsoupConnector provideJsoupConnector(Context context, SessionManager sessionManager) {
        return new JsoupConnector(context, sessionManager);
    }

}
