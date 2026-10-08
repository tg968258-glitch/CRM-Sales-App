package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.DealStage_history;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealStageHistoryRepository extends JpaRepository<DealStage_history, Integer> {
    List<DealStage_history> findByDeal_DealIdOrderByChangedAtDesc(Integer dealId);
}
