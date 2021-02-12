package com.bemonovoid.playqd.core.exception;

public abstract class PlayqdServiceException extends RuntimeException {

    protected PlayqdServiceException(String message) {
        super(message);
    }

    protected PlayqdServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    protected PlayqdServiceException(Throwable cause) {
        super(cause);
    }
}
