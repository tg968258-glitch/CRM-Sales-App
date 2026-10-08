package com.crm.sales_pipeline.controller;

import com.crm.sales_pipeline.dto.DealStageDto;
import com.crm.sales_pipeline.service.DealStageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deal-stages")
@Tag(name = "DealStage")
@PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_EXECUTIVE')")
public class DealStageController {
    private final DealStageService dealStageService;
    public DealStageController(DealStageService dealStageService) {
        this.dealStageService = dealStageService;
    }
    @GetMapping
    public List<DealStageDto> getAllDealStages() {
        return dealStageService.getAllDealStages();
    }
    @GetMapping("/{id}")
    public DealStageDto getDealStageById(@PathVariable Integer id) {
        return dealStageService.getDealStageById(id);
    }
    @PostMapping  @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public DealStageDto createDealStage(@RequestBody DealStageDto dto) {
        return dealStageService.createDealStage(dto);
    }
    @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public DealStageDto updateDealStage(@PathVariable Integer id,
                                        @RequestBody DealStageDto dto) {return dealStageService.updateDealStage(id, dto);}
}
