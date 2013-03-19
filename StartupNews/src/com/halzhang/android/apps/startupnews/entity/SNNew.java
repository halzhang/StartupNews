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
public class SNNew implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -4629300319201201845L;

    private String url;

    private String title;

    private String urlDomain;

    private String voteURL;

    private int points;

    private int commentsCount;

    private String subText;

    private String discussURL;

    private SNUser user;
    
    private String postID;

    public SNNew() {
    }

    public SNNew(String url, String title, String urlDomain, String subText) {
        super();
        this.url = url;
        this.title = title;
        this.urlDomain = urlDomain;
        this.subText = subText;
    }



    public SNNew(String url, String title, String urlDomain, String voteURL, int points,
            int commentsCount, String subText, String discussURL, SNUser user,String postID) {
        super();
        this.url = url;
        this.title = title;
        this.urlDomain = urlDomain;
        this.voteURL = voteURL;
        this.points = points;
        this.commentsCount = commentsCount;
        this.subText = subText;
        this.discussURL = discussURL;
        this.user = user;
        this.postID = postID;
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
        if (TextUtils.isEmpty(urlDomain)) {
            return null;
        }
        return urlDomain.substring(1, urlDomain.length() - 1);
    }

    public void setComHead(String comHead) {
        this.urlDomain = comHead;
    }

    public String getSubText() {
        return subText;
    }

    public void setSubText(String subText) {
        this.subText = subText;
    }

    public String getDiscussUrl() {
        return discussURL;
    }

    public void setDiscussUrl(String discussUrl) {
        this.discussURL = discussUrl;
    }

    public SNUser getUser() {
        return user;
    }

    public void setUser(SNUser user) {
        this.user = user;
    }

    public String getVoteUrl() {
        return voteURL;
    }

    public void setVoteUrl(String voteUrl) {
        this.voteURL = voteUrl;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getComments() {
        return commentsCount;
    }

    public void setComments(int comments) {
        this.commentsCount = comments;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("URL: ").append(url).append(" Title: ").append(title).append(" SubText: ")
                .append(subText).append(" Comhead: ").append(urlDomain).append(" DiscussURL: ")
                .append(discussURL);
        return builder.toString();
    }

}
