package com.api.family.apitreeservice.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {
    @ExceptionHandler(value = { BadCredentialsException.class })
    public ResponseEntity<CustomError> handleBadCredentialsException(
            BadCredentialsException ex) {
        return new ResponseEntity<>(Errors.BAD_CREDENTIALS, Errors.BAD_CREDENTIALS.getStatus());
    }

    @ExceptionHandler(value = { AccessDeniedException.class, UsernameNotFoundException.class,
            ExpiredJwtException.class })
    public ResponseEntity<CustomError> handleAccessDeniedException(
            RuntimeException ex) {
        return new ResponseEntity<>(Errors.ACCESS_DENIED, Errors.ACCESS_DENIED.getStatus());
    }

    @ExceptionHandler(value = { CustomException.class })
    public ResponseEntity<CustomError> handleCustomException(
            CustomException ex) {

        if (ex.getCause() != null) {
            var message = String.format("%s (%s)", ex.getMessage(), ex.getCause().getMessage());

            if (ex.getError().getStatus().is5xxServerError()) {
                log.error(message, ex.getCause());
            }
        }

        return new ResponseEntity<>(ex.getError(), ex.getError().getStatus());
    }
    @ExceptionHandler(value = { HttpRequestMethodNotSupportedException.class, HttpMessageNotReadableException.class,
            MultipartException.class, DataIntegrityViolationException.class })
    public ResponseEntity<CustomError> handleHttpRequestMethodNotSupportedException(
            RuntimeException ex) {
        return new ResponseEntity<>(Errors.INVALID_REQUEST, Errors.INVALID_REQUEST.getStatus());
    }

    @ExceptionHandler(value = { NoResourceFoundException.class })
    public ResponseEntity<CustomError> handleNoResourceFoundException(
            NoResourceFoundException ex) {
        return new ResponseEntity<>(Errors.NOT_FOUND, Errors.NOT_FOUND.getStatus());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<CustomError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        try {
            Map<String, String> errors = getStringStringMap(e);

            String message = "Validation failed. Object name : " + e.getBindingResult().getObjectName();

            CustomError error = Errors.INVALID_VALUE;

            error.setDetails(errors);
            error.setMessage(message);

            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        } catch (Exception ex) {
            log.error("GlobalExceptionHandler.handleMethodArgumentNotValidException", ex);
            return new ResponseEntity<>(Errors.INVALID_VALUE, HttpStatus.BAD_REQUEST);
        }
    }

    private static Map<String, String> getStringStringMap(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            Object rejectedValue = error.getRejectedValue();
            String rejectedValueString = null;

            if (rejectedValue != null) {
                rejectedValueString = rejectedValue.toString();
            }

            errors.put(error.getField(),
                    error.getDefaultMessage() + ". Rejected value [" + rejectedValueString + "]");
        }

        for (ObjectError error : e.getBindingResult().getGlobalErrors()) {
            errors.put(error.getObjectName(), error.getDefaultMessage());
        }
        return errors;
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<CustomError> handleInternalError(
            Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(Errors.INTERNAL_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = DisabledException.class)
    public ResponseEntity<CustomError> handleDisabledUserError(
            DisabledException ex) {
        return new ResponseEntity<>(Errors.DISABLED_USER, HttpStatus.BAD_REQUEST);
    }
}
