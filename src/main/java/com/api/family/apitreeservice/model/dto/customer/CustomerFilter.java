package com.api.family.apitreeservice.model.dto.customer;

import com.api.family.apitreeservice.model.dto.Pagination;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFilter extends Pagination {
    private String phoneNumber;
    @NotNull
    private int isSortAscending;
}
