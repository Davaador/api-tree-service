package com.api.family.apitreeservice.model.dto.customer;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CustomerSummaryDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String surName;
    private String email;
    private Integer age;
    private String register;
    private java.util.Date birthDate;
    private Boolean isDeceased;
    private java.util.Date deceasedDate;
}
