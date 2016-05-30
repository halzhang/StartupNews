package com.halzhang.android.startupnews.data.net;

import com.halzhang.android.startupnews.data.entity.SNFeed;

import rx.Observable;

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


}
