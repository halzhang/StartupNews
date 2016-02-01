/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.apps.startupnews.snkit;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.common.CDLog;
import com.halzhang.android.startupnews.data.utils.OkHttpClientManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.http.OkHeaders;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    private OkHttpClient mOkHttpClient;

    public SNApi() {
        this(null);
    }

    public SNApi(Context context) {
        mOkHttpClient = OkHttpClientManager.getInstance().getOkHttpClient();
    }


    /**
     * 登陆
     *
     * @param fnid
     * @param username 用户名
     * @param password 密码
     * @return 返回登陆后的 user
     */
    @Nullable
    public String login(String url, String fnid, String username, String password) {
        String user = null;
        FormEncodingBuilder builder = new FormEncodingBuilder();
        builder.addEncoded("fnid", fnid).addEncoded("u", username).addEncoded("p", password);
        Request request = new Request.Builder().url(url).post(builder.build())
                .addHeader("Accept-Language", "zh-cn")
                .addHeader("Accept", "*/*")
                .addHeader("Accept-Encoding", "gzip,deflate")
                .addHeader("Connection", "keep-alive")
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                Map<String, List<String>> cookiesMap = mOkHttpClient.getCookieHandler().get(request.uri(), OkHeaders.toMultimap(request.headers(), null));
                if (cookiesMap.size() > 0) {
                    List<String> cookies = cookiesMap.get("Cookie");
                    for (String s : cookies) {
                        CDLog.i(LOG_TAG, s);
                        String[] cookie = TextUtils.split(s, "=");
                        if (cookie.length == 2 && "user".equals(cookie[0])) {
                            user = cookie[1];
                        }
                    }
                }
            }
        } catch (IOException e) {
            Tracker.getInstance().sendException("User login error!", e, false);
            CDLog.e(LOG_TAG, null, e);
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 投票
     *
     * @param url
     * @param callback okhttp callback {@link Callback}
     */
    public void upVote(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 评论
     *
     * @param fnid
     * @param text
     * @param callback okhttp callback {@link Callback}
     */
    public void comment(Context context, String fnid, String text, Callback callback) {

        RequestBody body = new FormEncodingBuilder().add("fnid", fnid)
                .add("text", text).build();

        Request request = new Request.Builder()
                .url(context.getString(R.string.host, "/r"))
                .post(body).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * logout
     *
     * @param url
     * @param callback
     */
    public void logout(String url, Callback callback) {
        Request request = new Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }

    public boolean logout(String url) {
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            int statusCode = response.code();
            CDLog.i(LOG_TAG, "Status Code: " + statusCode);
            if (response.isSuccessful()) {
                return true;
            }
        } catch (IOException e) {
            CDLog.w(LOG_TAG, null, e);
            Tracker.getInstance().sendException("User logout error!", e, false);
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
                Tracker.getInstance().sendException(e.getMessage(), e, false);
            }

        }

    }

}
