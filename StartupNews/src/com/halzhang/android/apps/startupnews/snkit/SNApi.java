/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.apps.startupnews.snkit;

import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.common.CDLog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;

import java.io.IOException;
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

    /**
     * USER-AGENT
     */
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
            CDLog.e(LOG_TAG, e.getMessage());
        }
    }

    /**
     * logout
     * 
     * @param url
     * @param responseHandler
     */
    public void logout(String url, AsyncHttpResponseHandler responseHandler) {
        mAsyncHttpClient.get(url, responseHandler);
    }

    public boolean logout(String url) {
        DefaultHttpClient httpClient = (DefaultHttpClient) mAsyncHttpClient.getHttpClient();
        HttpGet request = new HttpGet(url);
        try {
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            CDLog.i(LOG_TAG, "Status Code: " + statusCode);
            if (HttpStatus.SC_OK == statusCode || HttpStatus.SC_MOVED_TEMPORARILY == statusCode) {
                return true;
            }
        } catch (Exception e) {
            CDLog.w(LOG_TAG, null, e);
            EasyTracker.getTracker().sendException("User logout error!", e, false);
            return false;
        }
        return false;
    }

    /**
     * 校验cookie的有效性
     * <p>
     * 手机和pc都登陆之后，pc退出，会导致手机中的cookie失效
     * </p>
     * 
     * @param url
     */
    public void verificateCookie(Context context, String url) {
        Connection conn = JsoupFactory.getInstance(context).newJsoupConnection(url);
        if (conn != null) {
            try {
                Document doc = conn.get();
                Elements elements = doc.select("a:matches(logout)");
                if (elements.size() < 1) {
                    // cookie无效
                    SessionManager.getInstance(context).clear();
                }
            } catch (IOException e) {
                EasyTracker.getTracker().sendException(e.getMessage(), e, false);
            }

        }

    }

}
