package com.api.family.apitreeservice.model.dto.auth;

import com.api.family.apitreeservice.annotation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {
    @NotBlank
    private String phoneNumber;
    @NotBlank
    @ValidPassword
    private String password;
}
