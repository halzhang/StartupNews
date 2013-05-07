/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.apps.startupnews.snkit;

import com.halzhang.android.apps.startupnews.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.WebSettings;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * StartupNews
 * <p>
 * API
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version May 4, 2013
 */
public class SNApi {

    private static final String LOG_TAG = SNApi.class.getSimpleName();

    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android " + Build.VERSION.RELEASE
            + "; " + Build.MODEL + " Build/" + Build.DISPLAY + ")";

    private AsyncHttpClient mAsyncHttpClient;

    public SNApi(Context context) {
        mAsyncHttpClient = new AsyncHttpClient();
        mAsyncHttpClient.addHeader("Accept-Language", "zh-cn");
        mAsyncHttpClient.addHeader("Accept", "*/*");
        mAsyncHttpClient.addHeader("Cookie", SessionManager.getInstance(context).getCookieString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mAsyncHttpClient.setUserAgent(WebSettings.getDefaultUserAgent(context));
        } else {
            mAsyncHttpClient.setUserAgent(USER_AGENT);
        }
    }

    /**
     * 投票
     * 
     * @param url
     * @param responseHandler
     */
    public void upVote(String url, AsyncHttpResponseHandler responseHandler) {
        mAsyncHttpClient.get(url, responseHandler);
    }

    /**
     * 评论
     * 
     * @param fnid
     * @param text
     * @param responseHandler
     */
    public void comment(Context context, String fnid, String text,
            AsyncHttpResponseHandler responseHandler) {
        ArrayList<NameValuePair> valuePairs = new ArrayList<NameValuePair>(2);
        valuePairs.add(new BasicNameValuePair("fnid", fnid));
        valuePairs.add(new BasicNameValuePair("text", text));
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, HTTP.UTF_8);
            mAsyncHttpClient.post(context, context.getString(R.string.host, "/r"), entity,
                    "application/x-www-form-urlencoded", responseHandler);
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
    }

}
