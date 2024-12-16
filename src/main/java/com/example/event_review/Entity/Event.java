package com.example.event_review.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long eventId;
    
    private String eventName;
    private String eventType;
    private LocalDateTime eventDate;
    private String eventDescription;
    private String eventLocation;

    // Getters and Setters
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    @Override
    public String toString() {
        return "Event [eventId=" + eventId + 
               ", eventName=" + eventName + 
               ", eventType=" + eventType + 
               ", eventDate=" + eventDate + 
               ", eventDescription=" + eventDescription + 
               ", eventLocation=" + eventLocation + "]";
    }
}
