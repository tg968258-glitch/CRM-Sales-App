package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository  extends JpaRepository<RolePermission, Long> {
    List<RolePermission> findByRoleRoleId(Long roleId);
}
