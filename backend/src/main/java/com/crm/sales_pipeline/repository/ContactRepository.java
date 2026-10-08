package com.crm.sales_pipeline.repository;

import com.crm.sales_pipeline.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    Page<Contact> findAllByOwner_Email(String email, Pageable pageable);
}
