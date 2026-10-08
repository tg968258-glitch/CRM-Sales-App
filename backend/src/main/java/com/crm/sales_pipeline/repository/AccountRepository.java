package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Page<Account> findAllByOwner_Email(String email, Pageable pageable);
}
