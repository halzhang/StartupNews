/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.startupnews.data.entity;

import java.io.Serializable;

/**
 * StartupNews
 * <p>
 * 用户信息
 * </p>
 * user: jkf created: 9 days ago karma: 17 about:
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 7, 2013
 */
public class SNUser implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -6123600100177508491L;

    private String id;
    
    private String name;

    private String created;

    private String karma;

    private String about;

    public SNUser() {

    }

    public SNUser(String id, String created, String karma, String about) {
        super();
        this.id = id;
        this.created = created;
        this.karma = karma;
        this.about = about;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getKarma() {
        return karma;
    }

    public void setKarma(String karma) {
        this.karma = karma;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    

}
