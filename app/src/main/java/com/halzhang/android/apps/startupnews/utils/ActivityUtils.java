/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.halzhang.android.apps.startupnews.ui.phone.BrowseActivity;
import com.halzhang.android.startupnews.data.entity.SNNew;

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
        if (snNew == null || activity == null) {
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
        AppUtils.getMyApplication(activity).addHistory(snNew.getUrl());
    }

    /**
     * With window Animations
     *
     * @param activity
     * @param snNew
     * @param v
     */
    public static void openActicle(Activity activity, SNNew snNew, View v) {
        if (snNew == null || activity == null) {
            return;
        }
        Bundle b = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // b = ActivityOptions.makeScaleUpAnimation(view, 0, 0,
            // view.getWidth(),
            // view.getHeight()).toBundle();
            Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                    Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.WHITE);
            b = ActivityOptions.makeThumbnailScaleUpAnimation(v, bitmap, 0, 0).toBundle();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            activity.startActivity(intent, b);
        } else {
            activity.startActivity(intent);
        }
    }

    /**
     * 判断Intent是否可用
     *
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
