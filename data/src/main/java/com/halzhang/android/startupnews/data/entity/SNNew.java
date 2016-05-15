/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.startupnews.data.entity;

import android.os.Parcel;
import android.os.Parcelable;
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
public class SNNew implements Parcelable {

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

    private boolean isDiscuss = false;// 是否是討論貼

    private String text;// 讨论帖内容

    private String createat;

    public SNNew() {
    }

    public SNNew(String url, String title, String urlDomain, String voteURL, int points,
            int commentsCount, String subText, String discussURL, SNUser user, String postID,
            boolean isDiscuss, String text, String createat) {
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
        this.isDiscuss = isDiscuss;
        this.text = text;
        this.createat = createat;
    }

    public SNNew(String url, String title, String urlDomain, String voteURL, int points,
            int commentsCount, String subText, String discussURL, SNUser user, String postID,
            String createat) {
        super();
        this.url = url;
        this.title = title;
        // this.urlDomain = urlDomain;
        setUrlDomain(urlDomain);
        this.voteURL = voteURL;
        this.points = points;
        this.commentsCount = commentsCount;
        this.subText = subText;
        this.discussURL = discussURL;
        this.user = user;
        this.postID = postID;
        this.createat = createat;
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

    public SNUser getUser() {
        return user;
    }

    public void setUser(SNUser user) {
        this.user = user;
    }

    public String getUrlDomain() {
        return urlDomain;
    }

    public void setUrlDomain(String urlDomain) {
        this.urlDomain = urlDomain;
        // news里面的讨论帖
        this.isDiscuss = this.urlDomain != null && this.urlDomain.endsWith("dbanotes.net");
    }

    public String getVoteURL() {
        return voteURL;
    }

    public void setVoteURL(String voteURL) {
        this.voteURL = voteURL;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public String getDiscussURL() {
        return discussURL;
    }

    public void setDiscussURL(String discussURL) {
        this.discussURL = discussURL;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isDiscuss() {
        return isDiscuss;
    }

    public void setDiscuss(boolean isDiscuss) {
        this.isDiscuss = isDiscuss;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreateat() {
        return createat;
    }

    public void setCreateat(String createat) {
        this.createat = createat;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("URL: ").append(url).append(" Title: ").append(title).append(" SubText: ")
                .append(subText).append(" Comhead: ").append(urlDomain).append(" DiscussURL: ")
                .append(discussURL).append(" isDiscuss: ").append(isDiscuss).append(" ID:")
                .append(postID).append(" UpVoteUrl:").append(voteURL);
        return builder.toString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.title);
        dest.writeString(this.urlDomain);
        dest.writeString(this.voteURL);
        dest.writeInt(this.points);
        dest.writeInt(this.commentsCount);
        dest.writeString(this.subText);
        dest.writeString(this.discussURL);
        dest.writeSerializable(this.user);
        dest.writeString(this.postID);
        dest.writeByte(isDiscuss ? (byte) 1 : (byte) 0);
        dest.writeString(this.text);
        dest.writeString(this.createat);
    }

    protected SNNew(Parcel in) {
        this.url = in.readString();
        this.title = in.readString();
        this.urlDomain = in.readString();
        this.voteURL = in.readString();
        this.points = in.readInt();
        this.commentsCount = in.readInt();
        this.subText = in.readString();
        this.discussURL = in.readString();
        this.user = (SNUser) in.readSerializable();
        this.postID = in.readString();
        this.isDiscuss = in.readByte() != 0;
        this.text = in.readString();
        this.createat = in.readString();
    }

    public static final Creator<SNNew> CREATOR = new Creator<SNNew>() {
        @Override
        public SNNew createFromParcel(Parcel source) {
            return new SNNew(source);
        }

        @Override
        public SNNew[] newArray(int size) {
            return new SNNew[size];
        }
    };
}
