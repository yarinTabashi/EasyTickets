package com.example.demo.Services;
import com.example.demo.DTOs.EventDTO;
import com.example.demo.Entities.Event;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.EventRepository;
import com.example.demo.Repositories.SeatRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.mysecurity.JwtHelper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository, SeatRepository seatRepository){
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event not found with id: " + id));
    }

    public Event createEvent(EventDTO eventDTO){
        Event event = new Event();
        event.setName(eventDTO.name());
        event.setDescription(eventDTO.desc());
        event.setDate(eventDTO.date());
        event.setVenue(eventDTO.venue());
        event.setCategory(eventDTO.category());

        // Save event to make it persistent
        event = eventRepository.save(event);
        return event;
    }

    /** The main function to retrieve the possible events. Considering:
     1. Just upcoming events
     2. Order by his preferences (arranged according to the probability that he will like it).
     3. Just events with available seats
     */
//    public List<Event> getUpcomingEvents(String token) {
//        // Get current date
//        Date currentDate = new Date();
//
//        // Get user's liked categories if user exists
//        Optional<User> optionalUser = getUserFromToken(token);
//        Set<Category> likedCategories = optionalUser.map(User::getLikedCategories).orElse(null);
//
//        // Fetch upcoming events with available seats, ordered by preference
//        return eventRepository.findUpcomingEventsWithAvailableSeatsOrderedByPreference(currentDate, likedCategories);
//    }

        public List<Event> getUpcomingEvents(String token) {
            // Get current date
            Date currentDate = new Date();

            // Get user's liked categories if user exists

            return eventRepository.findBySeatsAvailableIsTrue();
        }

        private Optional<User> getUserFromToken(String token) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        return userRepository.findByEmail(email);
    }
}