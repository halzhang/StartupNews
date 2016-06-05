package com.halzhang.android.startupnews.data.entity;

/**
 * Created by Hal on 16/6/3.
 */
public class Status {

    /**
     * 操作失败
     */
    public static final int CODE_FAILURE = -1;
    /**
     * 操作成功
     */
    public static final int CODE_SUCCESS = 1;

    /**
     * cookie 无效，需要重新登录
     */
    public static final int CODE_COOKIE_VALID = 10;

    /**
     * 重复操作
     */
    public static final int CODE_REPEAT = 20;


    public int code;
    public String message;
}
