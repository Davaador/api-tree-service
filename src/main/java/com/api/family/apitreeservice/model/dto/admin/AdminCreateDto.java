package com.api.family.apitreeservice.model.dto.admin;

import java.util.Date;

import com.api.family.apitreeservice.model.dto.user.UserDto;
import com.api.family.apitreeservice.model.postgres.Customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminCreateDto {
    private Integer id;
    private String lastName;
    private String firstName;
    private String register;
    private String surName;
    private Date birthDate;
    private Integer age;
    private String gender;
    private String phoneNumber;
    private String email;
    private UserDto user;
    private Customer spouse;
    private Customer husband;
    private Customer wife;

}
