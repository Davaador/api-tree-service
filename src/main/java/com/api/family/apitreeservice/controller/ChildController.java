package com.api.family.apitreeservice.controller;

import com.api.family.apitreeservice.model.dto.child.ChildDto;
import com.api.family.apitreeservice.model.dto.customer.ParentDto;
import com.api.family.apitreeservice.model.dto.user.UserDto;
import com.api.family.apitreeservice.service.ChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/child")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addChild(@RequestBody ChildDto childDto) {
        return childService.createChild(childDto);
    }

    @GetMapping("/list")
    public List<ParentDto> getChildInfos() {
        return childService.getChildInfos();
    }

}
