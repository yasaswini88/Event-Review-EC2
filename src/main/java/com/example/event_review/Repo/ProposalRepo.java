package com.example.event_review.Repo;

import com.example.event_review.Entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProposalRepo extends JpaRepository<Proposal, Long> {
    List<Proposal> findByUser_UserId(Long userId); // This method is used to find all proposals created by a specific user.
    List<Proposal> findByCurrentApprover_UserId(Long approverId); //Fetch proposals assigned to a specific approver.

    List<Proposal> findByStatus(String status); // This method is used to find all proposals that have a specific status, such as "pending", "approved", or "rejected".
    List<Proposal> findByDepartment_DeptId(Long deptId);  // This method is used to find all proposals associated with a specific department.
    List<Proposal> findByProposalDateBetween(LocalDateTime startDate, LocalDateTime endDate); // This method is used to find all proposals created between two specific dates.
List<Proposal> findByUser_UserIdAndStatus(Long userId, String status);// This method is used to find all proposals created by a specific user with a specific status.
List<Proposal> findByUser_UserIdAndStatusAndProposalDateBetween(
    Long userId, String status, LocalDateTime startDate, LocalDateTime endDate);  // This method is used to find all proposals created by a specific user with a specific status between two specific dates.

}