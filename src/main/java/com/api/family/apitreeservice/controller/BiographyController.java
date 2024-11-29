package com.api.family.apitreeservice.controller;

import com.api.family.apitreeservice.model.dto.biography.BiographyDto;
import com.api.family.apitreeservice.service.BiographyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/biography")
@RequiredArgsConstructor
public class BiographyController {
    private final BiographyService biographyService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public BiographyDto addBiography(@RequestBody BiographyDto biographyDto) {
        return biographyService.addBiography(biographyDto);
    }
    @GetMapping
    public BiographyDto getBiography() {
        return biographyService.getBiography();
    }

}
