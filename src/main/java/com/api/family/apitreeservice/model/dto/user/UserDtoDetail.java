package com.api.family.apitreeservice.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoDetail {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String register;
    private String surName;
    private boolean editCustomer;
    private LocalDateTime modifiedDate;
    private Date birthDate;
    private UserDto userDto;
}
