/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.ui;

import com.actionbarsherlock.view.Window;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.utils.PreferenceUtils;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

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

    private static final String LOG_TAG = BrowseActivity.class.getSimpleName();

    public static final String EXTRA_URL = "extra_url";

    public static final String EXTRA_TITLE = "extra_title";

    private WebView mWebView;

    private ProgressBar mProgressBar;

    private String mTitle;

    private String mUrl;

    @Override
    protected void onCreate(Bundle arg0) {
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);// 注释掉，ShareActionProvider无法解析
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_browse);
        mWebView = (WebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mProgressBar.setMax(100);
        mProgressBar.setVisibility(View.GONE);
        mUrl = getIntent().getStringExtra(EXTRA_URL);
        mTitle = getIntent().getStringExtra(EXTRA_TITLE);

        final com.actionbarsherlock.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());

        WebSettings settings = mWebView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            settings.setDisplayZoomControls(false);
        }
        settings.setJavaScriptEnabled(true);

        String mHtmlProvider = PreferenceUtils.getHtmlProvider(getApplicationContext());
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
        // MenuItem actionItem = menu.findItem(R.id.menu_share);
        // mShareActionProvider = (ShareActionProvider)
        // actionItem.getActionProvider();
        // mShareActionProvider
        // .setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        //
        // Intent intent = new Intent(Intent.ACTION_SEND);
        // intent.setType("text/plain");
        // intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
        // StringBuilder builder = new StringBuilder();
        // builder.append(mTitle).append(" ").append(mUrl);
        // builder.append(" （")
        // .append("分享自StartupNews: ")
        // .append("http://play.google.com/store/apps/details?id=com.halzhang.android.apps.startupnews")
        // .append("）");
        // intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
        // mShareActionProvider.setShareIntent(intent);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_back:
                mWebView.goBack();
                return true;
            case R.id.menu_forward:
                mWebView.goForward();
                return true;
            case R.id.menu_readability:
                mWebView.loadUrl("http://www.readability.com/m?url=" + mUrl);
                return true;
            case R.id.menu_refresh:
                mWebView.reload();
                return true;
            case R.id.menu_share: {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
                StringBuilder builder = new StringBuilder();
                builder.append(mTitle).append(" ").append(mUrl);
                builder.append(" （")
                        .append("分享自StartupNews: ")
                        .append("http://play.google.com/store/apps/details?id=com.halzhang.android.apps.startupnews")
                        .append("）");
                intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
                startActivity(Intent.createChooser(intent, "分享文章的方式:"));
            }
                return true;
            case R.id.menu_website: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mUrl));
                startActivity(intent);
            }
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
