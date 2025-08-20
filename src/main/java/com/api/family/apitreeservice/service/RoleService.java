package com.api.family.apitreeservice.service;

import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.postgres.Role;
import com.api.family.apitreeservice.repository.RoleRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Role getRoleByName(@NotBlank String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new CustomException(Errors.ROLE_NOT_FOUND));
    }
}
