package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.Exception.ResourceNotFoundException;
import com.crm.sales_pipeline.dto.DealDto;
import com.crm.sales_pipeline.entity.*;
import com.crm.sales_pipeline.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DealService {
    private final DealRepository dealRepository;
    private final AccountRepository accountRepository;
    private final ContactRepository contactRepository;
    private final DealStageRepository dealStageRepository;
    private final UserRepository userRepository;
    private final DealStageHistoryRepository dealStageHistoryRepository;
    private final RecordAccessService recordAccessService;

    public DealService(DealRepository dealRepository,
                       AccountRepository accountRepository,
                       ContactRepository contactRepository,
                       DealStageRepository dealStageRepository,
                       UserRepository userRepository,
                       DealStageHistoryRepository dealStageHistoryRepository,
                       RecordAccessService recordAccessService) {
        this.dealRepository = dealRepository;
        this.accountRepository = accountRepository;
        this.contactRepository = contactRepository;
        this.dealStageRepository = dealStageRepository;
        this.userRepository = userRepository;
        this.dealStageHistoryRepository = dealStageHistoryRepository;
        this.recordAccessService = recordAccessService;

    }
    public Page<DealDto> getAllDeals(int page, int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("dealId").descending()
        );
        Page<Deal> deals = recordAccessService.isSalesExecutive()
                ? dealRepository.findAllByOwner_Email(recordAccessService.currentEmail(), pageable)
                : dealRepository.findAll(pageable);
        return deals
                .map(this::mapToResponse);
    }
    public DealDto getDealById(Integer dealId) {
        Deal deal = findDealById(dealId);
        return mapToResponse(deal);
    }

    public DealDto createDeal(DealDto dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Account not found"));
        recordAccessService.requireAccess(account.getOwner(), "Account");

        Contact contact = contactRepository.findById(dto.getContactId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Contact not found"));
        recordAccessService.requireAccess(contact.getOwner(), "Contact");
        if (contact.getAccount().getAccId() != account.getAccId()) {
            throw new IllegalArgumentException(
                    "Contact does not belong to the selected account");}

        Deal_Stage stage = dealStageRepository.findById(dto.getDealStageId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Deal stage not found"));

        User owner = recordAccessService.resolveOwner(dto.getDealOwnerId());

        Deal deal = new Deal();
        deal.setTitle(dto.getTitle());
        deal.setAccount(account);
        deal.setContact(contact);
        deal.setId(stage);
        deal.setOwner(owner);
        deal.setValue(dto.getValue());
        deal.setClose_date(dto.getExpectedCloseDate());
        deal.setStatus(dto.getDealStatus());
        deal.setNote(dto.getClosingNote());

        Deal savedDeal = dealRepository.save(deal);
        return mapToResponse(savedDeal);
    }
    @Transactional
    public DealDto updateDeal(Integer dealId, DealDto dto) {
        Deal deal = findDealById(dealId);
        if (dto.getTitle() != null) {
            deal.setTitle(dto.getTitle());
        }
        if (dto.getValue() != null) {
            deal.setValue(dto.getValue());
        }
        if (dto.getExpectedCloseDate() != null) {
            deal.setClose_date(dto.getExpectedCloseDate());
        }
        if (dto.getDealStatus() != null) {
            deal.setStatus(dto.getDealStatus());
        }
        if (dto.getClosingNote() != null) {
            deal.setNote(dto.getClosingNote());
        }
        if (dto.getDealStageId() != null) {
            Deal_Stage oldStage = deal.getId();
            Deal_Stage newStage = dealStageRepository
                    .findById(dto.getDealStageId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Deal stage not found"));
            if (oldStage.getDeal_stageId() != newStage.getDeal_stageId()) {
                DealStage_history history = new DealStage_history();

                history.setDeal(deal);
                history.setFrom_stage(oldStage.getStage());
                history.setTo_stage(newStage.getStage());
                history.setChangedAt(LocalDateTime.now());
                history.setOwner(deal.getOwner());
                dealStageHistoryRepository.save(history);
                deal.setId(newStage);
            }
        }
        Deal updatedDeal = dealRepository.save(deal);
        return mapToResponse(updatedDeal);
    }

    private Deal findDealById(Integer id) {
        Deal deal = dealRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Deal not found"));
        recordAccessService.requireAccess(deal.getOwner(), "Deal");
        return deal;
    }
    public void deleteDeal(Integer id) {
        Deal deal = findDealById(id);
        dealRepository.delete(deal);
    }
    public List<DealStage_history> getDealHistory(Integer dealId) {
        findDealById(dealId);
        return dealStageHistoryRepository
                .findByDeal_DealIdOrderByChangedAtDesc(dealId);
    }

    private DealDto mapToResponse(Deal deal) {
        return new DealDto(
                deal.getDealId(),
                deal.getTitle(),
                deal.getAccount().getAccId(),
                deal.getContact().getContactId(),
                deal.getId().getDeal_stageId(),
                deal.getOwner().getUid(),
                deal.getValue(),
                deal.getClose_date(),
                deal.getStatus(),
                deal.getNote()
        );
    }

}
