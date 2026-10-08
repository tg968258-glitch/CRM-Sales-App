package com.crm.sales_pipeline.entity;

import com.crm.sales_pipeline.enums.NotificationStatus;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Notification" ,indexes = {
        @Index(
                name = "idx_notification_user_status",
                columnList = "user_id, notification_status"
        )
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private int notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "uid", nullable = false)
    private User user;

    @Column(name = "Notification_Type")
    private String type;

    @Column(name = "message", length=500, nullable=false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_status", nullable = false)
    private NotificationStatus status;

    @Column(name = "created at", nullable=false,updatable = false)
    private LocalDateTime created_at;

    @Column(name = "sent at", nullable=false)
    private LocalDateTime sent_at;
}
