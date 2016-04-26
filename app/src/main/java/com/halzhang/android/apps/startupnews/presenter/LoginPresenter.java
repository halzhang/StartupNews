package com.halzhang.android.apps.startupnews.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.snkit.SNApi;
import com.halzhang.android.apps.startupnews.snkit.SessionManager;
import com.halzhang.android.mvp.presenter.Presenter;
import com.halzhang.android.startupnews.data.parser.BaseHTMLParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * 登录业务封装
 * Created by Hal on 15/5/6.
 */
public class LoginPresenter extends Presenter<LoginPresenter.ILoginView, LoginPresenter.ILoginCallback> {

    private static final String LOG_TAG = LoginPresenter.class.getSimpleName();

    /**
     * 业务接口
     */
    public interface ILoginCallback {
        public void onLogin(Context context, String username, String password);
    }

    /**
     * 业务回调
     */
    public interface ILoginView extends Presenter.IView<ILoginCallback> {

        public void onLoginPreTaskPostExecute(String result);

        public void onLoginPreTaskCancel();

        public void onUserLoginTaskPostExecute(String user);

        public void onUserLoginTaskCancel();

    }

    private String mFnid;
    private LoginPreTask mLoginPreTask;
    private UserLoginTask mUserLoginTask;

    private static final String FNID_KEY = "fnid";

    @Override
    protected void onCreate(Bundle saveState) {
        super.onCreate(saveState);
        if (saveState != null) {
            mFnid = saveState.getString(FNID_KEY);
        }
    }

    @Override
    public ILoginCallback createViewCallback(IView view) {
        return new ILoginCallback() {
            @Override
            public void onLogin(Context context, String username, String password) {
                doUserLoginTask(context, username, password);
            }
        };
    }

    @Override
    protected void onSave(Bundle saveState) {
        super.onSave(saveState);
        saveState.putString(FNID_KEY, mFnid);
    }

    @Override
    protected void onAttachView(ILoginView view) {
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
        if (TextUtils.isEmpty(mFnid)) {
            mLoginPreTask = new LoginPreTask();
            mLoginPreTask.execute();
        }
    }

    /**
     * 登录
     */
    public void doUserLoginTask(Context context, String username, String password) {
        if (mUserLoginTask != null) {
            return;
        }
        mUserLoginTask = new UserLoginTask(context, username, password);
        mUserLoginTask.execute();
    }

    @WorkerThread
    private String getFnid(Context context) {
        String loginUrl = null;
        Document doc = null;
        try {
            doc = Jsoup.connect(context.getString(R.string.host, "/news")).get();
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

    /**
     * Parse login url and fnid
     *
     * @author Hal
     * @deprecated
     */
    private class LoginPreTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // do nothing
            return "";
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
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private Context mContext;
        private String mUsername;
        private String mPassword;

        public UserLoginTask(Context context, String username, String password) {
            mContext = context;
            mUsername = username;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            mFnid = getFnid(mContext);
            if (TextUtils.isEmpty(mFnid)) {
                return null;
            }
            String user = null;
            SNApi api = new SNApi();
            user = api.login(mContext.getString(R.string.host, "/y"), mFnid, mUsername, mPassword);
            if (!TextUtils.isEmpty(user)) {
                SessionManager.getInstance().storeSession(user, mUsername);
                // sync cookie to webview
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.setCookie(mContext.getString(R.string.host), "user="
                        + user);
                CookieSyncManager.getInstance().sync();
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
