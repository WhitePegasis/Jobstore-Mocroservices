package com.sourav.jobms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sourav.jobms.model.Job;

public interface JobRepository extends JpaRepository<Job, Long> {
}
