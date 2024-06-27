package com.example.demo.Controllers;
import com.example.demo.DTOs.EventDTO;
import com.example.demo.Entities.Event;
import com.example.demo.Services.EventService;
import com.example.demo.Services.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final SeatService seatService;

    public EventController(EventService eventService, SeatService seatService){
        this.eventService = eventService;
        this.seatService = seatService;
    }

    // Retrieve by event id
    @GetMapping("/{id}")
    public Event getEventById(@PathVariable Long id) {
        return eventService.getEventById(id);
    }

    @PostMapping()
    public void createEvent(@RequestBody EventDTO eventDTO) {
        // Create the event object and save it in the db
        Event event = eventService.createEvent(eventDTO);

        // Create seats and initialize it to available.
        seatService.createSeats(event);
    }

    @GetMapping("/possible")
    public ResponseEntity<List<Event>> getPossibleEvents(@RequestHeader("Authorization") String token) {
        try{
            List<Event> events = eventService.getUpcomingEvents(token);
            return ResponseEntity.ok(events);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }
}