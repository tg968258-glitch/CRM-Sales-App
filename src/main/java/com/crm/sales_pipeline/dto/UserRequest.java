package com.crm.sales_pipeline.dto;

import com.crm.sales_pipeline.enums.UserRole;
import lombok.Data;

@Data
public class UserRequest {
    private Long uid;
    private String name;
    private String email;
    private String password;
    private UserRole role;
}
