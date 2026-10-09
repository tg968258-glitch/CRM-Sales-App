package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.Exception.ResourceNotFoundException;
import com.crm.sales_pipeline.dto.ChangePasswordDto;
import com.crm.sales_pipeline.dto.ProfileUpdateDto;
import com.crm.sales_pipeline.dto.UserRequest;
import com.crm.sales_pipeline.dto.UserResponse;
import com.crm.sales_pipeline.entity.Role;
import com.crm.sales_pipeline.entity.User;
import com.crm.sales_pipeline.repository.RoleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.crm.sales_pipeline.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

public UserService(UserRepository userRepository,RoleRepository roleRepository,
                   PasswordEncoder passwordEncoder)
{
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;

}
public List<UserResponse> getAllUsers(){

    return userRepository.findAll()
            .stream()
            .map(this::mapToResponse)
            .toList();
}

    public UserResponse getUserById(Long uid) {

        User user = userRepository.findById(uid)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
        return mapToResponse(user);
    }
    public UserResponse createUser(UserRequest request) {
    User existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser != null) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsById(request.getUid())) {
            throw new RuntimeException("User ID already exists");
        }
        Role role = roleRepository.findByRoleName(request.getRole())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Role not found"));

        User user = new User();
        user.setUid(request.getUid());
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setRole(role);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return mapToResponse(savedUser);
    }
    public UserResponse getProfile(String email) {
        User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new ResourceNotFoundException("User not found");
            }
            return mapToResponse(user);
        }
    public void changePassword(ChangePasswordDto request) {
    Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
    String email = authentication.getName();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {
            throw new IllegalArgumentException(
                    "Current password is incorrect");}
        if (passwordEncoder.matches(
                request.getNewPassword(),
                user.getPassword())) {
            throw new IllegalArgumentException(
                    "New password cannot be same as current password");}
        user.setPassword(
                passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
    public UserResponse updateProfile(String currentEmail, ProfileUpdateDto request) {
    User user = userRepository.findByEmail(currentEmail);
    if (user == null) {
            throw new ResourceNotFoundException("User not found");}
    if (request.getName() != null) {
            user.setName(request.getName());}
        User updatedUser = userRepository.save(user);
    return mapToResponse(updatedUser);}

    private UserResponse mapToResponse (User user){
    return new UserResponse(
            user.getUid(),
            user.getName(),
            user.getEmail(),
            user.getRole().getRoleName().name(),
            user.isActive()

    );
}
}
