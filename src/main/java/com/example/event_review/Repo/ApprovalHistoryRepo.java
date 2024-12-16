package com.example.event_review.Repo;

import com.example.event_review.Entity.ApprovalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ApprovalHistoryRepo extends JpaRepository<ApprovalHistory, Long> {
    List<ApprovalHistory> findByProposal_ProposalId(Long proposalId);
    List<ApprovalHistory> findByApprover_UserId(Long approverId);
    List<ApprovalHistory> findByProposal_ProposalIdOrderByActionDateDesc(Long proposalId);
}