
package com.halzhang.android.apps.startupnews.ui.tablet;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;
import com.actionbarsherlock.widget.ShareActionProvider.OnShareTargetSelectedListener;
import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.ui.phone.BrowseActivity;
import com.halzhang.android.apps.startupnews.ui.widgets.WebViewController;
import com.halzhang.android.apps.startupnews.utils.PreferenceUtils;
import com.halzhang.android.common.CDLog;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * 浏览器 Created by Hal on 13-5-25.
 */
public class BrowseFragment extends SherlockFragment {

    private static final String LOG_TAG = BrowseFragment.class.getSimpleName();

    private WebView mWebView;

    private WebViewController mWebViewController;

    private String mTitle;

    private String mOriginalUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebViewController = new WebViewController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_browse, null);
        mWebView = (WebView) view.findViewById(R.id.webview);
        mWebViewController.initControllerView(mWebView, view);
        Bundle args = getArguments();
        if (args != null && args.containsKey(BrowseActivity.EXTRA_TITLE)
                && args.containsKey(BrowseActivity.EXTRA_URL)) {
            mTitle = args.getString(BrowseActivity.EXTRA_TITLE);
            mOriginalUrl = args.getString(BrowseActivity.EXTRA_URL);
            String mHtmlProvider = PreferenceUtils.getHtmlProvider(getActivity());
            final String url = mHtmlProvider + mOriginalUrl;
            mWebViewController.loadUrl(url);
        }
        return view;
    }
    
    public void setTitle(String title) {
        mTitle = title;
    }

    public void load(String url) {
        mOriginalUrl = url;
        String mHtmlProvider = PreferenceUtils.getHtmlProvider(getActivity());
        mWebViewController.loadUrl(mHtmlProvider + mOriginalUrl);
    }

    @Override
    public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
            com.actionbarsherlock.view.MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_browse, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem actionItem = menu.findItem(R.id.menu_share);
        ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
        actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
        actionProvider.setShareIntent(createShareIntent());
        actionProvider.setAllowPolicyChangeIntent(true);
        actionProvider.setOnShareTargetSelectedListener(new OnShareTargetSelectedListener() {

            @Override
            public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
                /*
                 * 这里改变intent是没用的，intent只是一份拷贝，只能自己启动修改后的Intent
                 * 然而自己启动Intent并不会改变历史记录
                 * 增加setAllowPolicyChangeIntent方法解决这问题
                 */
                String packageName = intent.getComponent().getPackageName();
                CDLog.i(LOG_TAG, packageName);
                String shareContent = getShareContent();
                intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
                EasyTracker.getTracker().sendEvent("ui_action", "share", packageName, 0L);
                if (getString(R.string.weibo_package_name).equals(packageName)) {
                    intent.putExtra(Intent.EXTRA_TEXT, shareContent + " "
                            + getString(R.string.weibo_share_suffix));
                }else{
                    intent.putExtra(Intent.EXTRA_TEXT, shareContent);
                }
                return false;
            }
        });
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
        intent.putExtra(Intent.EXTRA_TEXT, getShareContent());
        return intent;
    }

    private String getShareContent() {
        StringBuilder builder = new StringBuilder();
        builder.append(mTitle).append(" ").append(mOriginalUrl);
        // builder.append(" （").append("分享自StartupNews: ").append(getString(R.string.google_play_url))
        // .append("）");
        return builder.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_original_url:
                EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                        "browseactivity_menu_original_url", 0L);
                mWebViewController.loadUrl(mOriginalUrl);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
