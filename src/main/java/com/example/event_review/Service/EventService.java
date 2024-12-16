package com.example.event_review.Service;

import com.example.event_review.DTO.EventDTO;
import com.example.event_review.Entity.Event;
import com.example.event_review.Repo.EventRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

@Service
public class EventService {

    @Autowired
    private EventRepo eventRepo;

    public List<EventDTO> getAllEvents() {
        return eventRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<EventDTO> getEventById(Long id) {
        return eventRepo.findById(id)
                .map(this::convertToDTO);
    }

    public Optional<EventDTO> getEventByName(String eventName) {
        return eventRepo.findByEventName(eventName)
                .map(this::convertToDTO);
    }

    public Event addEvent(Event event) {
        return eventRepo.save(event);
    }

    public Event updateEvent(Long id, Event event) {
        Optional<Event> existingEvent = eventRepo.findById(id);
        if (existingEvent.isPresent()) {
            Event updatedEvent = existingEvent.get();
            updatedEvent.setEventName(event.getEventName());
            updatedEvent.setEventType(event.getEventType());
            updatedEvent.setEventDate(event.getEventDate());
            updatedEvent.setEventDescription(event.getEventDescription());
            updatedEvent.setEventLocation(event.getEventLocation());
            return eventRepo.save(updatedEvent);
        }
        return null;
    }

    public Event updateEventByName(String eventName, Event event) {
        Optional<Event> existingEvent = eventRepo.findByEventName(eventName);
        if (existingEvent.isPresent()) {
            Event updatedEvent = existingEvent.get();
            updatedEvent.setEventName(event.getEventName());
            updatedEvent.setEventType(event.getEventType());
            updatedEvent.setEventDate(event.getEventDate());
            updatedEvent.setEventDescription(event.getEventDescription());
            updatedEvent.setEventLocation(event.getEventLocation());
            return eventRepo.save(updatedEvent);
        }
        return null;
    }

    public void deleteEvent(Long id) {
        eventRepo.deleteById(id);
    }

    private EventDTO convertToDTO(Event event) {
        EventDTO dto = new EventDTO();
        dto.setEventId(event.getEventId());
        dto.setEventName(event.getEventName());
        dto.setEventType(event.getEventType());
        dto.setEventDate(event.getEventDate());
        dto.setEventDescription(event.getEventDescription());
        dto.setEventLocation(event.getEventLocation());
        return dto;
    }
}
