package com.halzhang.android.startupnews.data;

import android.content.Context;

import com.halzhang.android.startupnews.data.utils.SessionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Module provide {@link SessionManager}
 * Created by Hal on 16/5/30.
 */
@Module
public class SessionManagerModule {

    @Singleton
    @Provides
    SessionManager provideSessionManager(Context context) {
        return new SessionManager(context);
    }

}
