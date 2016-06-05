package com.halzhang.android.apps.startupnews;

import android.content.Context;

import com.halzhang.android.startupnews.data.utils.OkHttpClientHelper;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Hal on 2016/5/30.
 */
@Module
public class ApplicationModule {

    private final Context mContext;

    private final OkHttpClientHelper.CookieFactory mCookieFactory;

    public ApplicationModule(Context context, OkHttpClientHelper.CookieFactory cookieFactory) {
        mContext = context;
        mCookieFactory = cookieFactory;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

    @Provides
    OkHttpClientHelper.CookieFactory provideCookieFactory() {
        return mCookieFactory;
    }
}
