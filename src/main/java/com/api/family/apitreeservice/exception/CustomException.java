package com.api.family.apitreeservice.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CustomException extends RuntimeException {
    private final CustomError error;


    public CustomException(CustomError error) {
        this.error = error;
    }

    public CustomException(CustomError error, Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
    }
}
