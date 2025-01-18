package com.example.event_review.Repo;

import com.example.event_review.Entity.ProposalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProposalHistoryRepo extends JpaRepository<ProposalHistory, Long> {
    // Optional: a method to find all version rows for a single proposal
    List<ProposalHistory> findByProposal_ProposalId(Long proposalId);

    // If you want to get the highest version number for a given proposal, you
    // can do a custom query or rely on other logic in your service.

    
}
