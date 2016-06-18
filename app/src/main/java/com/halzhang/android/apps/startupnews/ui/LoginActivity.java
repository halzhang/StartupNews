/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.apps.startupnews.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;
import com.halzhang.android.apps.startupnews.MyApplication;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.SnApiComponent;
import com.halzhang.android.apps.startupnews.presenter.DaggerLoginComponent;
import com.halzhang.android.apps.startupnews.presenter.LoginPresenter;
import com.halzhang.android.apps.startupnews.presenter.LoginPresenterModule;
import com.halzhang.android.apps.startupnews.ui.fragment.LoginFragment;

import javax.inject.Inject;

/**
 * StartupNews
 * <p>
 * 登陆
 * </p>
 *
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Apr 20, 2013
 */
public class LoginActivity extends BaseActivity {

    private static final String LOG_TAG = LoginActivity.class.getSimpleName();

    @Inject
    LoginPresenter mPresenter;

    private LoginFragment mLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_login);
        mLoginFragment = LoginFragment.newInstance();
        SnApiComponent snApiComponent = ((MyApplication) getApplication()).getSnApiComponent();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mLoginFragment).commit();
        DaggerLoginComponent.builder().loginPresenterModule(new LoginPresenterModule(mLoginFragment))
                .snApiComponent(snApiComponent).build().inject(this);
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

}
