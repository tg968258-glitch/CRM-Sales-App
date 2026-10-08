package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.Deal_Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DealStageRepository extends JpaRepository<Deal_Stage, Integer> {
}
