package com.halzhang.android.startupnews.data.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Hal on 2016/5/30.
 */
public class PrefUtils {

    public static void set(Context ctx, final String key, final String value) {
        PreferenceManager.getDefaultSharedPreferences(ctx).edit().putString(key, value).apply();
    }

    public static String get(Context context, final String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, null);
    }
}
