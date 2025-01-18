package com.example.event_review.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long proposalId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "current_approver_id")
    private User currentApprover;

    @ManyToOne
    @JoinColumn(name = "dept_id", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "proposal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProposalHistory> historyVersions = new ArrayList<>();

    public Long getDeptId() {
        return department.getDeptId();
    }

    public void setDeptId(Long deptId) {
        this.department.setDeptId(deptId);
    }

    private String itemName;
    private String category;
    private String description;
    private Integer quantity;
    private Double estimatedCost;
    private String vendorInfo;
    private String businessPurpose;
    private String status;
    private LocalDateTime proposalDate;

    @Column(name = "expected_due_date", nullable = true)
    private LocalDate expectedDueDate;

    // getters and setters

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public User getCurrentApprover() {
        return currentApprover;
    }

    public void setCurrentApprover(User currentApprover) {
        this.currentApprover = currentApprover;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<ProposalHistory> getHistoryVersions() {
        return historyVersions;
    }

    public void setHistoryVersions(List<ProposalHistory> historyVersions) {
        this.historyVersions = historyVersions;
    }

    public LocalDate getExpectedDueDate() {
        return expectedDueDate;
    }
    
    public void setExpectedDueDate(LocalDate expectedDueDate) {
        this.expectedDueDate = expectedDueDate;
    }

}