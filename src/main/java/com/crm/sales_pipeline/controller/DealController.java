package com.crm.sales_pipeline.controller;

import com.crm.sales_pipeline.dto.DealDto;
import com.crm.sales_pipeline.entity.DealStage_history;
import com.crm.sales_pipeline.service.DealService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/deals")
@Tag(name = "Deal")
@PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_EXECUTIVE')")
public class DealController {
    private final DealService dealService;
    public DealController(DealService dealService) {
        this.dealService = dealService;
    }
    @GetMapping
    public Page<DealDto> getAllDeals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return dealService.getAllDeals(page, size);
    }
    @GetMapping("/{id}")
    public DealDto getDealById(@PathVariable Integer id) {
        return dealService.getDealById(id);
    }
    @PostMapping
    public DealDto createDeal(@Valid @RequestBody DealDto dto) {
        return dealService.createDeal(dto);
    }
    @PutMapping("/{id}")
    public DealDto updateDeal(@PathVariable Integer id,
                              @RequestBody DealDto dto) {return dealService.updateDeal(id, dto);}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDeal(@PathVariable Integer id) {dealService.deleteDeal(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{dealId}/history")
    public ResponseEntity<List<DealStage_history>> getDealHistory(
            @PathVariable Integer dealId) {
        return ResponseEntity.ok(
                dealService.getDealHistory(dealId)
        );
    }
}
