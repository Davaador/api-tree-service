package com.api.family.apitreeservice.controller;

import com.api.family.apitreeservice.model.dto.customer.CoupleDto;
import com.api.family.apitreeservice.model.dto.customer.CustomerCoupleDto;
import com.api.family.apitreeservice.model.dto.customer.CustomerDto;
import com.api.family.apitreeservice.model.response.Dashboard;
import com.api.family.apitreeservice.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/couples")
    public List<CustomerDto> getCouples() {
        return customerService.getCoupleAll();
    }

    @GetMapping("/couple")
    public CoupleDto findByCouple() {
        return customerService.findByCouple();
    }

    @PostMapping("/add/couple")
    @ResponseStatus(HttpStatus.CREATED)
    public CoupleDto createCouple(@RequestBody CustomerCoupleDto customerParentDto) {
        return customerService.saveCouple(customerParentDto);
    }

    @PostMapping("/dashboard")
    public Dashboard dashboard() {
        return customerService.dashboard();
    }

    @PostMapping("/update/info")
    @ResponseStatus(HttpStatus.OK)
    public CoupleDto updateInfo(@RequestBody CustomerDto customerDto) {
        return customerService.updateInfo(customerDto);
    }


}
