package com.api.family.apitreeservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    private String token;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;

    public Token(String token) {
        this.token = token;
        this.tokenType = "Bearer";
    }

    public Token(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.tokenType = "Bearer";
    }
}
