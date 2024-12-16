package com.example.event_review.Controller;

import com.example.event_review.DTO.EventDTO;
import com.example.event_review.Entity.Event;
import com.example.event_review.Service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public List<EventDTO> getAllEvents() {
        return eventService.getAllEvents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        Optional<EventDTO> event = eventService.getEventById(id);
        return event.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{eventName}")
    public ResponseEntity<EventDTO> getEventByName(@PathVariable String eventName) {
        Optional<EventDTO> event = eventService.getEventByName(eventName);
        return event.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Event addEvent(@RequestBody Event event) {
        return eventService.addEvent(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Event event) {
        Event updatedEvent = eventService.updateEvent(id, event);
        return updatedEvent != null ? 
            ResponseEntity.ok(updatedEvent) : 
            ResponseEntity.notFound().build();
    }

    @PutMapping("/name/{eventName}")
    public ResponseEntity<Event> updateEventByName(
            @PathVariable String eventName, 
            @RequestBody Event event) {
        Event updatedEvent = eventService.updateEventByName(eventName, event);
        return updatedEvent != null ? 
            ResponseEntity.ok(updatedEvent) : 
            ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}
