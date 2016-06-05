package com.halzhang.android.startupnews.data.exception;

/**
 * 网络异常
 * Created by Hal on 16/6/5.
 */
public class NetworkException extends Exception {

    /**
     * http status code
     */
    private int code;

    public NetworkException(int code, String message) {
        super(message);
        this.code = code;
    }

    public NetworkException(int code, Throwable throwable) {
        super(throwable);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
