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
    private final RecordAccessService recordAccessService;

    public DashboardService(
            LeadRepository leadRepository,
            DealRepository dealRepository,
            ActivityRepository activityRepository,
            RecordAccessService recordAccessService) {
        this.leadRepository = leadRepository;
        this.dealRepository = dealRepository;
        this.activityRepository = activityRepository;
        this.recordAccessService = recordAccessService;
    }
    public DashboardDto getDashboard() {
        if (recordAccessService.isSalesExecutive()) {
            String email = recordAccessService.currentEmail();
            return new DashboardDto(
                    leadRepository.countByOwner_Email(email),
                    leadRepository.countByStatusAndOwner_Email(LeadStatus.Qualified, email),
                    dealRepository.countByOwner_Email(email),
                    dealRepository.countByStatusAndOwner_Email(DealStatus.open, email),
                    dealRepository.countByStatusAndOwner_Email(DealStatus.won, email),
                    dealRepository.countByStatusAndOwner_Email(DealStatus.lost, email),
                    activityRepository.countByUser_EmailAndDueAtBeforeAndCompletedAtIsNull(email, LocalDateTime.now())
            );
        }
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
