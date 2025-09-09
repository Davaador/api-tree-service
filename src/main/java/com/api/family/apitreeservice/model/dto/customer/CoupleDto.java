package com.api.family.apitreeservice.model.dto.customer;

import java.time.LocalDateTime;
import java.util.Date;

import com.api.family.apitreeservice.model.dto.user.UserDto;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.Image;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CoupleDto {
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
    private Customer wife;
    private Customer husband;
    private Customer parent;
    private Image profileImage;
    private Boolean isDeceased = false;
    private Date deceasedDate;

}
