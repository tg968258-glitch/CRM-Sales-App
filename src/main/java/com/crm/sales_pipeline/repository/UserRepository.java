package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

}
