package com.example.event_review.DTO;

public class MonthlyStatsDTO {
    private int total;
    private int approved;
    private double budgetApproved;

    // Getters and Setters
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getApproved() {
        return approved;
    }

    public void setApproved(int approved) {
        this.approved = approved;
    }

    public double getBudgetApproved() {
        return budgetApproved;
    }

    public void setBudgetApproved(double budgetApproved) {
        this.budgetApproved = budgetApproved;
    }
}
