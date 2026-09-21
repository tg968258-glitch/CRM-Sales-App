package com.crm.sales_pipeline.entity;

import com.crm.sales_pipeline.enums.Industry;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acc_id")
    private int accId;


    @Column(name = "account_name", nullable=false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name="industry",nullable = false)
    private Industry industry;

    @Column(name = "phone_number")
    private String phone;

    @Column(name = "Website")
    private String website;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "acc_owner_id", referencedColumnName = "uid", nullable = false)
    private User owner;


}
