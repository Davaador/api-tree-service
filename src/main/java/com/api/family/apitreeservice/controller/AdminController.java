package com.api.family.apitreeservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.api.family.apitreeservice.model.dto.admin.AdminCreateDto;
import com.api.family.apitreeservice.model.dto.biography.BiographyDto;
import com.api.family.apitreeservice.model.dto.biography.BiographyHistoryDto;
import com.api.family.apitreeservice.model.dto.customer.CustomerSummaryDto;
import com.api.family.apitreeservice.service.AdminService;

import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AdminCreateDto createCustomer(@RequestBody AdminCreateDto adminCreateDto) {
        return adminService.createCustomer(adminCreateDto);
    }

    @GetMapping("/customers")
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerSummaryDto> listRegisteredCustomersByCurrentAdmin() {
        return adminService.listRegisteredCustomersByCurrentAdmin();
    }

    @GetMapping("/customers/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CustomerSummaryDto getCustomerById(@PathVariable Integer id) {
        return adminService.getCustomerById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AdminCreateDto updateCustomer(@PathVariable Integer id, @RequestBody AdminCreateDto adminCreateDto) {
        return adminService.updateCustomer(id, adminCreateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Integer id) {
        adminService.deleteCustomer(id);
    }

    @GetMapping("/customers/{id}/available-spouses")
    @ResponseStatus(HttpStatus.OK)
    public List<CustomerSummaryDto> getAvailableSpousesForCustomer(@PathVariable Integer id) {
        return adminService.getAvailableSpousesForCustomer(id);
    }

    @PostMapping("/customers/{id}/biography")
    @ResponseStatus(HttpStatus.OK)
    public BiographyDto addBiographyForCustomer(@PathVariable Integer id, @RequestBody BiographyDto biographyDto) {
        return adminService.addBiographyForCustomer(id, biographyDto);
    }

    @GetMapping("/customers/{id}/biography")
    @ResponseStatus(HttpStatus.OK)
    public BiographyDto getBiographyForCustomer(@PathVariable Integer id) {
        return adminService.getBiographyForCustomer(id);
    }

    @GetMapping("/customers/{id}/biography/history")
    @ResponseStatus(HttpStatus.OK)
    public List<BiographyHistoryDto> getBiographyHistoryForCustomer(@PathVariable Integer id) {
        return adminService.getBiographyHistoryForCustomer(id);
    }

    @PostMapping("/customers/{id}/biography/restore/{historyId}")
    @ResponseStatus(HttpStatus.OK)
    public BiographyDto restoreBiographyVersionForCustomer(@PathVariable Integer id, @PathVariable Long historyId) {
        return adminService.restoreBiographyVersionForCustomer(id, historyId);
    }

}
