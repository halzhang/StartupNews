/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.GAServiceManager;
import com.halzhang.android.apps.startupnews.analytics.MyExceptionParser;

import android.app.Application;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * StartupNews
 * <p>
 * app
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 8, 2013
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EasyTracker.getInstance().setContext(this);
        EasyTracker.getTracker().setExceptionParser(new MyExceptionParser(getApplicationContext()));
        UncaughtExceptionHandler handler = new ExceptionReporter(EasyTracker.getTracker(),
                GAServiceManager.getInstance(), Thread.getDefaultUncaughtExceptionHandler());
        Thread.setDefaultUncaughtExceptionHandler(handler);
        // CrashHandler.getInstance().init(this);
    }

}
