package com.api.family.apitreeservice.repository;

import com.api.family.apitreeservice.model.postgres.Biography;
import com.api.family.apitreeservice.model.postgres.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BiographyRepository extends JpaRepository<Biography, Long> {

    Optional<Biography> findByCustomer(Customer customer);
}
