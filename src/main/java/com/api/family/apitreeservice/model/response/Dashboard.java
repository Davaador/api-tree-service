package com.api.family.apitreeservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dashboard {
    private Integer total = 0;
    private Integer pendingCount = 0;
    private Integer activeCount = 0;
    private Integer birthOrder = 0; // Which child they are (1st, 2nd, 3rd, etc.)
}
