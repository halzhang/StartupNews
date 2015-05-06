/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.apps.startupnews.ui;

import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.analytics.Tracker;
import com.halzhang.android.apps.startupnews.entity.SNNew;
import com.halzhang.android.apps.startupnews.snkit.SNApi;
import com.halzhang.android.apps.startupnews.snkit.SessionManager;
import com.halzhang.android.common.CDToast;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Jul 7, 2013
 */
public class SNApiHelper {

    private WeakReference<Activity> mActivityRef;

    public SNApiHelper(Activity activity) {
        mActivityRef = new WeakReference<Activity>(activity);
    }

    /**
     * 
     * @param postID {@link SNNew#getPostID()}
     */
    public void upVote(String postID) {

        final Activity activity = mActivityRef.get();
        if (activity == null) {
            return;
        }
        SessionManager sm = SessionManager.getInstance(activity);
        if (sm.isValid()) {
            SNApi api = new SNApi(activity);
            final String url = activity.getString(R.string.vote_url, postID,
                    sm.getSessionId(), sm.getSessionUser());
            api.upVote(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, String content) {
                    if (statusCode == HttpStatus.SC_OK && TextUtils.isEmpty(content)) {
                        CDToast.showToast(activity, R.string.tip_vote_success);
                    } else {
                        Tracker.getInstance().sendEvent("ui_action_feedback", "upvote_feedback",
                                content, 0L);
                        if (content.contains("mismatch")) {
                            // 用户cookie无效
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                            CDToast.showToast(activity, R.string.tip_cookie_invalid);
                        } else {
                            CDToast.showToast(activity,
                                    activity.getString(R.string.tip_vote_duplicate));
                        }
                    }
                }

                @Override
                public void onFailure(Throwable error, String content) {
                    Tracker.getInstance()
                            .sendException("up vote error:" + content, error, false);
                    CDToast.showToast(activity, activity.getString(R.string.tip_vote_failure));
                }
            });
        } else {
            Intent intent = new Intent(activity, LoginActivity.class);
            activity.startActivity(intent);
        }

    }

}
