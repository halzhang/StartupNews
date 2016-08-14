/**
 * Copyright (C) 2013 HalZhang
 */
package com.halzhang.android.apps.startupnews.ui;

import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.mvp.presenter.Presenter;
import com.halzhang.android.mvp.support.v7.MVPAppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * StartupNews
 * <p>
 * </p>
 *
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 13, 2013
 */
public class BaseFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            Window w = getWindow();
//            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
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
