package com.admiral.telebot.http.exception;

public class LimitReachedException extends Exception {
    public LimitReachedException() {
    }

    public LimitReachedException(String message) {
        super(message);
    }

    public LimitReachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public LimitReachedException(Throwable cause) {
        super(cause);
    }

    public LimitReachedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
