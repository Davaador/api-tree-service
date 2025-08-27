package com.api.family.apitreeservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.api.family.apitreeservice.model.dto.child.ChildDto;
import com.api.family.apitreeservice.model.dto.user.UserDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addChild(@RequestBody ChildDto childDto) {
        return null;
    }

}
