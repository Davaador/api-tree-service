package com.api.family.apitreeservice.validator;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.api.family.apitreeservice.annotation.ValidPassword;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private Pattern pattern;

    @Value("${tinker.validation.password:22}")
    private String passwordPattern;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        pattern = Pattern.compile(passwordPattern);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return pattern.matcher(value).matches();
    }
}
