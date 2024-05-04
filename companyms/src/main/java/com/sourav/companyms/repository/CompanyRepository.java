package com.sourav.companyms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sourav.companyms.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
