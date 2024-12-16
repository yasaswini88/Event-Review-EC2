package com.example.event_review.Entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long deptId;
    
    private String deptName;
    
    @OneToMany(mappedBy = "department")
    private List<Proposal> proposals;
    
    // Getters and Setters
    public Long getDeptId() {
        return deptId;
    }
    
    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }
    
    public String getDeptName() {
        return deptName;
    }
    
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }
    
    public List<Proposal> getProposals() {
        return proposals;
    }
    
    public void setProposals(List<Proposal> proposals) {
        this.proposals = proposals;
    }
}