package com.api.family.apitreeservice.service;

import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.dto.user.AddRoleUserDto;
import com.api.family.apitreeservice.model.postgres.Role;
import com.api.family.apitreeservice.model.postgres.RoleUsers;
import com.api.family.apitreeservice.repository.RoleRepository;
import com.api.family.apitreeservice.repository.RoleUsersRepository;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleUsersRepository roleUsersRepository;
    private final ModelMapper modelMapper;

    public Role getRoleByName(@NotBlank String name) {
        return roleRepository.findByName(name).orElseThrow(() -> new CustomException(Errors.ROLE_NOT_FOUND));
    }

    public AddRoleUserDto addRoleUser(@Valid AddRoleUserDto addRoleUserDto) {
        RoleUsers roleUsers = new RoleUsers();
        List<RoleUsers> roleUsers2 = roleUsersRepository.findByPhoneNumber(addRoleUserDto.getPhoneNumber());
        if (CollectionUtils.isEmpty(roleUsers2)) {
            roleUsers.setPhoneNumber(addRoleUserDto.getPhoneNumber());
            roleUsers.setRoleName(addRoleUserDto.getRoleName());
            var roleUser = roleUsersRepository.save(roleUsers);
            return modelMapper.map(roleUser, AddRoleUserDto.class);
        }

        throw new CustomException(Errors.ADMIN_DUPLICATE);
    }
}
