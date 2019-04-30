package com.anosi.asset.exception;

/**
 * 数据中心异常
 */
public class DataCenterException extends RuntimeException{

    public DataCenterException() {
    }

    public DataCenterException(String message) {
        super(message);
    }

    public DataCenterException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataCenterException(Throwable cause) {
        super(cause);
    }

    public DataCenterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
