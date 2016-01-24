
package com.halzhang.android.apps.startupnews.utils;

import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.BuildConfig;
import com.halzhang.android.apps.startupnews.Constants;
import com.halzhang.android.apps.startupnews.MyApplication;
import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.common.CDLog;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Crash info collection
 */
public class CrashHandler implements UncaughtExceptionHandler {

    private static final String LOG_TAG = CrashHandler.class.getSimpleName();

    private static CrashHandler me = null;

    private Context mContext;

    private Map<String, String> infos = new HashMap<String, String>();

    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        if (me == null) {
            me = new CrashHandler();
        }
        return me;
    }

    public void init(Context context) {
        mContext = context;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (BuildConfig.DEBUG) {
            Log.e(LOG_TAG, ex.getMessage(), ex);
            handleException(ex);
        }
        Tracker.getInstance().sendException(ex.getMessage(), ex, true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    private boolean handleException(Throwable ex) {
        if (ex != null) {
            collectDeviceInfo(mContext);
            saveCrashInfo2File(ex);
            return true;
        }
        return false;
    }

    private void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                //Log.d(LOG_TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
            }
        }
    }

    /**
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());
            String fileName = "crash-" + time + "-" + timestamp + ".txt";
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + Constants.CRASH_LOG_DIR;
                File dir = new File(path);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                FileOutputStream fos = new FileOutputStream(path + fileName);
                fos.write(sb.toString().getBytes());
                fos.close();
            }
            return fileName;
        } catch (Exception e) {
            Log.e(LOG_TAG, "an error occured while writing file...", e);
        }
        return null;
    }
}
