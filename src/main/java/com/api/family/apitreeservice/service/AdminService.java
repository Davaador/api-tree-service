package com.api.family.apitreeservice.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.api.family.apitreeservice.constants.RoleEnumString;
import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.dto.admin.AdminCreateDto;
import com.api.family.apitreeservice.model.postgres.Customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class AdminService {

    private final UtilService utilService;

    public void createCustomer(@Valid AdminCreateDto adminCreateDto) {
        log.info("Creating admin customer");
        Customer customer = utilService.findByCustomer();
        if (!customer.getUser().getRoles().get(0).getName().equals(RoleEnumString.ROLE_ADMIN.getValue()))
            throw new CustomException(Errors.NOT_ADMIN);
        // utilService.checkIfDuplicateRegister(null);
        if (adminCreateDto.getAge() < 18) {
            log.info("child role");
        } else {
            log.info("customer role");
        }

    }

}
