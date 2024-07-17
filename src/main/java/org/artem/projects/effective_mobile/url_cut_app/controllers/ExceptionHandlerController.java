package org.artem.projects.effective_mobile.url_cut_app.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.artem.projects.effective_mobile.url_cut_app.exceptions.ExceptionResponse;
import org.artem.projects.effective_mobile.url_cut_app.exceptions.ShortedUrlNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(ShortedUrlNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFoundExceptions(RuntimeException e,
                                                                      HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(request.getRequestURI(), e.getMessage(),
                LocalDateTime.now());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }
}
