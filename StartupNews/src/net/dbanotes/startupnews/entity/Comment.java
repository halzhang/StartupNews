/**
 * Copyright (C) 2013 HalZhang
 */

package net.dbanotes.startupnews.entity;

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
public class Comment implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 5194737515600100830L;

    private String link;

    private String parent;

    private String discuss;

    private String text;
    
    private String created;

    private User user;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getDiscuss() {
        return discuss;
    }

    public void setDiscuss(String discuss) {
        this.discuss = discuss;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
    
    

}
