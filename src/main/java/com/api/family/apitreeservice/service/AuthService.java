package com.api.family.apitreeservice.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.api.family.apitreeservice.model.dto.auth.Credentials;
import com.api.family.apitreeservice.model.dto.customer.CoupleDto;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.User;
import com.api.family.apitreeservice.model.response.Token;
import com.api.family.apitreeservice.repository.UserRepository;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtility;
    private final ModelMapper modelMapper;
    private final UtilService utilService;
    private final UserRepository userRepository;

    // Metrics
    private final Counter authenticationSuccessCounter;
    private final Counter authenticationFailureCounter;

    @Timed("auth.service.authenticate")
    public Token authenticate(@Valid Credentials credentials) {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(credentials.getPhoneNumber(),
                            credentials.getPassword()));
            User user = (User) auth.getPrincipal();
            var jwtToken = jwtUtility.generateToken(user);
            var refreshToken = jwtUtility.generateRefreshToken(user);
            authenticationSuccessCounter.increment();
            return new Token(jwtToken, refreshToken);
        } catch (Exception e) {
            authenticationFailureCounter.increment();
            throw e;
        }
    }

    public Token refreshToken(String refreshToken) {
        if (!jwtUtility.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        String username = jwtUtility.extractUsername(refreshToken);
        User user = userRepository.findByPhoneNumber(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        var newJwtToken = jwtUtility.generateToken(user);
        var newRefreshToken = jwtUtility.generateRefreshToken(user);
        return new Token(newJwtToken, newRefreshToken);
    }

    public CoupleDto getUserDetails() {
        Customer customer = utilService.findByCustomer();
        return modelMapper.map(customer, CoupleDto.class);
    }
}
