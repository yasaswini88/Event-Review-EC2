package com.example.event_review.DTO;

import java.util.Map;

public class YearlyStatsDTO {
    private int totalProposals;
    private int approvedProposals;
    private double budgetApproved;
    private Map<String, MonthlyStatsDTO> monthlyBreakdown; // e.g., "Jan" -> { total=5, approved=4, budgetApproved=2 }

    // Getters and Setters
    public int getTotalProposals() {
        return totalProposals;
    }

    public void setTotalProposals(int totalProposals) {
        this.totalProposals = totalProposals;
    }

    public int getApprovedProposals() {
        return approvedProposals;
    }

    public void setApprovedProposals(int approvedProposals) {
        this.approvedProposals = approvedProposals;
    }

    public double getBudgetApproved() {
        return budgetApproved;
    }

    public void setBudgetApproved(double budgetApproved) {
        this.budgetApproved = budgetApproved;
    }

    public Map<String, MonthlyStatsDTO> getMonthlyBreakdown() {
        return monthlyBreakdown;
    }

    public void setMonthlyBreakdown(Map<String, MonthlyStatsDTO> monthlyBreakdown) {
        this.monthlyBreakdown = monthlyBreakdown;
    }
}
