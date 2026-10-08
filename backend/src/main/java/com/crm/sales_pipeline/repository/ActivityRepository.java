package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.Activities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ActivityRepository extends JpaRepository<Activities, Integer> {
    long countByDueAtBeforeAndCompletedAtIsNull(LocalDateTime dateTime);
    Page<Activities> findAllByUser_Email(String email, Pageable pageable);
    long countByUser_EmailAndDueAtBeforeAndCompletedAtIsNull(String email, LocalDateTime dateTime);
}
