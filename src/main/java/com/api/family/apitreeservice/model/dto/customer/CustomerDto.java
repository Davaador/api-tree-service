package com.api.family.apitreeservice.model.dto.customer;

import java.time.LocalDateTime;
import java.util.Date;

import com.api.family.apitreeservice.model.dto.user.UserDto;
import com.api.family.apitreeservice.model.postgres.Image;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CustomerDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String register;
    private String surName;
    private Date birthDate;
    private Integer age;
    private String gender;
    private String phoneNumber;
    private String email;
    private boolean editCustomer = false;
    private LocalDateTime modifiedDate = LocalDateTime.now();
    private UserDto user;
    private Image profileImage;

}
