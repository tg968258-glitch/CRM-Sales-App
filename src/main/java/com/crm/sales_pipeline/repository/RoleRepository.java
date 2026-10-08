package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.Role;
import com.crm.sales_pipeline.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(UserRole roleName);
}
