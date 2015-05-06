/**
 * Copyright (C) 2013 HalZhang
 */
package com.halzhang.android.apps.startupnews.ui;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.R;

import android.os.Bundle;

/**
 * StartupNews
 * <p>
 * </p>
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 13, 2013
 */
public class BaseFragmentActivity extends SherlockFragmentActivity {
    
    @Override
    protected void onCreate(Bundle arg0) {
        setTheme(R.style.Theme_Sherlock_Light_DarkActionBar);
        super.onCreate(arg0);
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

}
