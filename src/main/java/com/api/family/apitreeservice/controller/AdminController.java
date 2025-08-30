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

}
