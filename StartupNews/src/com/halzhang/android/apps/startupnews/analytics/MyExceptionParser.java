/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.apps.startupnews.analytics;

import com.google.analytics.tracking.android.ExceptionParser;
import com.halzhang.android.apps.startupnews.utils.AppUtils;

import android.content.Context;
import android.os.Build;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * StartupNews
 * <p>
 * 异常解析
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 26, 2013
 */
public class MyExceptionParser implements ExceptionParser {

    private Context mContext;

    private Map<String, String> infos = new HashMap<String, String>();

    public MyExceptionParser(Context context) {
        mContext = context;
    }

    @Override
    public String getDescription(String message, Throwable throwable) {
        collectDeviceInfo(mContext);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }
        sb.append(getStackTraceString(throwable));
        return sb.toString();
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        return sw.toString();
    }

    public void collectDeviceInfo(Context ctx) {
        infos.clear();
        infos.put("versionName", AppUtils.getVersionName(mContext));
        infos.put("versionCode", String.valueOf(AppUtils.getVersionCode(mContext)));
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
            }
        }
    }

}
