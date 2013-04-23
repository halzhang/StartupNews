/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.utils;

import com.halzhang.android.apps.startupnews.R;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 17, 2013
 */
public class PreferenceUtils {

    /**
     * 获取网页阅读模式
     * 
     * @param context
     * @return
     */
    public static String getHtmlProvider(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(
                context.getString(R.string.pref_key_html_provider),
                context.getString(R.string.default_html_provider));
    }

    /**
     * 使用内置浏览器
     * 
     * @param context
     * @return
     */
    public static boolean isUseInnerBrowse(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                context.getString(R.string.pref_key_default_browse), true);
    }

    public static void set(Context ctx, final String key, final String value) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString(key, value).commit();
    }

    public static String get(Context context, final String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
    }

}
