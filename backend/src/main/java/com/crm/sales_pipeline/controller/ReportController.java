package com.crm.sales_pipeline.controller;

import com.crm.sales_pipeline.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/leads/status")
    public ResponseEntity<Map<String, Long>> getLeadsByStatus() {
        return ResponseEntity.ok(reportService.getLeadsByStatus());
    }

    @GetMapping("/deals/status")
    public ResponseEntity<Map<String, Long>> getDealsByStatus() {
        return ResponseEntity.ok(reportService.getDealsByStatus());
    }

    @GetMapping("/deals/value")
    public ResponseEntity<Map<String, BigDecimal>> getDealValueByStatus() {
        return ResponseEntity.ok(reportService.getDealValueByStatus());
    }

    @GetMapping("/sales-performance")
    public ResponseEntity<List<Map<String, Object>>> getSalesPerformance() {
        return ResponseEntity.ok(reportService.getSalesPerformance());
    }
}