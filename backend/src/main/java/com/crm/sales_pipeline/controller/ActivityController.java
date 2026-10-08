package com.crm.sales_pipeline.controller;

import com.crm.sales_pipeline.dto.ActivityDto;
import com.crm.sales_pipeline.service.ActivityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities")
@Tag(name = "Activity")
@PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_EXECUTIVE')")
public class ActivityController {
    private final ActivityService activityService;
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }
    @GetMapping
    public Page<ActivityDto> getAllActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return activityService.getAllActivities(page, size);
    }
    @GetMapping("/{id}")
    public ActivityDto getActivityById(@PathVariable Integer id) {
        return activityService.getActivityById(id);
    }
    @PostMapping
    public ActivityDto createActivity(@Valid @RequestBody ActivityDto dto) {
        return activityService.createActivity(dto);
    }
    @PutMapping("/{id}")
    public ActivityDto updateActivity(@PathVariable Integer id,
                                      @RequestBody ActivityDto dto) {return activityService.updateActivity(id, dto);}
    @DeleteMapping("/{id}")
    public void deleteActivity(@PathVariable Integer id) {
        activityService.deleteActivity(id);}

}
