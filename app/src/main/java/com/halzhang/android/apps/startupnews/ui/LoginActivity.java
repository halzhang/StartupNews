/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.apps.startupnews.ui;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.Constants.IntentAction;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.parser.BaseHTMLParser;
import com.halzhang.android.apps.startupnews.snkit.SessionManager;
import com.halzhang.android.common.CDLog;
import com.halzhang.android.common.CDToast;

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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * StartupNews
 * <p>
 * 登陆
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Apr 20, 2013
 */
public class LoginActivity extends BaseFragmentActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    private String mFnid;

    private LoginPreTask mPreTask;

    private LoginFragment mLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_login);
        SessionManager.getInstance(this).clear();
        mLoginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mLoginFragment)
                .commit();
        mPreTask = new LoginPreTask();
        mPreTask.execute("");
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_login:
                EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                        "loginactivity_menu_login", 0L);
                mLoginFragment.attemptLogin();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public class UserLoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String user = null;
            final HttpPost httpPost = new HttpPost(getString(R.string.host, "/y"));
            httpPost.addHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
            httpPost.addHeader("Accept-Language", "zh-cn");
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                httpPost.addHeader(HTTP.USER_AGENT,
                        WebSettings.getDefaultUserAgent(getApplicationContext()));
            }
            httpPost.addHeader("Accept", "*/*");
            httpPost.addHeader("Accept-Encoding", "gzip,deflate");
            httpPost.addHeader("Connection", "keep-alive");
            ArrayList<NameValuePair> valuePairs = new ArrayList<NameValuePair>(3);
            valuePairs.add(new BasicNameValuePair("fnid", mFnid));
            valuePairs.add(new BasicNameValuePair("p", mLoginFragment.mPassword));
            valuePairs.add(new BasicNameValuePair("u", mLoginFragment.mUsername));
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
                CookieSyncManager.createInstance(getApplicationContext());
                for (Cookie cookie : cookies) {
                    if ("user".equals(cookie.getName())) {
                        String value = cookie.getValue();
                        CDLog.i(LOG_TAG, "Cookie name: user " + " Value: " + cookie.getValue());
                        SessionManager.getInstance(getApplicationContext()).storeSesson(value,
                                mLoginFragment.mUsername);
                        user = value;
                    }
                    // sync cookie to webview
                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.setCookie(getString(R.string.host), cookie.getName() + "="
                            + cookie.getValue());
                    CookieSyncManager.getInstance().sync();
                }
            } catch (IOException e1) {
                EasyTracker.getTracker().sendException("User login error!", e1, false);
                CDLog.e(LOG_TAG, null, e1);
                return user;
            } finally {
                httpClient.getConnectionManager().shutdown();
            }
            return user;
        }

        @Override
        protected void onPostExecute(final String user) {
            mAuthTask = null;
            mLoginFragment.showProgress(false);
            if (!TextUtils.isEmpty(user)) {
                CDToast.showToast(getApplicationContext(), R.string.tip_login_success);
                Intent intent = new Intent(IntentAction.ACTION_LOGIN);
                intent.putExtra(IntentAction.EXTRA_LOGIN_USER, user);
                sendBroadcast(intent);
                finish();
            } else {
                CDToast.showToast(getApplicationContext(), R.string.tip_login_failure);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mLoginFragment.showProgress(false);
        }
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
                doc = Jsoup.connect(getString(R.string.host, "/news")).get();
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
            mPreTask = null;
            mLoginFragment.showProgress(false);
            mLoginFragment.mUsernameView.requestFocus();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mPreTask = null;
            mLoginFragment.showProgress(false);
        }

    }

    @SuppressLint("ValidFragment")
    private static class LoginFragment extends SherlockFragment {

        // Values for email and password at the time of the login attempt.
        private String mUsername;

        private String mPassword;

        // UI references.
        private EditText mUsernameView;

        private EditText mPasswordView;

        private View mLoginFormView;

        private View mLoginStatusView;

        private TextView mLoginStatusMessageView;

        private WeakReference<LoginActivity> mLoginActivityRef;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            mLoginActivityRef = new WeakReference<LoginActivity>((LoginActivity) activity);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_login, null);
            mUsernameView = (EditText) view.findViewById(R.id.username);
            mPasswordView = (EditText) view.findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            mLoginFormView = view.findViewById(R.id.login_form);
            mLoginStatusView = view.findViewById(R.id.login_status);
            mLoginStatusMessageView = (TextView) view.findViewById(R.id.login_status_message);
            mLoginStatusMessageView.setText(R.string.login_progress_init);
            showProgress(true);
            return view;
        }

        /**
         * Attempts to sign in or register the account specified by the login
         * form. If there are form errors (invalid email, missing fields, etc.),
         * the errors are presented and no actual login attempt is made.
         */
        public void attemptLogin() {
            if (mLoginActivityRef.get().mAuthTask != null) {
                return;
            }

            // Reset errors.
            mUsernameView.setError(null);
            mPasswordView.setError(null);

            mUsername = mUsernameView.getText().toString();
            mPassword = mPasswordView.getText().toString();

            boolean cancel = false;
            View focusView = null;

            if (TextUtils.isEmpty(mPassword)) {
                mPasswordView.setError(getString(R.string.error_field_required));
                focusView = mPasswordView;
                cancel = true;
            }
            if (TextUtils.isEmpty(mUsername)) {
                mUsernameView.setError(getString(R.string.error_field_required));
                focusView = mUsernameView;
                cancel = true;
            }

            if (cancel) {
                focusView.requestFocus();
            } else {
                mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
                showProgress(true);
                mLoginActivityRef.get().mAuthTask = mLoginActivityRef.get().new UserLoginTask();
                mLoginActivityRef.get().mAuthTask.execute((Void) null);
            }
        }

        /**
         * Shows the progress UI and hides the login form.
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        public void showProgress(final boolean show) {
            if(getActivity() == null){
                //防止 not attact to activity 出错
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(
                        android.R.integer.config_shortAnimTime);

                mLoginStatusView.setVisibility(View.INVISIBLE);
                mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                            }
                        });

                mLoginFormView.setVisibility(View.INVISIBLE);
                mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                            }
                        });
            } else {
                mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }

    }

}
