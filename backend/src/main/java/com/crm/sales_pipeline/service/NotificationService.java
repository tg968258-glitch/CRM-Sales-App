package com.crm.sales_pipeline.service;

import com.crm.sales_pipeline.Exception.ResourceNotFoundException;
import com.crm.sales_pipeline.dto.NotificationDto;
import com.crm.sales_pipeline.entity.Notification;
import com.crm.sales_pipeline.entity.User;
import com.crm.sales_pipeline.enums.NotificationStatus;
import com.crm.sales_pipeline.repository.NotificationRepository;
import com.crm.sales_pipeline.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository=userRepository;
    }
    public List<NotificationDto> getAllNotifications() {
        return notificationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    public NotificationDto getNotificationById(Integer id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Notification not found"));
        return mapToResponse(notification);
    }
    public List<Notification> getMyNotifications(String email) {
        return notificationRepository.findByUser_Email(email)
                .stream()
                .sorted(Comparator.comparing(
                        Notification::getCreated_at,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
    }
    public NotificationDto markAsRead(Integer id, String email) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new ResourceNotFoundException("Notification not found");
        }

        if (notification.getStatus() == NotificationStatus.Unread) {
            notification.setStatus(NotificationStatus.Read);
            notification = notificationRepository.save(notification);
        }

        return mapToResponse(notification);
    }
    public NotificationDto createNotification(NotificationDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(dto.getMessage());
        notification.setType(dto.getNotificationType());
        notification.setStatus(dto.getNotificationStatus());
        notification.setCreated_at(LocalDateTime.now());
        notification.setSent_at(LocalDateTime.now());
        Notification savedNotification =
                notificationRepository.save(notification);
        return mapToResponse(savedNotification);
    }
    private NotificationDto mapToResponse(Notification notification) {
        return new NotificationDto(
                notification.getNotificationId(),
                notification.getUser().getUid(),
                notification.getMessage(),
                notification.getType(),
                notification.getStatus()
        );
    }
    }
