package com.crm.sales_pipeline.dto;

import com.crm.sales_pipeline.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoleResponseDto {
    private Long roleId;
    private UserRole roleName;
    private String description;
}
