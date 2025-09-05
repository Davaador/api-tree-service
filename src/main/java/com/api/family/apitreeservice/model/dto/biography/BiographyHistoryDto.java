package com.api.family.apitreeservice.model.dto.biography;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BiographyHistoryDto {
    private Long id;
    private String biographyContent;
    private Integer versionNumber;
    private String changeDescription;
    private LocalDateTime createdAt;
    private Long biographyId;
}
