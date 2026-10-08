package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.Exception.ResourceNotFoundException;
import com.crm.sales_pipeline.dto.DealStageDto;
import com.crm.sales_pipeline.entity.Deal_Stage;
import com.crm.sales_pipeline.repository.DealStageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DealStageService {
    private final DealStageRepository dealStageRepository;
    public DealStageService(DealStageRepository dealStageRepository) {
        this.dealStageRepository = dealStageRepository;
    }
    public List<DealStageDto> getAllDealStages() {
        return dealStageRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    public DealStageDto getDealStageById(Integer id) {
        Deal_Stage dealStage = findDealStageById(id);
        return mapToResponse(dealStage);
    }
    public DealStageDto createDealStage(DealStageDto dto) {
        Deal_Stage dealStage = new Deal_Stage();
        dealStage.setStage(dto.getStage());
        dealStage.setProbability(dto.getProbability());
        dealStage.setOrder(dto.getDisplayOrder());
        dealStage.setActive(dto.isActive());

        Deal_Stage savedDealStage = dealStageRepository.save(dealStage);
        return mapToResponse(savedDealStage);


    }
    public DealStageDto updateDealStage(Integer id, DealStageDto dto) {
        Deal_Stage dealStage = findDealStageById(id);

        if (dto.getStage() != null) {
            dealStage.setStage(dto.getStage());
        }
        if (dto.getProbability() != null) {
            dealStage.setProbability(dto.getProbability());
        }
        if (dto.getDisplayOrder() != null) {
            dealStage.setOrder(dto.getDisplayOrder());
        }
        Deal_Stage updatedDealStage =
                dealStageRepository.save(dealStage);
        return mapToResponse(updatedDealStage);
    }

    private Deal_Stage findDealStageById(Integer id) {
        return dealStageRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Deal stage not found"));
    }

    private DealStageDto mapToResponse(Deal_Stage dealStage) {
        return new DealStageDto(
                dealStage.getDeal_stageId(),
                dealStage.getStage(),
                dealStage.getProbability(),
                dealStage.getOrder(),
                dealStage.isActive()
        );
    }
}
