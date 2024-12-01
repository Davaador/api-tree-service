package com.api.family.apitreeservice.model.dto.customer;

import com.api.family.apitreeservice.model.dto.file.FileObjectDto;
import com.api.family.apitreeservice.model.postgres.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParentDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String gender;
    private Customer spouse;
    private Customer husband;
    private Customer wife;
    private List<ParentDto> children;
    private FileObjectDto profilePicture;
}
