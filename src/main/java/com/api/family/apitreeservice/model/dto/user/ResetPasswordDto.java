package com.api.family.apitreeservice.model.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@JsonInclude(value = JsonInclude.Include.NON_EMPTY, content = JsonInclude.Include.NON_EMPTY)
public class ResetPasswordDto {

    private String email;
    private String otp;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String confirmPassword;
    private String resetToken;

}
