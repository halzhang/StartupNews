
package com.halzhang.android.common;

import android.util.Log;

/**
 * CommonLog<br/>
 * log
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version ec 24, 2012 9:55:18 PM
 */
public class CDLog {
    public static final String LOG_TAG = CDLog.class.getSimpleName();

    public static final boolean DEBUG = true;

    private static final String LOG_PREFIX = "CDLOG_";

    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();

    private static final int MAX_LOG_TAG_LENGTH = 23;

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }
        return LOG_PREFIX + str;
    }

    /**
     * WARNING: Don't use this when obfuscating class names with Proguard!
     */
    public static String makeLogTag(Class<?> cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void i(String tag, String msg, Throwable throwable) {
        if (DEBUG) {
            Log.i(tag, msg, throwable);
        }
    }

    public static void i(String tag, Object... objects) {
        StringBuilder builder = new StringBuilder();
        for (Object obj : objects) {
            builder.append(obj);
        }
        i(tag, builder.toString());
    }

    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    public static void i(String msg) {
        i(LOG_TAG, msg, null);
    }

    public static void d(String tag, String msg, Throwable throwable) {
        if (DEBUG) {
            Log.d(tag, msg, throwable);
        }
    }

    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    public static void d(String msg) {
        d(LOG_TAG, msg, null);
    }

    public static void w(String tag, String msg, Throwable throwable) {
        if (DEBUG) {
            Log.w(tag, msg, throwable);
        }
    }

    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    public static void w(String msg) {
        w(LOG_TAG, msg, null);
    }

    public static void e(String tag, String msg, Throwable throwable) {
        if (DEBUG) {
            Log.e(tag, msg, throwable);
        }
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public static void e(String msg) {
        e(LOG_TAG, msg, null);
    }

    private CDLog() {
    }
}
