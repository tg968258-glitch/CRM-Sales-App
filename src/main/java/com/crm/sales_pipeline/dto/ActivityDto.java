package com.crm.sales_pipeline.dto;
import com.crm.sales_pipeline.enums.ActivityType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ActivityDto {
    private Integer id;
    private Integer dealId;
    private Integer leadId;
    private ActivityType activityType;

    @NotBlank(message = "Subject is required")
    private String subject;

    private String status;
    private LocalDateTime dueAt;
    private LocalDateTime completedAt;
}
