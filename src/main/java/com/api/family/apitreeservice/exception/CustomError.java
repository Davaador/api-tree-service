package com.api.family.apitreeservice.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@RequiredArgsConstructor
@ToString
public class CustomError {
    @JsonIgnore
    private HttpStatus status;
    private String message;
    private String code;
    private Map<String, String> details;
    public CustomError(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

}
