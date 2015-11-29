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
 * @version Mar 18, 2013
 */
public class SNComments implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -2397354673428961724L;

    private ArrayList<SNComment> snComments = new ArrayList<SNComment>(32);

    private String moreURL;

    public ArrayList<SNComment> getSnComments() {
        return snComments;
    }

    public void setSnComments(ArrayList<SNComment> snComments) {
        this.snComments = snComments;
    }

    public String getMoreURL() {
        return moreURL;
    }

    public void setMoreURL(String moreURL) {
        this.moreURL = moreURL;
    }
    
    public void clear(){
        snComments.clear();
    }
    
    public void addComment(SNComment comment){
        if(comment != null){
            snComments.add(comment);
        }
    }
    
    public void addComments(ArrayList<SNComment> comments){
        if(comments != null && comments.size() > 0){
            snComments.addAll(comments);
        }
    }
    
    public int size(){
        return snComments.size();
    }

}
