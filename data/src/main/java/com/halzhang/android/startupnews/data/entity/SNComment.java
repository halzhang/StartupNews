/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.startupnews.data.entity;

import java.io.Serializable;

/**
 * StartupNews
 * <p>
 * 评论
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
public class SNComment implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5194737515600100830L;

    private String linkURL;

    private String parentURL;

    private String discussURL;

    private String text;

    private String created;

    private SNUser user;

    private String artistTitle;//文章标题
    
    private String voteURL;
    
    private String replayURL;
    
    public SNComment(){}

    public SNComment(String linkURL, String parentURL, String discussURL, String text,
            String created, SNUser user, String artistTitle, String voteURL,String replayURL) {
        super();
        this.linkURL = linkURL;
        this.parentURL = parentURL;
        this.discussURL = discussURL;
        this.text = text;
        this.created = created;
        this.user = user;
        this.artistTitle = artistTitle;
        this.voteURL = voteURL;
        this.replayURL = replayURL;
    }

    public String getLinkURL() {
        return linkURL;
    }

    public void setLinkURL(String linkURL) {
        this.linkURL = linkURL;
    }

    public String getParentURL() {
        return parentURL;
    }

    public void setParentURL(String parentURL) {
        this.parentURL = parentURL;
    }

    public String getDiscussURL() {
        return discussURL;
    }

    public void setDiscussURL(String discussURL) {
        this.discussURL = discussURL;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SNUser getUser() {
        return user;
    }

    public void setUser(SNUser user) {
        this.user = user;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getArtistTitle() {
        return artistTitle;
    }

    public void setArtistTitle(String artistTitle) {
        this.artistTitle = artistTitle;
    }

    public String getVoteURL() {
        return voteURL;
    }

    public void setVoteURL(String voteURL) {
        this.voteURL = voteURL;
    }

    public String getReplayURL() {
        return replayURL;
    }

    public void setReplayURL(String replayURL) {
        this.replayURL = replayURL;
    }

}
