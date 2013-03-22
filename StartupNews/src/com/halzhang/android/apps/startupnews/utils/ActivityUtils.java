/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.utils;

import com.halzhang.android.apps.startupnews.entity.SNNew;
import com.halzhang.android.apps.startupnews.ui.BrowseActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

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

}
