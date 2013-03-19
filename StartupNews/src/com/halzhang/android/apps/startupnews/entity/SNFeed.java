/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.entity;

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
public class SNFeed implements Serializable {

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

}
