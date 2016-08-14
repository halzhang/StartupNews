package com.halzhang.android.startupnews.data.utils;

import com.halzhang.android.startupnews.data.utils.OkHttpClientHelper.CookieFactory;

/**
 * Created by Hal on 16/8/14.
 */
public class CookieFactoryImpl implements CookieFactory {

    private SessionManager mSessionManager;

    public CookieFactoryImpl(SessionManager sessionManager) {
        mSessionManager = sessionManager;
    }

    @Override
    public String getCookie() {
        return mSessionManager.getCookieString();
    }
}
