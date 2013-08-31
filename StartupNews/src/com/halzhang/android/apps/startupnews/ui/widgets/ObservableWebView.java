/*
 *
 * Copyright (C) 2013 HalZhang.
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.apps.startupnews.ui.widgets;

import com.halzhang.android.common.CDLog;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Aug 25, 2013
 */
public class ObservableWebView extends WebView {

    private OnScrollChangedCallback mOnScrollChangedCallback;

    public ObservableWebView(final Context context) {
        super(context);
    }

    public ObservableWebView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public ObservableWebView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onScrollChanged(final int l, final int t, final int oldl, final int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        CDLog.i(VIEW_LOG_TAG, "l: " + l + " t: " + t + " oldl: " + oldl + " oldt: " + oldt);
        if (mOnScrollChangedCallback != null) {
            mOnScrollChangedCallback.onScroll(l, t, oldl, oldt);
            if (t >= 0 && oldt >= 0) {
                if (oldt > t) {
                    mOnScrollChangedCallback.onScrollUp();
                } else {
                    mOnScrollChangedCallback.onScrollDown();
                }
            }
        }

    }

    public OnScrollChangedCallback getOnScrollChangedCallback() {
        return mOnScrollChangedCallback;
    }

    public void setOnScrollChangedCallback(final OnScrollChangedCallback onScrollChangedCallback) {
        mOnScrollChangedCallback = onScrollChangedCallback;
    }

    /**
     * Impliment in the activity/fragment/view that you want to listen to the
     * webview
     */
    public static interface OnScrollChangedCallback {
        public void onScroll(int l, int t, int oldl, int oldt);

        /**
         * 向上滚动
         */
        public void onScrollUp();

        /**
         * 向下滚动
         */
        public void onScrollDown();
    }

}
