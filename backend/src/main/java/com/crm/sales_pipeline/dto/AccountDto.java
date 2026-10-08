package com.crm.sales_pipeline.dto;

import com.crm.sales_pipeline.enums.Industry;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccountDto {

    private Integer accId;
    @NotNull private Long accOwnerId;

    @NotBlank(message = "Account name is required")
    private String accountName;

    private Industry industry;
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    @Pattern(
            regexp = "^[0-9+()\\- ]*$",
            message = "Phone number is invalid"
    )
    private String phoneNumber;
    private String website;
}