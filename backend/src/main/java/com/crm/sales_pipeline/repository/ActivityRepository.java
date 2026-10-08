package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.Activities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ActivityRepository extends JpaRepository<Activities, Integer> {
    long countByDueAtBeforeAndCompletedAtIsNull(LocalDateTime dateTime);
}
