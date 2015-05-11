package com.halzhang.android.apps.startupnews.presenter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.apps.startupnews.parser.BaseHTMLParser;
import com.halzhang.android.apps.startupnews.snkit.SessionManager;
import com.halzhang.android.apps.startupnews.ui.LoginActivity;
import com.halzhang.android.common.CDLog;
import com.halzhang.android.mvp.presenter.Presenter;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录业务封装
 * Created by Hal on 15/5/6.
 */
public class LoginPresenter extends Presenter<LoginActivity> {

    private static final String LOG_TAG = LoginPresenter.class.getSimpleName();

    private String mFnid;
    private LoginPreTask mLoginPreTask;
    private UserLoginTask mUserLoginTask;

    private static final String FNID_KEY = "fnid";

    @Override
    protected void onCreate(Bundle saveState) {
        super.onCreate(saveState);
        if (saveState != null){
            mFnid = saveState.getString(FNID_KEY);
        }
    }

    @Override
    protected void onSave(Bundle saveState) {
        super.onSave(saveState);
        saveState.putString(FNID_KEY, mFnid);
    }

    @Override
    protected void onAttachView(LoginActivity view) {
        super.onAttachView(view);
        doLoginPreTask();
    }

    /**
     * 初始化登录参数
     */
    public void doLoginPreTask() {
        if (mLoginPreTask != null) {
            return;
        }
        if (TextUtils.isEmpty(mFnid)){
            mLoginPreTask = new LoginPreTask();
            mLoginPreTask.execute();
        }
    }

    /**
     * 登录
     */
    public void doUserLoginTask() {
        if (mUserLoginTask != null) {
            return;
        }
        mUserLoginTask = new UserLoginTask();
        mUserLoginTask.execute();
    }

    /**
     * Parse login url and fnid
     *
     * @author Hal
     */
    private class LoginPreTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String loginUrl = null;
            Document doc = null;
            try {
                doc = Jsoup.connect(getView().getString(R.string.host, "/news")).get();
                if (doc != null) {
                    Elements loginElements = doc.select("a:matches(Login/Register)");
                    if (loginElements.size() == 1) {
                        loginUrl = BaseHTMLParser.resolveRelativeSNURL(loginElements.first().attr(
                                "href"));
                    }
                }
            } catch (IOException e1) {
                Log.w(LOG_TAG, e1);
            }
            Log.i(LOG_TAG, "Login Url: " + loginUrl);
            String fnid = null;
            if (!TextUtils.isEmpty(loginUrl)) {
                try {
                    doc = Jsoup.connect(loginUrl).get();
                    if (doc != null) {
                        Elements inputElements = doc.select("input[name=fnid]");
                        if (inputElements != null && inputElements.size() > 0) {
                            fnid = inputElements.first().attr("value");
                            Log.i(LOG_TAG, "Login fnid: " + fnid);
                        }
                    }
                } catch (IOException e) {
                    return fnid;
                }
            }
            return fnid;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mFnid = result;
            getView().onLoginPreTaskPostExecute(result);
            mLoginPreTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            getView().onLoginPreTaskCancel();
            mUserLoginTask = null;
        }

    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public class UserLoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Context context = getView().getApplicationContext();
            String user = null;
            final HttpPost httpPost = new HttpPost(getView().getString(R.string.host, "/y"));
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
            httpPost.addHeader("Accept-Language", "zh-cn");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                httpPost.addHeader(HTTP.USER_AGENT,
                        WebSettings.getDefaultUserAgent(context));
            }
            httpPost.addHeader("Accept", "*/*");
            httpPost.addHeader("Accept-Encoding", "gzip,deflate");
            httpPost.addHeader("Connection", "keep-alive");
            ArrayList<NameValuePair> valuePairs = new ArrayList<NameValuePair>(3);
            valuePairs.add(new BasicNameValuePair("fnid", mFnid));
            valuePairs.add(new BasicNameValuePair("p", getView().getPassword()));
            valuePairs.add(new BasicNameValuePair("u", getView().getUsername()));
            try {
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, HTTP.UTF_8);
                httpPost.setEntity(entity);
            } catch (UnsupportedEncodingException e) {

            }
            DefaultHttpClient httpClient = new DefaultHttpClient();
            httpClient.getCookieStore().clear();
            try {
                HttpResponse response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK && statusCode != HttpStatus.SC_MOVED_TEMPORARILY) {
                    CDLog.w(LOG_TAG, "http response error,code: " + statusCode);
                    return null;
                }
                List<Cookie> cookies = httpClient.getCookieStore().getCookies();
                if (cookies == null || cookies.size() < 1) {
                    return null;
                }
                CookieSyncManager.createInstance(context);
                for (Cookie cookie : cookies) {
                    if ("user".equals(cookie.getName())) {
                        String value = cookie.getValue();
                        CDLog.i(LOG_TAG, "Cookie name: user " + " Value: " + cookie.getValue());
                        SessionManager.getInstance(context).storeSesson(value,
                                getView().getUsername());
                        user = value;
                    }
                    // sync cookie to webview
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.setCookie(getView().getString(R.string.host), cookie.getName() + "="
                            + cookie.getValue());
                    CookieSyncManager.getInstance().sync();
                }
            } catch (IOException e1) {
                Tracker.getInstance().sendException("User login error!", e1, false);
                CDLog.e(LOG_TAG, null, e1);
                return user;
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return user;
        }

        @Override
        protected void onPostExecute(final String user) {
            getView().onUserLoginTaskPostExecute(user);
            mUserLoginTask = null;
        }

        @Override
        protected void onCancelled() {
            getView().onUserLoginTaskCancel();
            mUserLoginTask = null;
        }
    }

}
