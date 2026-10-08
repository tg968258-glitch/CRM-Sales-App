package com.crm.sales_pipeline.controller;

import com.crm.sales_pipeline.dto.LeadConversionDto;
import com.crm.sales_pipeline.dto.LeadDto;
import com.crm.sales_pipeline.service.LeadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/leads")
@Tag(name = "Leads")
@PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_EXECUTIVE')")
public class LeadController {
    private final LeadService leadService;

    public LeadController(LeadService leadService) {
        this.leadService = leadService;
    }

    @GetMapping
    public Page<LeadDto> getAllLeads(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return leadService.getAllLeads(page, size);
    }

    @GetMapping("/{id}")
    public LeadDto getLeadById(@PathVariable("id") Integer leadId) {
        return leadService.getLeadById(leadId);
    }

    @PostMapping
    public LeadDto createLead(@Valid @RequestBody LeadDto dto) {
        return leadService.createLead(dto);
    }

    @PutMapping("/{id}")
    public LeadDto updateLead(
            @PathVariable("id") Integer leadId,
            @RequestBody LeadDto dto) {
        return leadService.updateLead(leadId, dto);
    }

    @PutMapping("/{leadId}/convert")
    public ResponseEntity<LeadDto> convertLead(
            @PathVariable("leadId") Integer leadId,
            @Valid @RequestBody LeadConversionDto dto) {
        return ResponseEntity.ok(leadService.convertLead(leadId, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public void deleteLead(@PathVariable("id") Integer leadId) {
        leadService.deleteLead(leadId);
    }
}
