package com.crm.sales_pipeline.controller;

import com.crm.sales_pipeline.dto.NotificationDto;
import com.crm.sales_pipeline.entity.Notification;
import com.crm.sales_pipeline.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@Tag(name = "Notification")
@PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER', 'SALES_EXECUTIVE')")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    @GetMapping
    public List<NotificationDto> getAllNotifications() {
        return notificationService.getAllNotifications();
    }
    @GetMapping("/{id}")
    public NotificationDto getNotificationById(@PathVariable Integer id) {
        return notificationService.getNotificationById(id);
    }
    @GetMapping("/my")
    public ResponseEntity<List<Notification>> getMyNotifications(
            Authentication authentication) {
        return ResponseEntity.ok(
                notificationService.getMyNotifications(authentication.getName())
        );
    }
    @PostMapping  @PreAuthorize("hasAnyRole('ADMIN', 'SALES_MANAGER')")
    public NotificationDto createNotification(@RequestBody NotificationDto dto) {
        return notificationService.createNotification(dto);
    }
}
