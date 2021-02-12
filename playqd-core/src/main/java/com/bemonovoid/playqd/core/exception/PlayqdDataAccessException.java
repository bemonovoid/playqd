package com.bemonovoid.playqd.core.exception;

public class PlayqdDataAccessException extends PlayqdServiceException {

    public PlayqdDataAccessException(String message) {
        super(message);
    }

    public PlayqdDataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayqdDataAccessException(Throwable cause) {
        super(cause);
    }
}
