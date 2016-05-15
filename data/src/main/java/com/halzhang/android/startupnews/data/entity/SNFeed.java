/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.startupnews.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 18, 2013
 */
public class SNFeed implements Parcelable {

    private static final long serialVersionUID = 5171992791865009372L;

    private ArrayList<SNNew> mSnNews = new ArrayList<SNNew>(0);

    private String mMoreUrl;

    public ArrayList<SNNew> getSnNews() {
        return mSnNews;
    }

    public void setSnNews(ArrayList<SNNew> mSnNews) {
        this.mSnNews = mSnNews;
    }

    public String getMoreUrl() {
        return mMoreUrl;
    }

    public void setMoreUrl(String mMoreUrl) {
        this.mMoreUrl = mMoreUrl;
    }

    public void addNew(SNNew snNew) {
        if (snNew != null) {
            mSnNews.add(snNew);
        }
    }

    public void addNews(ArrayList<SNNew> news) {
        if (news != null && news.size() > 0) {
            mSnNews.addAll(news);
        }
    }

    public void clear() {
        mSnNews.clear();
    }
    
    public int size(){
        return mSnNews.size();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(this.mSnNews);
        dest.writeString(this.mMoreUrl);
    }

    public SNFeed() {
    }

    protected SNFeed(Parcel in) {
        this.mSnNews = new ArrayList<SNNew>();
        in.readList(this.mSnNews, SNNew.class.getClassLoader());
        this.mMoreUrl = in.readString();
    }

    public static final Creator<SNFeed> CREATOR = new Creator<SNFeed>() {
        @Override
        public SNFeed createFromParcel(Parcel source) {
            return new SNFeed(source);
        }

        @Override
        public SNFeed[] newArray(int size) {
            return new SNFeed[size];
        }
    };
}
