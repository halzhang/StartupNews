/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui.phone;

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.ui.BaseFragmentActivity;
import com.halzhang.android.apps.startupnews.ui.tablet.BrowseFragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.Window;

/**
 * StartupNews
 * <p>
 * 浏览页面
 * </p>
 *
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
public class BrowseActivity extends BaseFragmentActivity {

    // private static final String LOG_TAG =
    // BrowseActivity.class.getSimpleName();

    public static final String EXTRA_URL = "extra_url";

    public static final String EXTRA_TITLE = "extra_title";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_browse);
        String mOriginalUrl = getIntent().getStringExtra(EXTRA_URL);
        if (TextUtils.isEmpty(mOriginalUrl)) {
            finish();
            return;
        }
        String mTitle = getIntent().getStringExtra(EXTRA_TITLE);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        BrowseFragment fragment = new BrowseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, mOriginalUrl);
        bundle.putString(EXTRA_TITLE, mTitle);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
