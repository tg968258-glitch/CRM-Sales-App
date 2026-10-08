package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.Exception.ResourceNotFoundException;
import com.crm.sales_pipeline.entity.User;
import com.crm.sales_pipeline.enums.UserRole;
import com.crm.sales_pipeline.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class RecordAccessService {
    private final UserRepository userRepository;

    public RecordAccessService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return user;
    }

    public boolean isSalesExecutive() {
        return currentUser().getRole().getRoleName() == UserRole.SALES_EXECUTIVE;
    }

    public String currentEmail() {
        return currentUser().getEmail();
    }

    public User resolveOwner(Long requestedOwnerId) {
        User current = currentUser();
        if (current.getRole().getRoleName() == UserRole.SALES_EXECUTIVE) {
            return current;
        }
        return userRepository.findById(requestedOwnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
    }

    public void requireAccess(User owner, String resourceName) {
        if (isSalesExecutive() && !owner.getUid().equals(currentUser().getUid())) {
            throw new ResourceNotFoundException(resourceName + " not found");
        }
    }
}
