
package com.halzhang.android.apps.startupnews;

/**
 * Constants used by StartupNews application
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Jan 16, 2013 11:46:03 AM
 */
public abstract class Constants {

    /**
     * abs class
     */
    protected Constants() {

    }

    /**
     * Root Dir used by SN app
     */
    public static final String SDCARD_TOP_DIR = "/SN/";

    /**
     * Crash log dir
     */
    public static final String CRASH_LOG_DIR = SDCARD_TOP_DIR+"/crash/";

    /**
     * 历史记录保存文件名
     */
    public static final String HISTORY_FILE_NAME = "history.db";

    public final class IntentAction {

        /**
         * 登陆后结果cookie user
         * 
         * @see #ACTION_LOGIN
         */
        public static final String EXTRA_LOGIN_USER = "startupnews.intent.extra.login.USER";

        /**
         * 登陆结果
         * <p>
         * 输出：登陆后的cookie extras:LOGIN_USER(value:String)
         * </p>
         * 
         * @see #EXTRA_LOGIN_USER
         */
        public static final String ACTION_LOGIN = "startupnews.intent.action.LOGIN";
        
        /**
         * 注销
         */
        public static final String ACTION_LOGOUT = "startupnews.intent.action.LOGOUT";
        
    }
    
    public static final String TAG_BROWSE_FRAGMENT = "tag_browse_fragment";

}
