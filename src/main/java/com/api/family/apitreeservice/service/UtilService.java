package com.api.family.apitreeservice.service;

import com.api.family.apitreeservice.constants.RoleEnumString;
import com.api.family.apitreeservice.exception.CustomError;
import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.User;
import com.api.family.apitreeservice.repository.CustomerRepository;
import com.api.family.apitreeservice.validator.JwtTokenGenerate;
import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UtilService {
    private final JwtTokenGenerate jwtTokenGenerate;
    private final CustomerRepository customerRepository;

    public void checkAdmin() {
        var tokenUser = jwtTokenGenerate.getUser();
        tokenUser.getRoles().stream().filter(x -> !x.getName().equals(RoleEnumString.ROLE_CUSTOMER.getValue()))
                .findFirst().orElseThrow(() -> new CustomException(Errors.NOT_ADMIN));
    }

    public Customer findByCustomer() {
        User user = jwtTokenGenerate.getUser();
        Optional<Customer> optionalCustomer = customerRepository.findByUser(user);
        if (optionalCustomer.isPresent())
            return optionalCustomer.get();
        throw new CustomException(Errors.NOT_PENDING_USERS);
    }

    public Customer checkIfCustomerInEmail(String email, Boolean isCheck, CustomError errors) {
        if (StringUtils.isNotEmpty(email)) {
            Optional<Customer> opCustomer = customerRepository.findByEmail(email);
            if (isCheck.equals(opCustomer.isEmpty()))
                throw new CustomException(Objects.nonNull(errors) ? errors : Errors.NOT_EMAIL);
            return opCustomer.isEmpty() ? null : opCustomer.get();
        }
        return null;
    }
}
