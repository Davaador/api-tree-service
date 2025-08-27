package com.api.family.apitreeservice.model.dto.customer;

import java.util.Date;
import java.util.List;

import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.Image;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParentDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String gender;
    private Date birthDate;
    private Customer spouse;
    private Customer husband;
    private Customer wife;
    private List<ParentDto> children;
    private Image profileImage;
}
