/**
 * Copyright (C) 2013 HalZhang
 */
package com.halzhang.android.apps.startupnews.utils;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * StartupNews
 * <p>
 * </p>
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 12, 2013
 */
public class DateUtils {
    
    public static String getLastUpdateLabel(Context context){
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ds = format.format(new Date(System.currentTimeMillis()));
        return "上次更新:"+ds;
    }

}
