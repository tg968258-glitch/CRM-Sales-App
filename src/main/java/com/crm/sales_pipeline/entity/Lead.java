package com.crm.sales_pipeline.entity;

import com.crm.sales_pipeline.enums.LeadStatus;
import com.crm.sales_pipeline.enums.Salutation;
import com.crm.sales_pipeline.enums.LeadSource;
import com.crm.sales_pipeline.enums.LeadRating;

import jakarta.persistence.*;
import jakarta.persistence.Index;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(name = "Leads",  indexes = {
        @Index(name = "idx_lead_owner", columnList = "owner_id"),
        @Index(name = "idx_lead_email", columnList = "email"),
        @Index(name = "idx_lead_status", columnList = "status"),
        @Index(name = "idx_lead_source", columnList = "source")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Lead {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "lead_id")
private int leadId;

@Enumerated(EnumType.STRING)
@Column(name = "Salutation")
private Salutation  salutation;

@Column(name = "name", nullable=false)
private String name;

@ColumnDefault("New")
@Enumerated(EnumType.STRING)
@Column(name="status",nullable = false)
private LeadStatus status;

@Column(name = "email", length = 255, nullable=false,unique = true)
private String email;

@Column(name = "phone_number")
private String phone;

@Column(name = "company_name")
private String company_name;

@ColumnDefault("Website")
@Enumerated(EnumType.STRING)
@Column(name = "source")
private LeadSource source;

@Column(name = "notes")
private String notes;

@Enumerated(EnumType.STRING)
@Column(name = "lead_rating")
private LeadRating rating;

@Column(name = "expected_value")
private BigDecimal value;

@ManyToOne
@JoinColumn(name = "owner_id", referencedColumnName = "uid", nullable = false)
private User owner;

}
