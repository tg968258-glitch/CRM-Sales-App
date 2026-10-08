package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer>{
    List<Notification> findByUser_Email(String email);
}
