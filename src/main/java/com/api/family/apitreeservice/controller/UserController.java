package com.api.family.apitreeservice.controller;

import com.api.family.apitreeservice.model.dto.Pagination;
import com.api.family.apitreeservice.model.postgres.User;
import com.api.family.apitreeservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/pending/users")
    public Page<User> pendingUsers(Pagination pagination){
        return userService.getByPendingStatus(pagination);
    }

    @PutMapping("/users/active/{id}")
    public void updateActiveUser(@PathVariable Long id){
        userService.putActiveUser(id);
    }

    @DeleteMapping("/users/active/{id}")
    public void deleteActiveUser(@PathVariable Long id){
        userService.deleteUser(id);
    }
}
