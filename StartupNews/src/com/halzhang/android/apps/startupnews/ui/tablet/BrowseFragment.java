
package com.halzhang.android.apps.startupnews.ui.tablet;

import com.actionbarsherlock.app.SherlockFragment;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.ui.widgets.WebViewController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * 浏览器 Created by Hal on 13-5-25.
 */
public class BrowseFragment extends SherlockFragment {

    private WebView mWebView;

    private WebViewController mWebViewController;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWebViewController = new WebViewController(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_article, null);
        mWebView = (WebView) view.findViewById(R.id.webview);
        mWebViewController.initControllerView(mWebView, view);
        return view;
    }

    public void load(String url) {
        mWebViewController.loadUrl(url);
    }

    @Override
    public void onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu,
            com.actionbarsherlock.view.MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_news_option, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

}
