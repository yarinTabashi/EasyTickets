package com.example.demo.Services;
import com.example.demo.Entities.Event;
import com.example.demo.Entities.Reservation;
import com.example.demo.Entities.Seat;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.ReservationRepository;
import com.example.demo.Repositories.SeatRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.mysecurity.JwtHelper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatService seatService;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    @Autowired
    private CacheEvictionService cacheEvictionService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository, SeatService seatService, SeatRepository seatRepository){
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.seatService = seatService;
        this.seatRepository = seatRepository;
    }

    public boolean reserve(String token, Long seatId) {
        // Get the user
        Optional<User> optionalUser = getUserFromToken(token);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));

        // Get the seat (validation was done previously)
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new RuntimeException("Seat not found"));

        // Create a reservation object
        Reservation reservation = new Reservation(user, new Date(), seat, generateSerialNum());
        reservationRepository.save(reservation);

        // Create a message to be published
        String message = String.format("A ticket has just been purchased for this event.");
        // Create an event-specific channel name
        String channelName = "purchase-channel-" + seat.getEvent().getId();
        // Publish the message to the event-specific Redis channel
        redisTemplate.convertAndSend(channelName, message);

        // Check if it's the last available seat for the event
        boolean isLastSeat = seatService.isLastSeat(seat.getEvent().getId());
        if (isLastSeat) {
            // Evict the cache for events list associated with the specific event's date
            cacheEvictionService.evictEventsCache();
        }
        return true;
    }

    private int generateSerialNum(){
        Random random = new Random();
        int num = random.nextInt(10000); // Generates a number between 0 and 9999
        String numSt = String.format("%04d", num); // Pad with leading zeros to ensure 4 digits
        return Integer.valueOf(numSt);
    }

    public List<Reservation> getAll(String token) {
        // Get the user
        Optional<User> optionalUser = getUserFromToken(token);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));

        return reservationRepository.findByUser(user); // Assuming you have this method in the repository
    }

    private Optional<User> getUserFromToken(String token) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        return userRepository.findByEmail(email);
    }

    public Event getCloseEvent(String token){
        List<Reservation> reservationList = getAll(token);

        Reservation closestReservation = null;
        Date currentDate = new Date(); // Current date/time

        // Iterate through the reservations to find the closest one
        for (Reservation reservation : reservationList) {
            if (closestReservation == null ||
                    reservation.getSeat().getEvent().getDate().before(closestReservation.getSeat().getEvent().getDate())) {
                closestReservation = reservation;
            }
        }

        // If the closest reservation is found, return its associated Event
        if (closestReservation != null) {
            return closestReservation.getSeat().getEvent();
        } else {
            return null; // Handle case where no reservations are found
        }
    }
}

