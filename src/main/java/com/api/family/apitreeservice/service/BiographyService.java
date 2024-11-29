package com.api.family.apitreeservice.service;

import com.api.family.apitreeservice.model.dto.biography.BiographyDto;
import com.api.family.apitreeservice.model.postgres.Biography;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.repository.BiographyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class BiographyService {

    private final BiographyRepository biographyRepository;
    private final ModelMapper modelMapper;
    private final UtilService utilService;

    public BiographyDto addBiography(BiographyDto biographyDto) {
        log.info("Biography added");
        Customer customer = utilService.findByCustomer();
        Biography biography;
        Optional<Biography> optionalBiography = biographyRepository.findByCustomer(customer);
        if (optionalBiography.isEmpty()) {
            biography = new Biography(biographyDto);
            biography.setCustomer(customer);
        } else {
            biography = optionalBiography.get();
            biography.setDetailBiography(biographyDto.getDetailBiography());
            biography.setUpdatedAt(LocalDateTime.now());
        }
        var savedBiography = biographyRepository.save(biography);
        return modelMapper.map(savedBiography, BiographyDto.class);
    }

    public BiographyDto getBiography() {
        log.info("Biography retrieved");
        Customer customer = utilService.findByCustomer();
        Biography biography = biographyRepository.findByCustomer(customer).orElse(null);
        if (Objects.isNull(biography))
            return null;
        return modelMapper.map(biography, BiographyDto.class);
    }
}
