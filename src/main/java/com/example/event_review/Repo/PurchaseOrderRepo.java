package com.example.event_review.Repo;

import com.example.event_review.Entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderRepo extends JpaRepository<PurchaseOrder, Long> {
    List<PurchaseOrder> findByProposal_Status(String status);
    List<PurchaseOrder> findByOrderStatus(String orderStatus);
    List<PurchaseOrder> findByDeliveryStatus(String deliveryStatus);
    List<PurchaseOrder> findByProposal_Department_DeptId(Long departmentId);
    PurchaseOrder findByProposal_ProposalId(Long proposalId);
}
