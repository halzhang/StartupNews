package com.halzhang.android.startupnews.data;

import com.halzhang.android.startupnews.data.utils.CookieFactoryImpl;
import com.halzhang.android.startupnews.data.utils.OkHttpClientHelper;
import com.halzhang.android.startupnews.data.utils.SessionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hal on 16/8/14.
 */
@Module
public class CookieFactoryModule {

    @Singleton
    @Provides
    OkHttpClientHelper.CookieFactory provideCookieFactory(SessionManager sessionManager) {
        return new CookieFactoryImpl(sessionManager);
    }


}
