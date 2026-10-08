package com.crm.sales_pipeline.controller;

import com.crm.sales_pipeline.dto.UserRequest;
import com.crm.sales_pipeline.dto.UserResponse;
import com.crm.sales_pipeline.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }
    @GetMapping("/{uid}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long uid) {
        UserResponse user = userService.getUserById(uid);
        return ResponseEntity.ok(user);
    }
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @RequestBody UserRequest request) {
        UserResponse user = userService.createUser(request);
        return ResponseEntity.ok(user);
    }
}