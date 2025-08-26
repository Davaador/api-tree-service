package com.api.family.apitreeservice.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.api.family.apitreeservice.constants.RoleEnumString;
import com.api.family.apitreeservice.exception.CustomException;
import com.api.family.apitreeservice.exception.Errors;
import com.api.family.apitreeservice.model.dto.Pagination;
import com.api.family.apitreeservice.model.dto.user.UserDto;
import com.api.family.apitreeservice.model.postgres.Customer;
import com.api.family.apitreeservice.model.postgres.RoleUsers;
import com.api.family.apitreeservice.model.postgres.User;
import com.api.family.apitreeservice.repository.RoleUsersRepository;
import com.api.family.apitreeservice.repository.UserRepository;
import com.api.family.apitreeservice.spec.UserSpecs;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserSpecs userSpecs;
    private final UtilService utilService;
    private final CustomerService customerService;
    private final RoleUsersRepository roleUsersRepository;

    public UserDto create(@Valid UserDto userDto) {
        this.checkIfDuplicate(userDto.getPhoneNumber(), userDto.getRegister());
        this.matchPasswords(userDto.getPassword(), userDto.getConfirmPassword());
        var role = roleService.getRoleByName(this.checkRoleUser(userDto.getPhoneNumber()).getValue());
        var user = new User(passwordEncoder.encode(userDto.getPassword()), userDto.getPhoneNumber(), false);
        user.addRole(role);
        if (role.getName().equals(RoleEnumString.ROLE_ROOT.getValue())) {
            user.setEnabled(Boolean.TRUE);
            userDto.setIsParent(0);
            userDto.setAge(19);
        } else if (RoleEnumString.ROLE_ADMIN.getValue().equals(role.getName())) {
            user.setEnabled(Boolean.TRUE);
        }
        Customer customer = customerService.create(userDto, user);
        var mappedAdminDto = modelMapper.map(customer.getUser(), UserDto.class);
        mappedAdminDto.setId(customer.getUser().getId());
        return mappedAdminDto;
    }

    public RoleEnumString checkRoleUser(String phoneNumber) {
        List<RoleUsers> roleUsers = roleUsersRepository.findAll();
        Optional<RoleUsers> optionalRole = roleUsers.stream().filter(r -> r.getPhoneNumber().equals(phoneNumber))
                .findFirst();
        if (optionalRole.isPresent()) {
            if (optionalRole.get().getRoleName().equals(RoleEnumString.ROLE_ROOT.getValue())) {
                return RoleEnumString.ROLE_ROOT;
            } else {
                return RoleEnumString.ROLE_ADMIN;
            }
        }
        return RoleEnumString.ROLE_CUSTOMER;
    }

    public void checkIfDuplicate(String phoneNumber, String register) {
        if (StringUtils.isNotEmpty(phoneNumber) && userRepository.findByPhoneNumber(phoneNumber).isPresent())
            throw new CustomException(Errors.DUPLICATED_PHONE_NUMBER);
        if (StringUtils.isNotEmpty(register) && customerService.findByRegister(register))
            throw new CustomException(Errors.DUPLICATED_REGISTER);
    }

    public void matchPasswords(String password, String matchingPassword) {
        if (!matchingPassword.equals(password))
            throw new CustomException(Errors.NOT_MATCH_PASSWORD);
    }

    public void profileUpdate(@Valid UserDto userDto) {
        customerService.updateProfileData(userDto);
    }

    public Page<User> getByPendingStatus(Pagination pagination) {
        Specification<User> spec = Specification.where(userSpecs.containsEnabled(false));
        Pageable pageable = PageRequest.of(pagination.getPage(), pagination.getSize());
        var users = userRepository.findAll(spec, pageable);
        return users.map(u -> modelMapper.map(u, User.class));
    }

    public void putActiveUser(@NotNull Long id) {
        utilService.checkAdmin();
        var updateUser = this.getById(id);
        updateUser.setEnabled(Boolean.TRUE);
        userRepository.save(updateUser);
    }

    public void deleteUser(@NotNull Long id) {
        utilService.checkAdmin();
        this.getById(id);
        userRepository.deleteById(id);
    }

    public User getById(@NotNull Long id) {
        return userRepository.findById(id).orElseThrow(() -> new CustomException(Errors.NOT_PENDING_USERS));
    }

}
