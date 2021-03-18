package com.bemonovoid.playqd.controller.advice;

import java.util.Map;

import com.bemonovoid.playqd.core.exception.PlayqdEntityNotFoundException;
import com.bemonovoid.playqd.core.exception.PlayqdImageServiceException;
import com.bemonovoid.playqd.core.exception.PlayqdUnsupportedAudioFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ApiControllerAdvice {

    @ExceptionHandler(PlayqdUnsupportedAudioFormatException.class)
    ResponseEntity<Object> handlePlayqdUnsupportedAudioFormatException(PlayqdUnsupportedAudioFormatException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("code", HttpStatus.UNPROCESSABLE_ENTITY.value(), "reason", ex.getMessage()));
    }

    @ExceptionHandler(PlayqdEntityNotFoundException.class)
    ResponseEntity<Object> handlePlayqdEntityNotFoundException(PlayqdEntityNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(PlayqdImageServiceException.class)
    ResponseEntity<Object> handlePlayqdImageServiceException(PlayqdImageServiceException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.notFound().build();
    }

}
