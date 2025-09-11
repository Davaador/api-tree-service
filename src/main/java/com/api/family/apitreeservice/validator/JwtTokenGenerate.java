package com.api.family.apitreeservice.validator;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.api.family.apitreeservice.model.postgres.User;

@Component
public class JwtTokenGenerate {

    public User getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
