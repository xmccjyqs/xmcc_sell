package com.xmcc.springdemo.exception;

import com.xmcc.springdemo.beans.ResultEnums;

public class CustomException extends RuntimeException {

    private int code;

    public CustomException() {
        super();
    }

    public CustomException(String message) {
        this(ResultEnums.FAIL.getCode(),message);
    }

    public CustomException(int code,String message) {
        super(message);
        this.code = code;
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomException(Throwable cause) {
        super(cause);
    }

    protected CustomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
