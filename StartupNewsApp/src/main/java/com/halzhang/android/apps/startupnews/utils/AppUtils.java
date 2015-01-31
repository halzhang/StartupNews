/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.utils;

import com.halzhang.android.apps.startupnews.MyApplication;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 12, 2013
 */
public class AppUtils {

    public static String getVersionName(Context context) {
        try {

            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return info.versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    public static int getVersionCode(Context context) {
        try {

            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            return 0;
        }
    }

    public static MyApplication getMyApplication(Context context) {
        return (MyApplication) context.getApplicationContext();
    }
}
