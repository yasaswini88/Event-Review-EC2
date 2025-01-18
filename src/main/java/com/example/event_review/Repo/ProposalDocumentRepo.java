package com.example.event_review.Repo;

import com.example.event_review.Entity.ProposalDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProposalDocumentRepo extends JpaRepository<ProposalDocument, Long> {
    // e.g. get all documents for a given proposal:
    List<ProposalDocument> findByProposal_ProposalId(Long proposalId);
}
