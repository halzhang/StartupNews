/*
 * Copyright (C) 2013 HalZhang.
 *
 * http://www.gnu.org/licenses/gpl-3.0.txt
 */

package com.halzhang.android.startupnews.data.entity;

import java.io.Serializable;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Apr 23, 2013
 */
public class SNSession implements Serializable {

    private static final long serialVersionUID = 8793748185188606413L;

    private String user;

    private String id;

    public SNSession() {
    }

    public SNSession(String user, String id) {
        super();
        this.user = user;
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void clear() {
        user = null;
        id = null;
    }

}
