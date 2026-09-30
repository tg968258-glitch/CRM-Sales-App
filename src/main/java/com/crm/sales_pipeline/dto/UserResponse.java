package com.crm.sales_pipeline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long uid;
    private String email;
    private String password;
    private boolean isActive;

}
