package com.example.event_review.Repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.event_review.Entity.FundingSource;

public interface FundingSourceRepo extends JpaRepository<FundingSource, Long> {
    
}
