package com.api.family.apitreeservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.CustomerOtp;

public interface CustomerOtpRepository extends JpaRepository<CustomerOtp, Long>, JpaSpecificationExecutor<CustomerOtp> {

    List<CustomerOtp> findByCustomerAndStatus(Customer customer, String status);

    Optional<CustomerOtp> findByEmailAndStatus(String email, String status);
}
