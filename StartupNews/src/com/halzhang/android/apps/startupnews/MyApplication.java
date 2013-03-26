/**
 * Copyright (C) 2013 HalZhang
 */
package com.halzhang.android.apps.startupnews;

import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.analytics.MyExceptionParser;

import android.app.Application;

/**
 * StartupNews
 * <p>
 * app
 * </p>
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 8, 2013
 */
public class MyApplication extends Application {
    
    @Override
    public void onCreate() {
        super.onCreate();
        EasyTracker.getInstance().setContext(this);
        EasyTracker.getTracker().setExceptionParser(new MyExceptionParser());
       //CrashHandler.getInstance().init(this);
    }

}
