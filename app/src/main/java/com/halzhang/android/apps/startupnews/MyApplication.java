/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews;

import android.app.Application;
import android.os.StrictMode;
import android.text.TextUtils;

import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.apps.startupnews.utils.CrashHandler;
import com.halzhang.android.common.CDLog;
import com.halzhang.android.startupnews.data.CookieFactoryModule;
import com.halzhang.android.startupnews.data.OkHttpClientModule;
import com.halzhang.android.startupnews.data.SnApiModule;
import com.squareup.leakcanary.LeakCanary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

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

    private static final String LOG_TAG = MyApplication.class.getSimpleName();

    private HashSet<String> mHistorySet = new HashSet<String>();

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "StartupNews thread #" + mCount.getAndIncrement());
        }
    };

    private ExecutorService mExecutorService;

    private static MyApplication me;

    public static MyApplication instance() {
        return me;
    }

    public MyApplication() {
        super();
        me = this;
    }

    private SnApiComponent mSnApiComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Tracker.getInstance().init(this);
        CrashHandler.getInstance().init(this);
        mExecutorService = Executors.newSingleThreadExecutor(sThreadFactory);
        initHistory();
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode
                    .ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectActivityLeaks()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
            LeakCanary.install(this);
        }

        mSnApiComponent = DaggerSnApiComponent.builder().applicationModule(new ApplicationModule(this))
                .okHttpClientModule(new OkHttpClientModule())
                .snApiModule(new SnApiModule())
                .cookieFactoryModule(new CookieFactoryModule()).build();
        mSnApiComponent.getSessionManager().initSession(this);
    }

    public SnApiComponent getSnApiComponent() {
        return mSnApiComponent;
    }

    private void initHistory() {
        File file = new File(getFilesDir().getAbsolutePath() + File.separator
                + Constants.HISTORY_FILE_NAME);
        if (!file.exists()) {
            return;
        }
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String s = null;
            while (!TextUtils.isEmpty((s = reader.readLine()))) {
                mHistorySet.add(s);
            }
        } catch (FileNotFoundException e) {
            CDLog.e(LOG_TAG, "", e);
            Tracker.getInstance().sendException("History file not found!", e, false);
        } catch (IOException e) {
            CDLog.e(LOG_TAG, "", e);
            Tracker.getInstance().sendException("Read History file error!", e, false);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void storeHistory() {
        if (!mHistorySet.isEmpty()) {
            Iterator<String> iterator = mHistorySet.iterator();
            StringBuilder builder = new StringBuilder();
            while (iterator.hasNext()) {
                builder.append(iterator.next()).append("\n");
            }
            File file = new File(getFilesDir().getAbsolutePath() + File.separator
                    + Constants.HISTORY_FILE_NAME);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    CDLog.e(LOG_TAG, "Create history file error!");
                    Tracker.getInstance().sendException("Create history file error!", e, false);
                }
            }
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(file);
                writer.write(builder.toString());
                writer.close();
            } catch (FileNotFoundException e) {
                CDLog.e(LOG_TAG, "History file not found!");
                Tracker.getInstance().sendException("History file not found!", e, false);
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }

    /**
     * 增加访问历史记录
     *
     * @param url
     */
    public void addHistory(String url) {
        if (!TextUtils.isEmpty(url) && !mHistorySet.contains(url)) {
            mHistorySet.add(url);
            mExecutorService.submit(new Runnable() {

                @Override
                public void run() {
                    storeHistory();
                }
            });
        }
    }

    /**
     * 清空历史记录
     */
    public void clearHistory() {
        mHistorySet.clear();
    }

    public boolean isHistoryContains(String url) {
        return mHistorySet.contains(url);
    }

}
