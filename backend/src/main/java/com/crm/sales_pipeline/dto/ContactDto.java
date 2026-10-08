package com.crm.sales_pipeline.dto;

import com.crm.sales_pipeline.enums.LifecycleStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ContactDto {
    private Integer contactId;
    @NotNull private Integer accountId;
    @NotNull private Long contactOwnerId;
    @NotBlank(message = "Contact name is required")
    private String name;

    @Email(message = "Enter a valid email")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    private String jobTitle;
    @Pattern(
            regexp = "^[0-9+()\\- ]*$",
            message = "Phone number is invalid")
    private String phoneNumber;
    private LifecycleStatus lifecycleStatus;
}
