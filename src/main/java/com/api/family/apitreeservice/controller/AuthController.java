package com.api.family.apitreeservice.controller;

import com.api.family.apitreeservice.model.dto.auth.Credentials;
import com.api.family.apitreeservice.model.dto.customer.CoupleDto;
import com.api.family.apitreeservice.model.dto.customer.CustomerDto;
import com.api.family.apitreeservice.model.dto.user.UserDto;
import com.api.family.apitreeservice.model.dto.user.UserDtoDetail;
import com.api.family.apitreeservice.model.response.Token;
import com.api.family.apitreeservice.service.AuthService;
import com.api.family.apitreeservice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/token")
    public Token token(@RequestBody Credentials credentials, HttpServletResponse response){

        var token = authService.authenticate(credentials);
        var cookie = ResponseCookie.from("accessToken", token.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/").maxAge(1800).build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return token;

    }

    @PostMapping("/user/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@RequestBody UserDto userDto){
        return userService.create(userDto);
    }

    @GetMapping("/introspect")
    public CoupleDto introspect(){
        return authService.getUserDetails();
    }

    @PostMapping("/user/profile/update")
    @ResponseStatus(HttpStatus.OK)
    public void profileUpdate(@RequestBody UserDto userDto){
        userService.profileUpdate(userDto);
    }

    @PostMapping("/user/profile/update/name")
    @ResponseStatus(HttpStatus.OK)
    public void profileUpdateName(@RequestBody UserDto userDto){
        userService.nameUpdate(userDto);
    }
}
