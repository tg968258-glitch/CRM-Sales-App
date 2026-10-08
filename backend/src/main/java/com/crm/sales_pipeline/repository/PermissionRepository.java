package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository  extends JpaRepository<Permission, Long> {
}
