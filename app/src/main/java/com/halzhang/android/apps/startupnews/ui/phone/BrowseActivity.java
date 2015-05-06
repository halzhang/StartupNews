/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui.phone;

import com.actionbarsherlock.view.Window;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.ui.BaseFragmentActivity;
import com.halzhang.android.apps.startupnews.ui.tablet.BrowseFragment;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * StartupNews
 * <p>
 * 浏览页面
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BrowseActivity extends BaseFragmentActivity {

    // private static final String LOG_TAG =
    // BrowseActivity.class.getSimpleName();

    public static final String EXTRA_URL = "extra_url";

    public static final String EXTRA_TITLE = "extra_title";

    @Override
    protected void onCreate(Bundle arg0) {
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);// 注释掉，ShareActionProvider无法解析
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_PROGRESS);
//        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
//        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.ab_bg_black));
        setContentView(R.layout.activity_browse);

        String mOriginalUrl = getIntent().getStringExtra(EXTRA_URL);
        if (TextUtils.isEmpty(mOriginalUrl)) {
            finish();
            return;
        }
        String mTitle = getIntent().getStringExtra(EXTRA_TITLE);

        final com.actionbarsherlock.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        BrowseFragment fragment = new BrowseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_URL, mOriginalUrl);
        bundle.putString(EXTRA_TITLE, mTitle);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment)
                .commitAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
