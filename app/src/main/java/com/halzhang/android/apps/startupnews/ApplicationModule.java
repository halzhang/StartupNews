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


    public ApplicationModule(Context context) {
        mContext = context;
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

}
