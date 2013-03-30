/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.utils;

import com.halzhang.android.apps.startupnews.entity.SNNew;
import com.halzhang.android.apps.startupnews.ui.BrowseActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 22, 2013
 */
public class ActivityUtils {

    public static void openArticle(Activity activity, SNNew snNew) {
        if (snNew == null && activity == null) {
            return;
        }
        Intent intent = null;
        if (PreferenceUtils.isUseInnerBrowse(activity)) {
            intent = new Intent(activity, BrowseActivity.class);
            intent.putExtra(BrowseActivity.EXTRA_URL, snNew.getUrl());
            intent.putExtra(BrowseActivity.EXTRA_TITLE, snNew.getTitle());
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(PreferenceUtils.getHtmlProvider(activity) + snNew.getUrl()));
        }
        activity.startActivity(intent);
    }

    /**
     * 判断Intent是否可用
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }

}
