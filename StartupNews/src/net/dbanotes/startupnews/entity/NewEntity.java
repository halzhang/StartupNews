/**
 * Copyright (C) 2013 HalZhang
 */

package net.dbanotes.startupnews.entity;

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
        return comHead;
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
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("URL: ").append(url).append(" Title: ").append(title).append(" SubText: ").append(subText);
        return builder.toString();
    }

}
