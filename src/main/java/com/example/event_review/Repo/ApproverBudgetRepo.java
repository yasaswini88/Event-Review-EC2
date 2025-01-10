package com.example.event_review.Repo;

import com.example.event_review.Entity.ApproverBudget;
import com.example.event_review.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * This repository interface will allow us to
 * perform CRUD operations on the ApproverBudget table.
 */
@Repository
public interface ApproverBudgetRepo extends JpaRepository<ApproverBudget, Long> {

    /**
     * Finds an ApproverBudget record by a given user (approver), year, and month.
     * We'll need this to quickly check or update a specific user's monthly budget record.
     */
    Optional<ApproverBudget> findByApproverAndYearAndMonth(User approver, int year, int month);
}
