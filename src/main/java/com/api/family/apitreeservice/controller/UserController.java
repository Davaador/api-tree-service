package com.api.family.apitreeservice.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.family.apitreeservice.model.dto.Pagination;
import com.api.family.apitreeservice.model.dto.user.AddRoleUserDto;
import com.api.family.apitreeservice.model.postgres.User;
import com.api.family.apitreeservice.service.RoleService;
import com.api.family.apitreeservice.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    @GetMapping("/pending/users")
    public Page<User> pendingUsers(Pagination pagination) {
        return userService.getByPendingStatus(pagination);
    }

    @PutMapping("/users/active/{id}")
    public void updateActiveUser(@PathVariable Long id) {
        userService.putActiveUser(id);
    }

    @DeleteMapping("/users/active/{id}")
    public void deleteActiveUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/add/role/user")
    public AddRoleUserDto addRoleUser(@RequestBody AddRoleUserDto addRoleUserDto) {
        return roleService.addRoleUser(addRoleUserDto);
    }

}
