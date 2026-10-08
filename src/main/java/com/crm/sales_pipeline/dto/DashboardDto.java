package com.crm.sales_pipeline.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {
    private long totalLeads;
    private long qualifiedLeads;
    private long totalDeals;
    private long openDeals;
    private long wonDeals;
    private long lostDeals;
    private long overdueActivities;
}