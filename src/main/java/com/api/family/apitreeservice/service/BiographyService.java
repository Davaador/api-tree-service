package com.api.family.apitreeservice.service;

import com.api.family.apitreeservice.model.dto.biography.BiographyDto;
import com.api.family.apitreeservice.model.dto.biography.BiographyHistoryDto;
import com.api.family.apitreeservice.model.postgres.Biography;
import com.api.family.apitreeservice.model.postgres.BiographyHistory;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.repository.BiographyHistoryRepository;
import com.api.family.apitreeservice.repository.BiographyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class BiographyService {

    private final BiographyRepository biographyRepository;
    private final BiographyHistoryRepository biographyHistoryRepository;
    private final ModelMapper modelMapper;
    private final UtilService utilService;

    @Transactional
    public BiographyDto addBiography(BiographyDto biographyDto) {
        log.info("Biography added/updated");
        Customer customer = utilService.findByCustomer();
        Biography biography;
        Optional<Biography> optionalBiography = biographyRepository.findByCustomer(customer);

        if (optionalBiography.isEmpty()) {
            // Create new biography
            biography = new Biography(biographyDto);
            biography.setCustomer(customer);
            log.info("Creating new biography for customer: {}", customer.getId());
        } else {
            // Update existing biography
            biography = optionalBiography.get();
            String oldContent = biography.getDetailBiography();
            String newContent = biographyDto.getDetailBiography();

            // Save history if content changed
            if (!Objects.equals(oldContent, newContent)) {
                saveBiographyHistory(biography, "Content updated");
                log.info("Biography content updated for customer: {}", customer.getId());
            }

            biography.setDetailBiography(newContent);
            biography.setUpdatedAt(LocalDateTime.now());
        }

        var savedBiography = biographyRepository.save(biography);

        // Save history for new biography after it's saved (has ID)
        if (optionalBiography.isEmpty()) {
            saveBiographyHistory(savedBiography, "Initial biography created");
            log.info("Initial biography history saved for customer: {}", customer.getId());
        }

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

    public List<BiographyHistoryDto> getBiographyHistory() {
        log.info("Biography history retrieved");
        Customer customer = utilService.findByCustomer();
        Biography biography = biographyRepository.findByCustomer(customer).orElse(null);

        if (Objects.isNull(biography)) {
            log.warn("No biography found for customer: {}", customer.getId());
            return List.of();
        }

        log.info("Found biography with ID: {} for customer: {}", biography.getId(), customer.getId());
        List<BiographyHistory> history = biographyHistoryRepository.findByBiographyOrderByCreatedAtDesc(biography);
        log.info("Found {} history entries for biography ID: {}", history.size(), biography.getId());

        return history.stream()
                .map(this::mapToHistoryDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public BiographyDto restoreBiographyVersion(Long historyId) {
        log.info("Restoring biography from history: {}", historyId);
        Customer customer = utilService.findByCustomer();
        Biography biography = biographyRepository.findByCustomer(customer).orElse(null);

        if (Objects.isNull(biography)) {
            throw new RuntimeException("Biography not found");
        }

        BiographyHistory historyEntry = biographyHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("History entry not found"));

        // Save current content as history before restoring
        saveBiographyHistory(biography, "Restored from version " + historyEntry.getCreatedAt());

        // Restore content
        biography.setDetailBiography(historyEntry.getBiographyContent());
        biography.setUpdatedAt(LocalDateTime.now());

        var savedBiography = biographyRepository.save(biography);
        return modelMapper.map(savedBiography, BiographyDto.class);
    }

    private void saveBiographyHistory(Biography biography, String changeDescription) {
        if (biography == null || biography.getId() == null) {
            log.warn("Cannot save biography history: biography or biography ID is null");
            return;
        }

        try {
            Long versionCount = biographyHistoryRepository.countByBiography(biography);
            BiographyHistory history = new BiographyHistory(biography, changeDescription);
            history.setVersionNumber(versionCount.intValue() + 1);
            biographyHistoryRepository.save(history);
            log.info("Biography history saved successfully for biography ID: {}, version: {}",
                    biography.getId(), versionCount.intValue() + 1);
        } catch (Exception e) {
            log.error("Error saving biography history for biography ID: {}", biography.getId(), e);
        }
    }

    private BiographyHistoryDto mapToHistoryDto(BiographyHistory history) {
        BiographyHistoryDto dto = modelMapper.map(history, BiographyHistoryDto.class);
        dto.setBiographyId(history.getBiography().getId());
        return dto;
    }

}
