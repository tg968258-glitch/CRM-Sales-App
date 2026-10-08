package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.Exception.ResourceNotFoundException;
import com.crm.sales_pipeline.dto.ActivityDto;
import com.crm.sales_pipeline.entity.Activities;
import com.crm.sales_pipeline.entity.User;
import com.crm.sales_pipeline.entity.Deal;
import com.crm.sales_pipeline.entity.Lead;
import com.crm.sales_pipeline.repository.ActivityRepository;
import com.crm.sales_pipeline.repository.DealRepository;
import com.crm.sales_pipeline.repository.LeadRepository;
import com.crm.sales_pipeline.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final DealRepository dealRepository;
    private final LeadRepository leadRepository;
    private final UserRepository userRepository;

    public ActivityService(ActivityRepository activityRepository,
                           DealRepository dealRepository,
                           LeadRepository leadRepository,
                           UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.dealRepository = dealRepository;
        this.leadRepository = leadRepository;
        this.userRepository = userRepository;
    }
    public Page<ActivityDto> getAllActivities(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending()
        );
        return activityRepository.findAll(pageable)
                .map(this::mapToResponse);
    }
    public ActivityDto getActivityById(Integer activityId) {
        Activities activity = findActivityById(activityId);
        return mapToResponse(activity);
    }

    public ActivityDto createActivity(ActivityDto dto) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email);

        Activities activity = new Activities();
        activity.setUser(user);
        if (dto.getDealId() != null) {
            Deal deal = dealRepository.findById(dto.getDealId())
                    .orElseThrow(() -> new RuntimeException("Deal not found"));
            activity.setDeal(deal);
}
        if (dto.getLeadId() != null) {
            Lead lead = leadRepository.findById(dto.getLeadId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Lead not found"));
            activity.setLead(lead);
        }
        activity.setType(dto.getActivityType());
        activity.setSubject(dto.getSubject());
        activity.setStatus(dto.getStatus());
        activity.setDueAt(dto.getDueAt());
        activity.setCompletedAt(dto.getCompletedAt());

        Activities savedActivity = activityRepository.save(activity);
        return mapToResponse(savedActivity);
    }
    public ActivityDto updateActivity(Integer activityId, ActivityDto dto) {
        Activities activity = findActivityById(activityId);

        if (dto.getActivityType() != null) {
            activity.setType(dto.getActivityType());
        }
        if (dto.getSubject() != null) {
            activity.setSubject(dto.getSubject());
        }
        if (dto.getStatus() != null) {
            activity.setStatus(dto.getStatus());
        }
        if (dto.getDueAt() != null) {
            activity.setDueAt(dto.getDueAt());
        }
        if (dto.getCompletedAt() != null) {
            activity.setCompletedAt(dto.getCompletedAt());
        }
        Activities updatedActivity =
                activityRepository.save(activity);
        return mapToResponse(updatedActivity);
    }

    public void deleteActivity(Integer activityId) {
        Activities activity = findActivityById(activityId);
        activityRepository.delete(activity);
    }

    private Activities findActivityById(Integer id) {
        return activityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Activity not found"));
    }
    private ActivityDto mapToResponse(Activities activity) {
        return new ActivityDto(
                activity.getId(),
                activity.getDeal() != null
                        ? activity.getDeal().getDealId() : null,
                activity.getLead() != null
                        ? activity.getLead().getLeadId() : null,
                activity.getType(),
                activity.getSubject(),
                activity.getStatus(),
                activity.getDueAt(),
                activity.getCompletedAt()
        );
    }

    }
