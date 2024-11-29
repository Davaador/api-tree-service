package com.api.family.apitreeservice.validator;

import com.api.family.apitreeservice.annotation.ValidPassword;
import io.jsonwebtoken.security.Password;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

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
