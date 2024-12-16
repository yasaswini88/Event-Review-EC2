package com.example.event_review.DTO;

import java.time.LocalDateTime;

public class ApprovalHistoryDTO {
    private Long id;
    private Long proposalId;
    private Long approverId;
    private String oldStatus;
    private String newStatus;
    private String comments;
    private LocalDateTime actionDate;
    private Long fundingSourceId;

    public Long getFundingSourceId() {
        return fundingSourceId;
    }   

    public void setFundingSourceId(Long fundingSourceId) {
        this.fundingSourceId = fundingSourceId;
    }   

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public Long getApproverId() {
        return approverId;
    }

    public void setApproverId(Long approverId) {
        this.approverId = approverId;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public LocalDateTime getActionDate() {
        return actionDate;
    }

    public void setActionDate(LocalDateTime actionDate) {
        this.actionDate = actionDate;
    }
}