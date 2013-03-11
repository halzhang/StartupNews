/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui;

import com.halzhang.android.apps.startupnews.R;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.ShareActionProvider;

/**
 * StartupNews
 * <p>
 * 浏览页面
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
public class BrowseActivity extends FragmentActivity {

    private static final String LOG_TAG = BrowseActivity.class.getSimpleName();

    // private static final String HTMLPROVIDER_PREFIX_VIEWTEXT =
    // "http://viewtext.org/article?url=";
    //
    // private static final String HTMLPROVIDER_PREFIX_GOOGLE =
    // "http://www.google.com/gwt/x?u=";
    //
    // private static final String HTMLPROVIDER_PREFIX_SINA =
    // "http://weibo.cn/sinaurl?to=m&u=";

    public static final String EXTRA_URL = "extra_url";

    public static final String EXTRA_TITLE = "extra_title";

    private WebView mWebView;

    private ProgressBar mProgressBar;

    private ShareActionProvider mShareActionProvider;

    private String mTitle;

    private String mUrl;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_browse);
        mWebView = (WebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setMax(100);
        mUrl = getIntent().getStringExtra(EXTRA_URL);
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setTitle(mTitle);

        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());

        WebSettings settings = mWebView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptEnabled(true);

        String mHtmlProvider = PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()).getString(getString(R.string.pref_key_html_provider),
                getString(R.string.default_html_provider));
        final String url = mHtmlProvider + mUrl;
        Log.i(LOG_TAG, url);
        mWebView.loadUrl(url);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            mProgressBar.setProgress(newProgress);
            if (newProgress == 100) {
                mProgressBar.animate().alpha(0).withEndAction(new Runnable() {

                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);

                    }
                });
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_browse, menu);
        mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.menu_share)
                .getActionProvider();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
        StringBuilder builder = new StringBuilder();
        builder.append(mTitle).append(" ").append(mUrl);
        intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
        mShareActionProvider.setShareIntent(intent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return true;
    }

}
