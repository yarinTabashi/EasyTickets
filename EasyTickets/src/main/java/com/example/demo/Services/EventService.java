package com.example.demo.Services;
import com.example.demo.DTOs.EventDTO;
import com.example.demo.Entities.Category;
import com.example.demo.Entities.Event;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.EventRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.mysecurity.JwtHelper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.*;

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
        event.setUrl(eventDTO.url());

        // Save event to make it persistent
        event = eventRepository.save(event);
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

        // Get the user
        Optional<User> optionalUser = getUserFromToken(token);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));

        // Get user's liked categories if user exists
        Set<Category> likedCategories = user.getLikedCategories();

        // Get upcoming events with available seats and date >= currentDate
        List<Event> events = eventRepository.findBySeatsAvailableIsTrueAndDateGreaterThanEqual(currentDate);

        // Custom sorting: Sort events so that events related to liked categories appear first
        events.sort((e1, e2) -> {
            boolean e1Liked = isEventRelatedToLikedCategory(e1, likedCategories);
            boolean e2Liked = isEventRelatedToLikedCategory(e2, likedCategories);

            // Sort in descending order: liked categories first, then others
            if (e1Liked && !e2Liked) {
                return -1;
            } else if (!e1Liked && e2Liked) {
                return 1;
            } else {
                return 0;
            }
        });

        return events;
    }

    private boolean isEventRelatedToLikedCategory(Event event, Set<Category> likedCategories) {
        return likedCategories.contains(event.getCategory());
    }

    private Optional<User> getUserFromToken(String token) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        return userRepository.findByEmail(email);
    }
}