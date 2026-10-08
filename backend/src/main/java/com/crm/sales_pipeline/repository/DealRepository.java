package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.Deal;
import com.crm.sales_pipeline.enums.DealStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Integer> {
    long countByStatus(DealStatus status);
    @Query("SELECT d.status, COUNT(d) FROM Deal d GROUP BY d.status")
    List<Object[]> countDealsByStatus();
    @Query("SELECT d.status, SUM(d.value) FROM Deal d GROUP BY d.status")
    List<Object[]> getDealValueByStatus();
    @Query("""
       SELECT d.owner.uid, d.owner.name, COUNT(d), SUM(d.value)
       FROM Deal d
       WHERE d.status = :status
       GROUP BY d.owner.uid, d.owner.name
       """)
    List<Object[]> getSalesPerformance(@Param("status") DealStatus status);
}
