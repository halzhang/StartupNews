/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.apps.startupnews.entity;

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

    public String getLink() {
        return linkURL;
    }

    public void setLink(String link) {
        this.linkURL = link;
    }

    public String getParent() {
        return parentURL;
    }

    public void setParent(String parent) {
        this.parentURL = parent;
    }

    public String getDiscuss() {
        return discussURL;
    }

    public void setDiscuss(String discuss) {
        this.discussURL = discuss;
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
