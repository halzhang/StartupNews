/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.apps.startupnews.snkit;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version May 4, 2013
 */
public class SNApi {

    private AsyncHttpClient mAsyncHttpClient;

    public SNApi(Context context) {
        mAsyncHttpClient = new AsyncHttpClient();
        mAsyncHttpClient.addHeader("Accept-Language", "zh-cn");
        mAsyncHttpClient.addHeader("Accept", "*/*");
        mAsyncHttpClient.addHeader("Cookie", SessionManager.getInstance(context).getCookieString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mAsyncHttpClient.addHeader(HTTP.USER_AGENT, WebSettings.getDefaultUserAgent(context));
        }
    }

    public void upVote(String url, AsyncHttpResponseHandler responseHandler) {
        mAsyncHttpClient.get(url, responseHandler);
    }

}
