package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.dto.DashboardDto;
import com.crm.sales_pipeline.enums.DealStatus;
import com.crm.sales_pipeline.enums.LeadStatus;
import com.crm.sales_pipeline.repository.ActivityRepository;
import com.crm.sales_pipeline.repository.DealRepository;
import com.crm.sales_pipeline.repository.LeadRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DashboardService {
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;
    private final ActivityRepository activityRepository;

    public DashboardService(
            LeadRepository leadRepository,
            DealRepository dealRepository,
            ActivityRepository activityRepository) {
        this.leadRepository = leadRepository;
        this.dealRepository = dealRepository;
        this.activityRepository = activityRepository;
    }
    public DashboardDto getDashboard() {
        long totalLeads = leadRepository.count();
        long qualifiedLeads = leadRepository.countByStatus(LeadStatus.Qualified);
        long totalDeals = dealRepository.count();
        long openDeals = dealRepository.countByStatus(DealStatus.open);
        long wonDeals = dealRepository.countByStatus(DealStatus.won);
        long lostDeals = dealRepository.countByStatus(DealStatus.lost);
        long overdueActivities = activityRepository.countByDueAtBeforeAndCompletedAtIsNull(LocalDateTime.now());
        return new DashboardDto(
                totalLeads,
                qualifiedLeads,
                totalDeals,
                openDeals,
                wonDeals,
                lostDeals,
                overdueActivities
        );
    }
}
