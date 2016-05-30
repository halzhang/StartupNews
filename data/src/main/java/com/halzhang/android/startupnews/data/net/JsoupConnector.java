/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.startupnews.data.net;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.halzhang.android.startupnews.data.utils.SessionManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.inject.Singleton;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Apr 23, 2013
 */
@Singleton
public class JsoupConnector {
    
    private static final String LOG_TAG = JsoupConnector.class.getSimpleName();

    private Context mContext;

    private SessionManager mSessionManager;

    public JsoupConnector(Context context, SessionManager sessionManager) {
        mContext = context;
        mSessionManager = sessionManager;
    }

    public Connection newJsoupConnection(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Connection conn = null;
        String user = mSessionManager.getSessionUser();
        if (TextUtils.isEmpty(user)) {
            Log.i(LOG_TAG, "user is empty!");
            conn = Jsoup.connect(url);
        } else {
            conn = Jsoup.connect(url).cookie("user", user);
        }
        return conn;
    }

}
