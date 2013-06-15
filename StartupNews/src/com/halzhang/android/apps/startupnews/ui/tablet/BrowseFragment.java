package com.halzhang.android.apps.startupnews.ui.tablet;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.utils.UIUtils;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 浏览器
 * Created by Hal on 13-5-25.
 * TODO 增加浏览器功能
 */
public class BrowseFragment extends Fragment {

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            int progress = (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * newProgress;
            ((SherlockFragmentActivity) getActivity()).setSupportProgress(progress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            getActivity().setTitle(title);
        }

    }

    private WebView mWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        if (container == null){
//            return null;
//        }
        final View view = inflater.inflate(R.layout.fragment_article, null);
        mWebView = (WebView) view.findViewById(R.id.webview);
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());

        WebSettings settings = mWebView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        if (UIUtils.hasHoneycomb()) {
            settings.setDisplayZoomControls(false);
        }
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        return view;
    }

    public void load(String url) {
        if (mWebView != null) {
            mWebView.loadUrl(url);
        }
    }

}
