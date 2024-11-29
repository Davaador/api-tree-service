package com.api.family.apitreeservice.model.dto.user;

import lombok.Data;

import java.util.Date;

@Data
public class AddParentDto {
    private Integer parentId;
    private Integer isParent;
    private String surName;
    private String email;
    private Integer age;
    private Date birthDate;
}
