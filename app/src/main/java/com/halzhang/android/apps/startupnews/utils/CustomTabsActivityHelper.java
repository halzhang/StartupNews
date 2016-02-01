package com.halzhang.android.apps.startupnews.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.text.TextUtils;
import android.util.Log;

/**
 * 界面 helper
 * Created by Hal on 15/11/25.
 */
public class CustomTabsActivityHelper {

    private static final String TAG = CustomTabsActivityHelper.class.getSimpleName();


    private CustomTabsClient mClient;
    private CustomTabsSession mSession;
    private CustomTabsServiceConnection mConnection;

    public void bindService(Context context) {
        if (mConnection != null) {
            return;
        }
        String packageName = CustomTabsHelper.getPackageNameToUse(context);
        Log.i(TAG, "Package name: " + packageName);
        if (TextUtils.isEmpty(packageName)) {
            return;
        }
        mConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                Log.i(TAG, "Custom tabs service connected!");
                mClient = customTabsClient;
                mClient.warmup(0);
                mSession = mClient.newSession(new CustomTabsCallback());
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mClient = null;
                mSession = null;
            }
        };
        CustomTabsClient.bindCustomTabsService(context, packageName, mConnection);
    }

    public CustomTabsSession getSession() {
        if (mClient == null) {
            return null;
        }
        if (mSession == null) {
            mSession = mClient.newSession(new CustomTabsCallback());
        }
        return mSession;
    }

    public void unBindService(Context context) {
        if (mConnection == null) {
            return;
        }
        context.unbindService(mConnection);
        mConnection = null;
    }

    /**
     * 启动
     *
     * @param activity         {@link Activity}
     * @param url              链接
     * @param customTabsIntent {@link CustomTabsIntent},由用户自定义属性
     * @param listener         {@link com.halzhang.android.apps.startupnews.utils.CustomTabsActivityHelper.OnCustomTabsInvalidListener},没有处理，使用默认浏览器打开
     */
    public void launchUrl(Activity activity, String url, CustomTabsIntent customTabsIntent, OnCustomTabsInvalidListener listener) {
        String packageName = CustomTabsHelper.getPackageNameToUse(activity);
        if (TextUtils.isEmpty(packageName)) {
            if (listener != null) {
                listener.onInvalid(url);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                activity.startActivity(intent);
            }
        } else {
            customTabsIntent.launchUrl(activity, Uri.parse(url));
        }
    }

    /**
     * 无效监听
     */
    public interface OnCustomTabsInvalidListener {
        /**
         * 当 custom tabs 无效时处理
         *
         * @param url 链接
         */
        public void onInvalid(String url);
    }


}
