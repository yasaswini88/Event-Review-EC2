package com.example.event_review.DTO;

import java.time.LocalDateTime;

public class DocumentDTO {


    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private Long uploadedBy;
    private LocalDateTime uploadedAt;
    private Long proposalId;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public Long getProposalId() {
        return proposalId;
    }
    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }



}
