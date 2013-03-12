/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.entity;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
public class NewEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4629300319201201845L;

    private String url;

    private String title;

    private String comHead;

    private String subText;

    private String discussUrl;

    private User user;

    public NewEntity() {
    }

    public NewEntity(String url, String title, String comHead, String subText) {
        super();
        this.url = url;
        this.title = title;
        this.comHead = comHead;
        this.subText = subText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComHead() {
        if (TextUtils.isEmpty(comHead)) {
            return null;
        }
        return comHead.substring(1, comHead.length() - 1);
    }

    public void setComHead(String comHead) {
        this.comHead = comHead;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public String getDiscussUrl() {
        return discussUrl;
    }

    public void setDiscussUrl(String discussUrl) {
        this.discussUrl = discussUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("URL: ").append(url).append(" Title: ").append(title).append(" SubText: ")
                .append(subText).append(" Comhead: ").append(comHead).append(" DiscussURL: ")
                .append(discussUrl);
        return builder.toString();
    }

}
