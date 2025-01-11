package com.example.event_review.Repo;

import com.example.event_review.Entity.PurchaseOrderNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderNoteRepo extends JpaRepository<PurchaseOrderNote, Long> {
    List<PurchaseOrderNote> findByPurchaseOrder_OrderId(Long orderId);
}
