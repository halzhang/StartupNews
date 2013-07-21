/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.halzhang.android.apps.startupnews.Constants.IntentAction;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.entity.SNNew;
import com.halzhang.android.apps.startupnews.ui.tablet.DiscussFragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * StartupNews
 * <p>
 * 评论界面
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 17, 2013
 */
@SuppressLint("NewApi")
public class DiscussActivity extends BaseFragmentActivity {

    //private static final String LOG_TAG = DiscussActivity.class.getSimpleName();

    public static final String ARG_DISCUSS_URL = "discuss_url";

    public static final String ARG_SNNEW = "snnew";

    private DiscussFragment mDiscussFragment;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(android.content.Context context, Intent intent) {
            final String action = intent.getAction();
            if (IntentAction.ACTION_LOGIN.equals(action)) {
                String user = intent.getStringExtra(IntentAction.EXTRA_LOGIN_USER);
                if (!TextUtils.isEmpty(user)) {
                    mDiscussFragment.loadData();
                }
            }
        };
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
        setContentView(R.layout.activity_discuss);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SNNew snNew = (SNNew) getIntent().getSerializableExtra(ARG_SNNEW);
        String mDiscussURL = getIntent().getStringExtra(ARG_DISCUSS_URL);
        if (snNew == null || TextUtils.isEmpty(mDiscussURL)) {
            finish();
        }
        Bundle args = new Bundle(getIntent().getExtras());
        mDiscussFragment = new DiscussFragment();
        mDiscussFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mDiscussFragment).commitAllowingStateLoss();
        IntentFilter filter = new IntentFilter(IntentAction.ACTION_LOGIN);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDiscussFragment.loadData();
        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
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
