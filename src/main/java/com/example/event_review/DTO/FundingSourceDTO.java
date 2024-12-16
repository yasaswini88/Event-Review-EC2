package com.example.event_review.DTO;

public class FundingSourceDTO {
    private Long SourceId;
    private String SourceName;

    // Getters and Setters
    public Long getSourceId() {
        return SourceId;
    }

    public void setSourceId(Long sourceId) {
        SourceId = sourceId;
    }

    public String getSourceName() {
        return SourceName;
    }

    public void setSourceName(String sourceName) {
        SourceName = sourceName;
    }
    
}
