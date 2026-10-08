package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.Lead;
import com.crm.sales_pipeline.enums.LeadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Integer> {
    boolean existsByEmail(String email);
    long countByStatus(LeadStatus status);
    @Query("SELECT l.status, COUNT(l) FROM Lead l GROUP BY l.status")
    List<Object[]> countLeadsByStatus();
}
