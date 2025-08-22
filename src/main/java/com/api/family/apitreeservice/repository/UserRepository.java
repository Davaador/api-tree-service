package com.api.family.apitreeservice.repository;

import com.api.family.apitreeservice.model.postgres.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByEnabled(boolean enabled, Pageable pageable);

}
