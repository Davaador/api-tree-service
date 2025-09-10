package com.api.family.apitreeservice.model.dto.biography;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BiographyDto {
    private Long id;
    private String detailBiography;
    private Long customerId;
    private String customerFirstName;
    private String customerLastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
