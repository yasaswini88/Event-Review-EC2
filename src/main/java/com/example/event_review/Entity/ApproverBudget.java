package com.example.event_review.Entity;

import jakarta.persistence.*;

/**
 * This entity maps to a table that stores
 * an Approver's monthly budget and alert preferences.
 */
@Entity
public class ApproverBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The 'approver' field is a relationship (Many-to-One) with the existing 'User' entity.
     * We'll store which user (with an approver role) has this budget.
     */
    @ManyToOne
    @JoinColumn(name = "approver_id", nullable = false)
    private User approver;

    /**
     * The monthly budget allocated to this approver (e.g., 10000.00).
     */
    private Double monthlyBudget;

    /**
     * The year for which this budget is valid (e.g., 2025).
     */
    private int year;

    /**
     * The month for which this budget is valid (e.g., 1 for January).
     */
    private int month;

    /**
     * Flag indicating whether alerts are enabled or disabled for this approver.
     */
    private boolean alertEnabled;

    /**
     * Flag indicating whether the approver wants an alert at 50% usage.
     */
    private boolean alertAt50;

    /**
     * Flag indicating whether the approver wants an alert at 80% usage.
     */
    private boolean alertAt80;

   

    public ApproverBudget() {
        // Default constructor required by JPA
    }

   

    public Long getId() {
        return id;
    }

    public User getApprover() {
        return approver;
    }

    public void setApprover(User approver) {
        this.approver = approver;
    }

    public Double getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(Double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public boolean isAlertEnabled() {
        return alertEnabled;
    }

    public void setAlertEnabled(boolean alertEnabled) {
        this.alertEnabled = alertEnabled;
    }

    public boolean isAlertAt50() {
        return alertAt50;
    }

    public void setAlertAt50(boolean alertAt50) {
        this.alertAt50 = alertAt50;
    }

    public boolean isAlertAt80() {
        return alertAt80;
    }

    public void setAlertAt80(boolean alertAt80) {
        this.alertAt80 = alertAt80;
    }
}
