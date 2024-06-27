package com.example.demo.Services;
import com.example.demo.DTOs.EventDTO;
import com.example.demo.Entities.Category;
import com.example.demo.Entities.Event;
import com.example.demo.Entities.Seat;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.EventRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.mysecurity.JwtHelper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository, UserRepository userRepository){
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
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

        return event;
    }

    /** The main function to retrieve the possible events. Considering:
     1. Just upcoming events
     2. Order by his preferences (arranged according to the probability that he will like it).
     3. Just events with available seats
     */
    public List<Event> getUpcomingEvents(String token) {
        // Get current date
        Date currentDate = new Date();

        // Get user's liked categories if user exists
        Optional<User> optionalUser = getUserFromToken(token);
        Set<Category> likedCategories = optionalUser.map(User::getLikedCategories).orElse(null);

        // Fetch upcoming events with available seats, ordered by preference
        return eventRepository.findUpcomingEventsWithAvailableSeatsOrderedByPreference(currentDate, likedCategories);
    }

    private Optional<User> getUserFromToken(String token) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        return userRepository.findByEmail(email);
    }
}