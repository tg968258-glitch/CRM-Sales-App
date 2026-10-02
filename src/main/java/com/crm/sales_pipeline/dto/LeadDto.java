package com.crm.sales_pipeline.dto;

import com.crm.sales_pipeline.enums.LeadRating;
import com.crm.sales_pipeline.enums.LeadSource;
import com.crm.sales_pipeline.enums.LeadStatus;
import com.crm.sales_pipeline.enums.Salutation;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LeadDto {

    private Integer leadId;

    @NotNull(message = "Owner is required")
    private Long ownerId;

    private Salutation salutation;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Size(max = 255, message = "Phone number cannot exceed 255 characters")
    private String phoneNumber;

    @Size(max = 255, message = "Company name cannot exceed 255 characters")
    private String companyName;

    private LeadSource source;

    @NotNull(message = "Status is required")
    private LeadStatus status;

    private LeadRating leadRating;

    @PositiveOrZero(message = "Expected value cannot be negative")
    private BigDecimal expectedValue;

    @Size(max = 255, message = "Notes cannot exceed 255 characters")
    private String notes;
}