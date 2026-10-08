package com.crm.sales_pipeline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DealStageDto {
    private Integer dealStageId;
    private String stage;
    private Integer probability;
    private Integer displayOrder;
    private boolean isActive;
}
