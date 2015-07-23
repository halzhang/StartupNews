/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.apps.startupnews.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.halzhang.android.apps.startupnews.Constants.IntentAction;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.presenter.LoginPresenter;
import com.halzhang.android.apps.startupnews.snkit.SessionManager;
import com.halzhang.android.common.CDToast;
import com.halzhang.android.mvp.annotation.RequiresPresenter;

import java.lang.ref.WeakReference;

import static com.halzhang.android.apps.startupnews.presenter.LoginPresenter.ILoginCallback;
import static com.halzhang.android.apps.startupnews.presenter.LoginPresenter.ILoginView;

/**
 * StartupNews
 * <p>
 * 登陆
 * </p>
 *
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Apr 20, 2013
 */
@RequiresPresenter(LoginPresenter.class)
public class LoginActivity extends BaseFragmentActivity<LoginPresenter, LoginPresenter.ILoginCallback> implements ILoginView {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    private String mFnid;

    private LoginFragment mLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_login);
        SessionManager.getInstance(this).clear();
        mLoginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mLoginFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_login:
                EasyTracker.getInstance(this).send(MapBuilder.createEvent("ui_action", "options_item_selected",
                        "loginactivity_menu_login", 0L).build());
                mLoginFragment.attemptLogin();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private ILoginCallback mCallback;

    @Override
    protected void onSetCallback(ILoginCallback iLoginCallback) {
        mCallback = iLoginCallback;
    }

    // Presenter 与 View 之间的接口
    public void onLoginPreTaskPostExecute(String result) {
        mLoginFragment.showProgress(false);
        mLoginFragment.mUsernameView.requestFocus();
    }

    public void onLoginPreTaskCancel() {
        mLoginFragment.showProgress(false);
    }

    public void onUserLoginTaskPostExecute(String user) {
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

    public void onUserLoginTaskCancel() {
        mLoginFragment.showProgress(false);
    }

    public String getUsername() {
        return mLoginFragment.mUsername;
    }

    public String getPassword() {
        return mLoginFragment.mPassword;
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }
    //end interface

    @SuppressLint("ValidFragment")
    private static class LoginFragment extends Fragment {

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
            mLoginActivityRef = new WeakReference<>((LoginActivity) activity);
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
            Button loginBtn = (Button) view.findViewById(R.id.btn_login);
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attemptLogin();
                }
            });
            return view;
        }

        /**
         * Attempts to sign in or register the account specified by the login
         * form. If there are form errors (invalid email, missing fields, etc.),
         * the errors are presented and no actual login attempt is made.
         */
        public void attemptLogin() {
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
                mLoginActivityRef.get().mCallback.onLogin();
            }
        }

        /**
         * Shows the progress UI and hides the login form.
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        public void showProgress(final boolean show) {
            if (getActivity() == null) {
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
