package com.api.family.apitreeservice.controller;

import com.api.family.apitreeservice.model.dto.auth.Credentials;
import com.api.family.apitreeservice.model.dto.customer.CoupleDto;
import com.api.family.apitreeservice.model.dto.user.ResetPasswordDto;
import com.api.family.apitreeservice.model.dto.user.UserDto;
import com.api.family.apitreeservice.model.response.Token;
import com.api.family.apitreeservice.service.AuthService;
import com.api.family.apitreeservice.service.ResetPasswordService;
import com.api.family.apitreeservice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final ResetPasswordService resetPasswordService;

    @PostMapping("/token")
    public Token token(@RequestBody Credentials credentials, HttpServletResponse response) {

        var token = authService.authenticate(credentials);
        var cookie = ResponseCookie.from("accessToken", token.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/").maxAge(1800).build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return token;

    }

    @PostMapping("/refresh")
    public Token refreshToken(@RequestBody Map<String, String> request, HttpServletResponse response) {
        String refreshToken = request.get("refreshToken");
        var token = authService.refreshToken(refreshToken);

        var cookie = ResponseCookie.from("accessToken", token.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/").maxAge(1800).build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return token;
    }

    @PostMapping("/user/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @GetMapping("/introspect")
    public CoupleDto introspect() {
        return authService.getUserDetails();
    }

    @PostMapping("/user/profile/update")
    @ResponseStatus(HttpStatus.OK)
    public void profileUpdate(@RequestBody UserDto userDto) {
        userService.profileUpdate(userDto);
    }

    @PostMapping("/sent/otp")
    public void sendOtp(@RequestBody ResetPasswordDto resetPasswordDto) {
        resetPasswordService.sendToOtp(resetPasswordDto);
    }

    @PostMapping("/check/otp")
    public String checkOtp(@RequestBody ResetPasswordDto resetPasswordDto) {
        return resetPasswordService.checkCustomerOtp(resetPasswordDto);
    }

    @PostMapping("/reset/password")
    public void resetPassword(@RequestBody ResetPasswordDto resetPasswordDto) {
        resetPasswordService.resetPassword(resetPasswordDto);
    }

}
