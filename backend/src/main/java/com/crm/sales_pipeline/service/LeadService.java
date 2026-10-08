package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.Exception.ResourceNotFoundException;
import com.crm.sales_pipeline.dto.LeadConversionDto;
import com.crm.sales_pipeline.dto.LeadDto;
import com.crm.sales_pipeline.entity.*;
import com.crm.sales_pipeline.enums.DealStatus;
import com.crm.sales_pipeline.enums.LeadStatus;
import com.crm.sales_pipeline.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class LeadService {
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ContactRepository contactRepository;
            private final DealRepository dealRepository;
                    private final DealStageRepository dealStageRepository;
    private final RecordAccessService recordAccessService;

    public LeadService(LeadRepository leadRepository,
                       UserRepository userRepository,
                       AccountRepository accountRepository,
                       ContactRepository contactRepository,
                       DealRepository dealRepository,
                       DealStageRepository dealStageRepository,
                       RecordAccessService recordAccessService) {
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.contactRepository = contactRepository;
        this.dealRepository = dealRepository;
        this.dealStageRepository = dealStageRepository;
        this.recordAccessService = recordAccessService;
    }

    public Page<LeadDto> getAllLeads(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("leadId").descending());
        Page<Lead> leads = recordAccessService.isSalesExecutive()
                ? leadRepository.findAllByOwner_Email(recordAccessService.currentEmail(), pageable)
                : leadRepository.findAll(pageable);
        return leads
                .map(this::mapToResponse);
    }
    public LeadDto getLeadById(Integer leadId) {
        Lead lead = findLeadById(leadId);
        return mapToResponse(lead);
    }
        public LeadDto createLead (LeadDto dto){
            User owner = recordAccessService.resolveOwner(dto.getOwnerId());
            Lead lead = new Lead();

            lead.setOwner(owner);
            lead.setSalutation(dto.getSalutation());
            lead.setName(dto.getName());
            lead.setEmail(dto.getEmail());
            lead.setPhone(dto.getPhoneNumber());
            lead.setCompany_name(dto.getCompanyName());
            lead.setSource(dto.getSource());
            lead.setStatus(dto.getStatus());
            lead.setRating(dto.getLeadRating());
            lead.setValue(dto.getExpectedValue());
            lead.setNotes(dto.getNotes());

            Lead savedLead = leadRepository.save(lead);
            return mapToResponse(savedLead);
        }
    public LeadDto updateLead(Integer leadId, LeadDto dto) {
        Lead lead = findLeadById(leadId);
        if (dto.getSalutation() != null) {
            lead.setSalutation(dto.getSalutation());
        }
        if (dto.getName() != null) {
            lead.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            lead.setEmail(dto.getEmail());
        }
        if (dto.getPhoneNumber() != null) {
            lead.setPhone(dto.getPhoneNumber());
        }
        if (dto.getCompanyName() != null) {
            lead.setCompany_name(dto.getCompanyName());
        }
        if (dto.getSource() != null) {
            lead.setSource(dto.getSource());
        }
        if (dto.getStatus() != null) {
            lead.setStatus(dto.getStatus());
        }
        if (dto.getLeadRating() != null) {
            lead.setRating(dto.getLeadRating());
        }
        if (dto.getExpectedValue() != null) {
            lead.setValue(dto.getExpectedValue());
        }
        if (dto.getNotes() != null) {
            lead.setNotes(dto.getNotes());
        }
        Lead updatedLead = leadRepository.save(lead);
        return mapToResponse(updatedLead);
    }
    public void deleteLead(Integer leadId) {
        Lead lead = findLeadById(leadId);
        leadRepository.delete(lead);
    }
    @Transactional
    public LeadDto convertLead(Integer leadId, LeadConversionDto dto) {
        Lead lead = findLeadById(leadId);
        if (lead.getStatus() != LeadStatus.Qualified) {
            throw new RuntimeException("Only qualified lead can be converted");
        }
        Account account = new Account();
        account.setOwner(lead.getOwner());
        account.setName(lead.getCompany_name());
        account.setIndustry(dto.getIndustry());
        account.setPhone(lead.getPhone());

        account = accountRepository.save(account);

        Contact contact = new Contact();
        contact.setOwner(lead.getOwner());
        contact.setAccount(account);
        contact.setName(lead.getName());
        contact.setEmail(lead.getEmail());
        contact.setPhone(lead.getPhone());
        contact.setStatus(dto.getLifecycleStatus());

        contact = contactRepository.save(contact);
        Deal_Stage dealStage = dealStageRepository
                .findById(dto.getDealStageId())
                .orElseThrow(() -> new ResourceNotFoundException("Deal stage not found"));


        Deal deal = new Deal();

        deal.setTitle(dto.getDealTitle());
        deal.setAccount(account);
        deal.setContact(contact);
        deal.setId(dealStage);
        deal.setOwner(lead.getOwner());
        deal.setValue(lead.getValue());
        deal.setClose_date(dto.getExpectedCloseDate());
        deal.setStatus(DealStatus.open);

        dealRepository.save(deal);
        lead.setStatus(LeadStatus.Converted);
        leadRepository.save(lead);
        return mapToResponse(lead);
    }

    private Lead findLeadById(Integer id) {
        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lead not found"));
        recordAccessService.requireAccess(lead.getOwner(), "Lead");
        return lead;
    }
        private LeadDto mapToResponse (Lead lead){
        return new LeadDto(
                    lead.getLeadId(),
                    lead.getOwner().getUid(),
                    lead.getSalutation(),
                    lead.getName(),
                    lead.getEmail(),
                    lead.getPhone(),
                    lead.getCompany_name(),
                    lead.getSource(),
                    lead.getStatus(),
                    lead.getRating(),
                    lead.getValue(),
                    lead.getNotes()
            );
        }
    }
