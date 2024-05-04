package com.sourav.reviewms.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sourav.reviewms.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCompanyId(Long companyId);
}
