package com.halzhang.android.apps.startupnews.ui.tablet;

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.ui.widgets.WebViewController;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * 浏览器
 * Created by Hal on 13-5-25.
 */
public class BrowseFragment extends Fragment {

    private WebView mWebView;
    
    private WebViewController mWebViewController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_article, null);
        mWebView = (WebView) view.findViewById(R.id.webview);
        mWebViewController = new WebViewController(getActivity());
        mWebViewController.initControllerView(mWebView, view);
        return view;
    }

    public void load(String url) {
        mWebViewController.loadUrl(url);
    }

}
