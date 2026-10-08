package com.crm.sales_pipeline.entity;

import com.crm.sales_pipeline.enums.LifecycleStatus;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Contact",
        indexes = {
                @Index(name = "idx_contact_account", columnList = "account_id"),
                @Index(
                        name = "idx_contact_owner",
                        columnList = "contact_owner_id")
                        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private int contactId;


    @Column(name = "name", nullable=false)
    private String name;

    @Column(name = "email", length = 255, nullable=false,unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name="LifecycleStatus",nullable = false)
    private LifecycleStatus status;

    @Column(name = "phone_number")
    private String phone;

    @Column(name = "Job Title")
    private String job_title;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "contact_owner_id", referencedColumnName = "uid", nullable = false)
    private User owner;


    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "acc_id", nullable = false)
    private Account account;


}
