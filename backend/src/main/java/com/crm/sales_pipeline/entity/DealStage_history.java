package com.crm.sales_pipeline.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "DealStage_History")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DealStage_history {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private int historyId;

    @Column(name = "from_stage", nullable=false)
    private String from_stage;

    @Column(name = "to_stage", nullable=false)
    private String to_stage;

    @Column(name = "changed_at")
    private LocalDateTime changedAt;

    @ManyToOne
    @JoinColumn(name = "Changed_By", referencedColumnName = "uid", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "deal_id", referencedColumnName = "deal_id", nullable = false)
    private Deal deal;
}
