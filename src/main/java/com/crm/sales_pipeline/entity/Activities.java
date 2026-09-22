package com.crm.sales_pipeline.entity;

import com.crm.sales_pipeline.enums.ActivityType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "Activity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Activities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int Id;

    @Enumerated(EnumType.STRING)
    @Column(name="ActivityType",nullable = false)
    private ActivityType type;

    @Column(name = "Subject", nullable=false)
    private String subject;

    @Column(name = "Status", nullable=false)
    private String status;

    @Column(name = "due_at")
    private Date dueAt;

    @Column(name = "completed_at")
    private Date completedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "deal_id", referencedColumnName = "deal_id")
    private Deal deal;

    @ManyToOne
    @JoinColumn(name = "lead_id", referencedColumnName = "lead_id")
    private Lead lead;

    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "uid", nullable = false)
    private User user;



}
