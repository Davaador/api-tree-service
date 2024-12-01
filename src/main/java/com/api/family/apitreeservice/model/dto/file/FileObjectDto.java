package com.api.family.apitreeservice.model.dto.file;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileObjectDto {
    @NotNull
    private Long id;
    private String url;
    private String name;
    private String extension;
    private BigInteger size;
}
