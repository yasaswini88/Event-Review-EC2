package com.example.event_review.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "proposal_document")
public class ProposalDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Link back to the main "Proposal" entity, so we know 
    // which proposal this document belongs to.
    @ManyToOne
    @JoinColumn(name = "proposal_id", nullable = false)
    @JsonIgnore
    private Proposal proposal;

    private String fileName;
    private String fileType;
    private String s3Key;
    private Long fileSize;

    // new field => who uploaded it (e.g. userId)
    private Long uploadedBy;  

    private LocalDateTime uploadedAt;

    // Constructors
    public ProposalDocument() {
    }

    // getters & setters
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

    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getS3Key() {
        return s3Key;
    }
    public void setS3Key(String s3Key) {
        this.s3Key = s3Key;
    }

    public Long getFileSize() {
        return fileSize;
    }
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getUploadedBy() {
        return uploadedBy;
    }
    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
