package com.api.family.apitreeservice.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.api.family.apitreeservice.model.postgres.Image;
import com.api.family.apitreeservice.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/file")
public class FileObjectController {

    private final CustomerService customerService;

    @PostMapping
    public Image uploadFile(@RequestParam MultipartFile file) throws IOException {
        return customerService.updateImage(file);
    }

}
