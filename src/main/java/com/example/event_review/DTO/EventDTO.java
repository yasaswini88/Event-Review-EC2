package com.example.event_review.DTO;

import java.time.LocalDateTime;

public class EventDTO {
    private Long eventId;
    private String eventName;
    private String eventType;
    private LocalDateTime eventDate;
    private String eventDescription;
    private String eventLocation;
    public Long getEventId() {
        return eventId;
    }
    public String getEventName() {
        return eventName;
    }


    
    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }
    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }
    @Override
    public String toString() {
        return "EventDTO [eventId=" + eventId + ", eventName=" + eventName + ", eventType=" + eventType + ", eventDate="
                + eventDate + ", eventDescription=" + eventDescription + ", eventLocation=" + eventLocation + "]";
    }
    public String getEventType() {
        return eventType;
    }
    public LocalDateTime getEventDate() {
        return eventDate;
    }
    public String getEventDescription() {
        return eventDescription;
    }
    public String getEventLocation() {
        return eventLocation;
    }
    
    

    
}