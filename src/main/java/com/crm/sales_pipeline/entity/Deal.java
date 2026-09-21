package com.crm.sales_pipeline.entity;

import com.crm.sales_pipeline.enums.DealStatus;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "Deal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deal_id")
    private int dealId;


    @Column(name = "Title", nullable=false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name="DealStatus",nullable = false)
    private DealStatus status;


    @Column(name = "expected_close_date")
    private Date close_date;


    @Column(name = "Value")
    private BigDecimal value;

    @Column(name="Closing Note")
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @ManyToOne
    @JoinColumn(name = "deal_owner_id", referencedColumnName = "uid", nullable = false)
    private User owner;


    @ManyToOne
    @JoinColumn(name = "deal_stage_id", referencedColumnName = "deal_stage_id", nullable = false)
    private Deal_Stage id;


    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "acc_id", nullable = false)
    private Account account;


    @ManyToOne
    @JoinColumn(name = "contact_id", referencedColumnName = "contact_id", nullable = false)
    private Contact contact;


}
