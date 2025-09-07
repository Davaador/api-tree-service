package com.api.family.apitreeservice.model.dto.customer;

import com.api.family.apitreeservice.model.dto.Pagination;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFilter extends Pagination {
    private String phoneNumber;
    private String lastName;
    private String firstName;
    private String email;
    private String register;
    private String gender;
    private Integer minAge;
    private Integer maxAge;
    private Date birthDateFrom;
    private Date birthDateTo;
    private Boolean isDeceased;
    private Integer isParent;
    @NotNull
    private int isSortAscending;
    private String sortField = "birthDate"; // Default sort field
}
