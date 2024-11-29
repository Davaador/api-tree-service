package com.api.family.apitreeservice.model.dto.user;

import com.api.family.apitreeservice.model.postgres.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@RequiredArgsConstructor
public class UserDto {
    private Long id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    private String phoneNumber;
    private String confirmPassword;
    private String email;
    private String surName;
    private String register;
    private Date birthDate;
    private Integer age;
    private List<Role> roles;
}
