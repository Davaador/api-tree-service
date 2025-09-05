package com.api.family.apitreeservice.controller;

import com.api.family.apitreeservice.model.dto.biography.BiographyDto;
import com.api.family.apitreeservice.model.dto.biography.BiographyHistoryDto;
import com.api.family.apitreeservice.service.BiographyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/history")
    public List<BiographyHistoryDto> getBiographyHistory() {
        return biographyService.getBiographyHistory();
    }

    @PostMapping("/restore/{historyId}")
    public ResponseEntity<BiographyDto> restoreBiographyVersion(@PathVariable Long historyId) {
        try {
            BiographyDto restoredBiography = biographyService.restoreBiographyVersion(historyId);
            return ResponseEntity.ok(restoredBiography);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
