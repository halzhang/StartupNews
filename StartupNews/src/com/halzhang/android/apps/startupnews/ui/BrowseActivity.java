/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.actionbarsherlock.widget.ShareActionProvider.OnShareTargetSelectedListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.utils.PreferenceUtils;
import com.halzhang.android.common.CDLog;

/**
 * StartupNews
 * <p>
 * 浏览页面
 * </p>
 *
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
public class BrowseActivity extends BaseFragmentActivity implements OnClickListener {

    private static final String LOG_TAG = BrowseActivity.class.getSimpleName();

    public static final String EXTRA_URL = "extra_url";

    public static final String EXTRA_TITLE = "extra_title";

    private WebView mWebView;

    private String mTitle;

    private String mOriginalUrl;

    private String mCurrentUrl;

    private String mHtmlProvider;

    @Override
    protected void onCreate(Bundle arg0) {
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);// 注释掉，ShareActionProvider无法解析
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_browse);
        mWebView = (WebView) findViewById(R.id.webview);
        mOriginalUrl = getIntent().getStringExtra(EXTRA_URL);
        if (TextUtils.isEmpty(mOriginalUrl)) {
            finish();
            return;
        }
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);

        final com.actionbarsherlock.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());

        WebSettings settings = mWebView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.GINGERBREAD_MR1) {
            settings.setDisplayZoomControls(false);
        }
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        findViewById(R.id.browse_back).setOnClickListener(this);
        findViewById(R.id.browse_forward).setOnClickListener(this);
        findViewById(R.id.browse_readability).setOnClickListener(this);
        findViewById(R.id.browse_refresh).setOnClickListener(this);
        findViewById(R.id.browse_website).setOnClickListener(this);

        mHtmlProvider = PreferenceUtils.getHtmlProvider(getApplicationContext());
        final String url = mHtmlProvider + mOriginalUrl;
        CDLog.i(LOG_TAG, "Open Url: " + url);
        mWebView.loadUrl(url);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            setCurrentUrl(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            setCurrentUrl(url);
        }
    }

    private void setCurrentUrl(String url) {
        mCurrentUrl = url;
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            int progress = (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * newProgress;
            setSupportProgress(progress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }

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
                String packageName = intent.getComponent().getPackageName();
                CDLog.i(LOG_TAG, intent.getComponent().getPackageName()));
                if ("com.sina.weibo".equals(packageName)) {
                    String extraText = intent.getStringExtra(Intent.EXTRA_TEXT);
                    intent.putExtra(Intent.EXTRA_TEXT,extraText+" "+getString(R.string.weibo_share_suffix));
                }
                EasyTracker.getTracker().sendEvent("ui_action", "share",
                        intent.getComponent().getPackageName(), 0L);
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
//        builder.append(" （").append("分享自StartupNews: ").append(getString(R.string.google_play_url))
//                .append("）");
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

    private String getCurrentUrl() {
        return TextUtils.isEmpty(mCurrentUrl) ? mOriginalUrl : mCurrentUrl;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.browse_back:
                back();
                break;
            case R.id.browse_forward:
                forward();
                break;
            case R.id.browse_readability:
                readability();
                break;
            case R.id.browse_refresh:
                refresh();
                break;
            case R.id.browse_website:
                webSite();
                break;
            default:
                break;
        }

    }

    private void back() {
        EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                "browseactivity_menu_back", 0L);
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
    }

    private void forward() {
        EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                "browseactivity_menu_forward", 0L);
        if (mWebView.canGoForward()) {
            mWebView.goForward();
        }
    }

    private void readability() {
        EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                "browseactivity_menu_readability", 0L);
        mWebView.loadUrl("http://www.readability.com/m?url=" + getCurrentUrl());
    }

    private void refresh() {
        EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                "browseactivity_menu_refresh", 0L);
        mWebView.reload();
    }

    private void webSite() {
        // 打开原链接，还是转码的链接呢？
        EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                "browseactivity_menu_website", 0L);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getCurrentUrl()));
        startActivity(intent);
    }

}
