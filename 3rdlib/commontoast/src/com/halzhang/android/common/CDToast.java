
package com.halzhang.android.common;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

/**
 * CommonToast<br/>
 * Toast
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Dec 27, 2012 11:39:39 AM
 */
public class CDToast {

    private static Toast sToast;

    private static Handler mHandler;

    /**
     * @param context
     * @param text
     */
    public static void showToast(Context context, String text) {
        if (sToast == null) {
            mHandler = new Handler(context.getMainLooper());
            sToast = Toast.makeText(context.getApplicationContext(), text, Toast.LENGTH_SHORT);
        } else {
            sToast.setText(text);
        }
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                sToast.show();
            }
        });
    }

    /**
     * @param context
     * @param resId
     */
    public static void showToast(Context context, int resId) {
        String text = context.getString(resId);
        showToast(context, text);
    }

}
