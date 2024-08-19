package com.example.demo.Services;
import com.example.demo.DTOs.EventDTO;
import com.example.demo.Entities.Category;
import com.example.demo.Entities.Event;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.EventRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.mysecurity.JwtHelper;
import org.springframework.stereotype.Service;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public EventService(EventRepository eventRepository, UserRepository userRepository){
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
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

    private Date stripTime(Date date) {
        // Remove time from the date
        String formattedDate = DATE_FORMAT.format(date);
        try {
            return DATE_FORMAT.parse(formattedDate);
        } catch (Exception e) {
            throw new RuntimeException("Date parsing error", e);
        }
    }

    /** The main function to retrieve the possible events. Considering:
     1. Just upcoming events
     2. Order by his preferences (arranged according to the probability that he will like it).
     3. Just events with available seats
     */
    public List<Event> getUpcomingEvents(String token) {
        // Create current date object (without the time)
        Date currentDate = stripTime(new Date());

        // Get the user
        Optional<User> optionalUser = getUserFromToken(token);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));

        // Get user's liked categories if user exists
        Set<Category> likedCategories = user.getLikedCategories();

        // Ensuring the events have available seats and the date is greater than or equal to the currentDate
        List<Event> events = null;
        events = eventRepository.findBySeatsAvailableIsTrueAndDateGreaterThanEqual(currentDate);

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

    /**
     * Check if the event is related to any of the liked categories
     * @param event Event to check
     * @param likedCategories Set of liked categories
     * */
    private boolean isEventRelatedToLikedCategory(Event event, Set<Category> likedCategories) {
        return likedCategories.contains(event.getCategory());
    }

    private Optional<User> getUserFromToken(String token) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        return userRepository.findByEmail(email);
    }
}