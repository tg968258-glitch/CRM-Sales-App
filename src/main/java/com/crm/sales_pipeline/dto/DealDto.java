package com.crm.sales_pipeline.dto;

import com.crm.sales_pipeline.enums.DealStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DealDto {

    private Integer dealId;

    @NotBlank(message = "Deal title is required")
    private String title;
    private Integer accountId;
    private Integer contactId;
    private Integer dealStageId;
    private Long dealOwnerId;

    @PositiveOrZero(message = "Deal value cannot be negative")
    private BigDecimal value;

    private LocalDateTime expectedCloseDate;
    private DealStatus dealStatus;
    private String closingNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}