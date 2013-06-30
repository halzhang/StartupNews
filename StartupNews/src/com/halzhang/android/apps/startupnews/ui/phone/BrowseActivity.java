/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui.phone;

import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.actionbarsherlock.widget.ShareActionProvider.OnShareTargetSelectedListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.ui.BaseFragmentActivity;
import com.halzhang.android.apps.startupnews.ui.widgets.WebViewController;
import com.halzhang.android.apps.startupnews.utils.PreferenceUtils;
import com.halzhang.android.common.CDLog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebView;

/**
 * StartupNews
 * <p>
 * 浏览页面
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
@SuppressLint("SetJavaScriptEnabled")
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BrowseActivity extends BaseFragmentActivity {

    private static final String LOG_TAG = BrowseActivity.class.getSimpleName();

    public static final String EXTRA_URL = "extra_url";

    public static final String EXTRA_TITLE = "extra_title";

    private WebView mWebView;

    private String mTitle;

    private String mOriginalUrl;

    private String mHtmlProvider;

    private WebViewController mWebViewController;

    @Override
    protected void onCreate(Bundle arg0) {
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);// 注释掉，ShareActionProvider无法解析
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_browse);
        mWebView = (WebView) findViewById(R.id.webview);
        mWebViewController = new WebViewController(this);
        mWebViewController.initControllerView(mWebView, findViewById(R.id.browse_bar));

        mOriginalUrl = getIntent().getStringExtra(EXTRA_URL);
        if (TextUtils.isEmpty(mOriginalUrl)) {
            finish();
            return;
        }
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);

        final com.actionbarsherlock.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mHtmlProvider = PreferenceUtils.getHtmlProvider(getApplicationContext());
        final String url = mHtmlProvider + mOriginalUrl;
        mWebViewController.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_browse, menu);
        MenuItem actionItem = menu.findItem(R.id.menu_share);
        ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
        actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        actionProvider.setShareIntent(createShareIntent());
        actionProvider.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {

            @Override
            public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                /*
                 * 这里改变intent是没用的，intent只是一份拷贝，只能自己启动修改后的Intent
                 * 然而自己启动Intent并不会改变历史记录
                 */
                String packageName = intent.getComponent().getPackageName();
                CDLog.i(LOG_TAG, packageName);
                EasyTracker.getTracker().sendEvent("ui_action", "share", packageName, 0L);
                if (getString(R.string.weibo_package_name).equals(packageName)) {
                    String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
                    intent.putExtra(Intent.EXTRA_TEXT, extraText + " "
                            + getString(R.string.weibo_share_suffix));
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });
        return true;
    }

    /**
     * Creates a sharing {@link Intent}.
     * 
     * @return The sharing intent.
     */
    private Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
        StringBuilder builder = new StringBuilder();
        builder.append(mTitle).append(" ").append(mOriginalUrl);
        // builder.append(" （").append("分享自StartupNews: ").append(getString(R.string.google_play_url))
        // .append("）");
        intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_original_url:
                EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                        "browseactivity_menu_original_url", 0L);
                mWebView.loadUrl(mOriginalUrl);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
