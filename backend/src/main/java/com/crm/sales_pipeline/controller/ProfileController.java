package com.crm.sales_pipeline.controller;

import com.crm.sales_pipeline.dto.ChangePasswordDto;
import com.crm.sales_pipeline.dto.ProfileUpdateDto;
import com.crm.sales_pipeline.dto.UserResponse;
import com.crm.sales_pipeline.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@Tag(name = "Profile")
public class ProfileController {
    private final UserService userService;
    public ProfileController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        UserResponse response = userService.getProfile(authentication.getName());
        return ResponseEntity.ok(response);
    }
    @PutMapping
    public ResponseEntity<UserResponse> updateProfile(
            @RequestBody ProfileUpdateDto request,
            Authentication authentication) {
        UserResponse response = userService.updateProfile(
                authentication.getName(),
                request);
        return ResponseEntity.ok(response);}
    @PutMapping("/change-password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody ChangePasswordDto request) {
        userService.changePassword(request);
        return ResponseEntity.ok("Password changed successfully");
    }
}
