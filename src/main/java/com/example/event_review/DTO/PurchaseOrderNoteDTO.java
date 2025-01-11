package com.example.event_review.DTO;

import java.time.LocalDateTime;

public class PurchaseOrderNoteDTO {
    private Long noteId;
    private Long orderId;       // so the frontend knows which PurchaseOrder this belongs to
    private String noteText;
    private LocalDateTime createdDate;
    private String createdBy;

    // Constructors
    public PurchaseOrderNoteDTO() {
    }

    public PurchaseOrderNoteDTO(Long noteId, Long orderId, String noteText, LocalDateTime createdDate, String createdBy) {
        this.noteId = noteId;
        this.orderId = orderId;
        this.noteText = noteText;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
    }

    // Getters and setters
    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
}
