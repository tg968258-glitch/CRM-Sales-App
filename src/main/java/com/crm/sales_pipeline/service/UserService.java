package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.dto.UserResponse;
import com.crm.sales_pipeline.entity.User;
import com.crm.sales_pipeline.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

public UserService(UserRepository userRepository)
{
    this.userRepository = userRepository;
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
                        new RuntimeException("User not found"));

        return mapToResponse(user);
    }

private UserResponse mapToResponse (User user){
    return new UserResponse(
            user.getUid(),
            user.getName(),
            user.getEmail(),
            user.isActive()
    );
}
}
