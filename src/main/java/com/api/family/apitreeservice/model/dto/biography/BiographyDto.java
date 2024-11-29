package com.api.family.apitreeservice.model.dto.biography;

import com.api.family.apitreeservice.model.postgres.Customer;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BiographyDto {
    private Long id;
    private String detailBiography;
    private Customer customer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
