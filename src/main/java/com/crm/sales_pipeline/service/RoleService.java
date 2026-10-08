package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.dto.RoleResponseDto;
import com.crm.sales_pipeline.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    public List<RoleResponseDto> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(role -> new RoleResponseDto(
                        role.getRoleId(),
                        role.getRoleName(),
                        role.getDescription()))
                .toList();
    }
}
