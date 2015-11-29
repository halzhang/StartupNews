/**
 * Copyright (C) 2013 HalZhang
 */

package com.halzhang.android.startupnews.data.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * StartupNews
 * <p>
 * </p>
 * 
 * @author <a href="http://weibo.com/halzhang">Hal</a>
 * @version Mar 19, 2013
 */
public class SNDiscuss implements Serializable {
    private static final long serialVersionUID = 5814294611810270990L;

    private SNNew snNew;

    private ArrayList<SNComment> mComments = new ArrayList<SNComment>();

    private String fnid;// comment hidden field

    public SNNew getSnNew() {
        return snNew;
    }

    public void setSnNew(SNNew snNew) {
        this.snNew = snNew;
    }

    public ArrayList<SNComment> getComments() {
        return mComments;
    }

    public void setComments(ArrayList<SNComment> comments) {
        this.mComments = comments;
    }

    public String getFnid() {
        return fnid;
    }

    public void setFnid(String fnid) {
        this.fnid = fnid;
    }

    public int commentSize() {
        return mComments.size();
    }

    public void addComments(ArrayList<SNComment> comments) {
        if (comments != null && comments.size() > 0) {
            this.mComments.addAll(comments);
        }
    }

    public void addComment(SNComment comment) {
        if (comment != null) {
            mComments.add(comment);
        }
    }
    
    public void clearComments(){
        mComments.clear();
    }

    /**
     * 数据拷贝
     * 
     * @param discuss
     */
    public void copy(SNDiscuss discuss) {
        if (discuss != null && discuss != this) {
            addComments(discuss.getComments());
            setFnid(discuss.getFnid());
            setSnNew(discuss.getSnNew());
        }
    }

}
