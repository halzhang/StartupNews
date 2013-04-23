/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.apps.startupnews.utils;

import com.halzhang.android.apps.startupnews.snkit.SessionManager;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import android.content.Context;
import android.text.TextUtils;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Apr 23, 2013
 */
public class JsoupFactory {

    private Context mContext;

    private JsoupFactory(Context context) {
        mContext = context;
    }

    private static JsoupFactory me;

    public static JsoupFactory getInstance(Context context) {
        if (me == null) {
            me = new JsoupFactory(context);
        }
        return me;
    }

    public Connection newJsoupConnection(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        Connection conn = null;
        String user = SessionManager.getInstance(mContext).getSessionUser();
        if (TextUtils.isEmpty(user)) {
            conn = Jsoup.connect(url);
        } else {
            conn = Jsoup.connect(url).cookie("user", user);
        }
        return conn;
    }

}
