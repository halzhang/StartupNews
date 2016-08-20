/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui;

import com.halzhang.android.apps.startupnews.Constants.IntentAction;
import com.halzhang.android.apps.startupnews.MyApplication;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.SnApiComponent;
import com.halzhang.android.apps.startupnews.presenter.DaggerDiscussComponent;
import com.halzhang.android.apps.startupnews.presenter.DiscussPresenter;
import com.halzhang.android.apps.startupnews.presenter.DiscussPresenterModule;
import com.halzhang.android.apps.startupnews.ui.tablet.DiscussFragment;
import com.halzhang.android.startupnews.data.entity.SNNew;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

/**
 * StartupNews
 * <p>
 * 评论界面
 * </p>
 *
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 17, 2013
 */
public class DiscussActivity extends BaseFragmentActivity {

    //private static final String LOG_TAG = DiscussActivity.class.getSimpleName();

    public static final String ARG_DISCUSS_URL = "discuss_url";//required!

    public static final String ARG_SNNEW = "snnew";//Optional

    public static void start(Context context, String discussUrl, SNNew snNew) {
        Intent starter = new Intent(context, DiscussActivity.class);
        starter.putExtra(ARG_DISCUSS_URL, discussUrl);
        starter.putExtra(ARG_SNNEW, snNew);
        context.startActivity(starter);
    }

    @Inject
    DiscussPresenter mDiscussPresenter;

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
        }

        ;
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_discuss);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        SNNew snNew = (SNNew) getIntent().getSerializableExtra(ARG_SNNEW);
        String mDiscussURL = getIntent().getStringExtra(ARG_DISCUSS_URL);
        if (TextUtils.isEmpty(mDiscussURL)) {
            finish();
        }
        mDiscussFragment = DiscussFragment.newInstance(mDiscussURL,snNew);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mDiscussFragment).commitAllowingStateLoss();
        IntentFilter filter = new IntentFilter(IntentAction.ACTION_LOGIN);
        registerReceiver(mReceiver, filter);
        SnApiComponent snApiComponent = ((MyApplication) getApplication()).getSnApiComponent();
        DaggerDiscussComponent.builder().snApiComponent(snApiComponent)
                .discussPresenterModule(new DiscussPresenterModule(mDiscussFragment)).build().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
