package com.api.family.apitreeservice.model.dto.child;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BirthOrderDto {
    private Integer birthOrder; // Which child they are (1st, 2nd, 3rd, etc.)
    private Integer totalChildren; // Total number of children in the family
}
