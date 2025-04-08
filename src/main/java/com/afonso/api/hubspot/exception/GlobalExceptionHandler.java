package com.afonso.api.hubspot.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> customException(CustomException exception) {
        Marker basic = new BasicMarkerFactory().getMarker("basic");
        log.error(basic, "CustomException", exception);
        return ResponseEntity.status(exception.getStatus()).body(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exception(Exception exception) {
        Marker basic = new BasicMarkerFactory().getMarker("basic");
        log.error(basic, "Generic error", exception);
        return ResponseEntity.internalServerError().body("Erro inesperado, por favor contate o admin afonsoneto121@gmail.com");
    }
}
