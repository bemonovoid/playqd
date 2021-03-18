package com.bemonovoid.playqd.core.exception;

public class PlayqdEntityNotFoundException extends PlayqdDataAccessException {

    public PlayqdEntityNotFoundException(String entityId, String entityName) {
        super(String.format("%s with id = '%s' was not found.", entityName, entityId));
    }
}
