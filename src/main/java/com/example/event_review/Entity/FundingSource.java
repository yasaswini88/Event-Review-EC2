package com.example.event_review.Entity;
import jakarta.persistence.*;

@Entity
public class FundingSource {
     @Id 
        @GeneratedValue(strategy = GenerationType.AUTO) 
        private Long SourceId;
        private String SourceName;

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
