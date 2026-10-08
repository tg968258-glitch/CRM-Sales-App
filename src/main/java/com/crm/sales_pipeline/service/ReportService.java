package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.enums.DealStatus;
import com.crm.sales_pipeline.repository.LeadRepository;
import com.crm.sales_pipeline.repository.DealRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class ReportService {

    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;

    public ReportService(LeadRepository leadRepository,
                         DealRepository dealRepository) {
        this.leadRepository = leadRepository;
        this.dealRepository = dealRepository;
    }

    public Map<String, Long> getLeadsByStatus() {
        Map<String, Long> report = new LinkedHashMap<>();

        for (Object[] row : leadRepository.countLeadsByStatus()) {
            report.put(row[0].toString(), (Long) row[1]);
        }

        return report;
    }

    public Map<String, Long> getDealsByStatus() {
        Map<String, Long> report = new LinkedHashMap<>();

        for (Object[] row : dealRepository.countDealsByStatus()) {
            report.put(row[0].toString(), (Long) row[1]);
        }

        return report;
    }

    public Map<String, BigDecimal> getDealValueByStatus() {
        Map<String, BigDecimal> report = new LinkedHashMap<>();

        for (Object[] row : dealRepository.getDealValueByStatus()) {
            report.put(
                    row[0].toString(),
                    row[1] == null ? BigDecimal.ZERO : (BigDecimal) row[1]
            );
        }

        return report;
    }

    public List<Map<String, Object>> getSalesPerformance() {
        List<Map<String, Object>> report = new ArrayList<>();

        for (Object[] row :
                dealRepository.getSalesPerformance(DealStatus.won)) {

            Map<String, Object> data = new LinkedHashMap<>();

            data.put("userId", row[0]);
            data.put("name", row[1]);
            data.put("wonDeals", row[2]);
            data.put("revenue",
                    row[3] == null ? BigDecimal.ZERO : row[3]);

            report.add(data);
        }

        return report;
    }
}