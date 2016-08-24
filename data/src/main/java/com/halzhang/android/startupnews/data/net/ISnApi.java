package com.halzhang.android.startupnews.data.net;

import com.halzhang.android.startupnews.data.entity.SNComments;
import com.halzhang.android.startupnews.data.entity.SNDiscuss;
import com.halzhang.android.startupnews.data.entity.SNFeed;
import com.halzhang.android.startupnews.data.entity.Status;

import rx.Observable;
import rx.subscriptions.BooleanSubscription;

/**
 * api
 * Created by Hal on 15/11/28.
 */
public interface ISnApi {

    /**
     * 获取新闻列表
     *
     * @param url 页面链接
     * @return {@link SNFeed}
     */
    Observable<SNFeed> getSNFeed(String url);

    Observable<String> getFnid();

    Observable<String> login(String fnid, String username, String password);

    Observable<SNComments> getSNComments(String url);

    /**
     * 投票
     *
     * @param postId 文章 id
     * @return 状态，成功与否
     */
    Observable<Status> upVote(String postId);

    /**
     * 评论
     *
     * @param text 评论内容
     * @param fnid 主题的 find
     * @return 状态
     */
    Observable<Status> comment(String text, String fnid);

    /**
     * 登出
     *
     * @return true 登出成功
     */
    Observable<Boolean> logout();

    /**
     * 获取讨论列表
     *
     * @param url
     */
    Observable<SNDiscuss> getDiscuss(String url);


}
