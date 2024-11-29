package com.api.family.apitreeservice.service;

import com.api.family.apitreeservice.model.dto.auth.Credentials;
import com.api.family.apitreeservice.model.dto.customer.CoupleDto;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.User;
import com.api.family.apitreeservice.model.response.Token;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtility;
    private final ModelMapper modelMapper;
    private final UtilService utilService;

    public Token authenticate(@Valid Credentials credentials) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(credentials.getPhoneNumber(),
                        credentials.getPassword()));
        var jwtToken = jwtUtility.generateToken((User) auth.getPrincipal());
        return new Token(jwtToken);
    }
    public CoupleDto getUserDetails() {
        Customer customer = utilService.findByCustomer();
        return modelMapper.map(customer, CoupleDto.class);
    }
}
