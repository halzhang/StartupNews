/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.apps.startupnews.snkit;

import android.content.Context;
import android.text.TextUtils;

import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.utils.PreferenceUtils;
import com.halzhang.android.startupnews.data.entity.SNSession;

/**
 * StartupNews
 * <p>
 * Session管理
 * </p>
 *
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Apr 23, 2013
 */
public class SessionManager {

    private SNSession mSession;

    private static SessionManager me;

    private Context mContext;

    private SessionManager() {
        mSession = new SNSession();
    }

    public static SessionManager getInstance() {
        if (me == null) {
            me = new SessionManager();
        }
        return me;
    }

    public void initSession(Context context) {
        mContext = context;
        initSessionFromPref();
    }

    public void storeSession(SNSession session) {
        mSession = session;
        saveSessionToPref();
    }

    public void storeSession(String user, String id) {
        if (mSession == null) {
            mSession = new SNSession(user, id);
        } else {
            mSession.setId(id);
            mSession.setUser(user);
        }
        storeSession(mSession);
    }

    private void saveSessionToPref() {
        if (mSession != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(mSession.getId()).append(";").append(mSession.getUser());
            PreferenceUtils.set(mContext, mContext.getString(R.string.pref_key_cookie),
                    builder.toString());
        }
    }

    private void initSessionFromPref() {
        String cookie = PreferenceUtils.get(mContext, mContext.getString(R.string.pref_key_cookie));
        if (!TextUtils.isEmpty(cookie)) {
            String[] datas = cookie.split(";");
            if (datas.length == 2) {
                if (mSession == null) {
                    mSession = new SNSession(datas[1], datas[0]);
                } else {
                    mSession.setId(datas[0]);
                    mSession.setUser(datas[1]);
                }
            }
        }
    }

    public void clear() {
        PreferenceUtils.set(mContext, mContext.getString(R.string.pref_key_cookie), "");
        mSession.clear();
    }

    public String getSessionId() {
        if (mSession != null) {
            return mSession.getId();
        }
        return null;
    }

    public String getSessionUser() {
        if (mSession != null) {
            return mSession.getUser();
        }
        return null;
    }

    /**
     * session是否有效
     *
     * @return true 有效
     */
    public boolean isValid() {
        return mSession != null && !TextUtils.isEmpty(mSession.getUser());
    }

    public String getCookieString() {
        return "user=" + mSession.getUser();
    }
}
