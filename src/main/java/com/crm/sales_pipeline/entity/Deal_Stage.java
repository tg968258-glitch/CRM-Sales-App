package com.crm.sales_pipeline.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "DealStage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Deal_Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deal_stage_id")
    private int deal_stageId;

    @Column(name = "Stage", nullable=false)
    private String stage;

    @Column(name = "display_order", nullable = false)
    private int order;

    @Column(name = "probability", nullable = false)
    private int probability;


    @Column(name = "is_active", nullable = false)
    private boolean isActive;

}
