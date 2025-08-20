package com.api.family.apitreeservice.model.dto.child;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChildDto {
    private Long id;
    private String lastName;
    private String firstName;
    private String register;
    private String surName;
}
