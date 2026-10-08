package com.crm.sales_pipeline.dto;

import com.crm.sales_pipeline.enums.Industry;
import com.crm.sales_pipeline.enums.LifecycleStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadConversionDto {
    private Industry industry;
    private LifecycleStatus lifecycleStatus;
    private String dealTitle;
    private Integer dealStageId;
    private LocalDateTime expectedCloseDate;
}
