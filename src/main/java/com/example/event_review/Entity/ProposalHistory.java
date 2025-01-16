package com.example.event_review.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

/**
 * This entity/table stores *older versions* of a Proposal whenever the Proposal is updated.
 * Each row is basically a snapshot of a Proposal at a certain point in time.
 */
@Entity
@Table(name = "proposal_history")
public class ProposalHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Link back to the main "Proposal" entity so we know which proposal
    // this historical record belongs to.
    @ManyToOne
    @JoinColumn(name = "proposal_id", nullable = false)
    @JsonBackReference
    private Proposal proposal;

    // 1) All the same fields you want to version
    private String itemName;
    private String category;
    private String description;
    private Integer quantity;
    private Double estimatedCost;
    private String vendorInfo;
    private String businessPurpose;
    private String status;
    private LocalDateTime proposalDate;
    private Long currentApproverId;
    private Long departmentId;

    // 2) A version number
    // We'll store "1" for the first version, "2" for the second, etc.
    private Integer versionNumber;

    // 3) Some metadata about the "change"
    private LocalDateTime changedOn; 
    private Long changedBy;  // e.g. userId of who caused the version, or email

    // ========== GETTERS / SETTERS ==========

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Proposal getProposal() {
        return proposal;
    }
    public void setProposal(Proposal proposal) {
        this.proposal = proposal;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }
    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public String getVendorInfo() {
        return vendorInfo;
    }
    public void setVendorInfo(String vendorInfo) {
        this.vendorInfo = vendorInfo;
    }

    public String getBusinessPurpose() {
        return businessPurpose;
    }
    public void setBusinessPurpose(String businessPurpose) {
        this.businessPurpose = businessPurpose;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getProposalDate() {
        return proposalDate;
    }
    public void setProposalDate(LocalDateTime proposalDate) {
        this.proposalDate = proposalDate;
    }

    public Long getCurrentApproverId() {
        return currentApproverId;
    }
    public void setCurrentApproverId(Long currentApproverId) {
        this.currentApproverId = currentApproverId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }
    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public LocalDateTime getChangedOn() {
        return changedOn;
    }
    public void setChangedOn(LocalDateTime changedOn) {
        this.changedOn = changedOn;
    }

    public Long getChangedBy() {
        return changedBy;
    }
    public void setChangedBy(Long changedBy) {
        this.changedBy = changedBy;
    }
}

