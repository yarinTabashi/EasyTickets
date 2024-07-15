package com.example.demo.Services;
import com.example.demo.Entities.Event;
import com.example.demo.Entities.Reservation;
import com.example.demo.Entities.Seat;
import com.example.demo.Entities.User;
import com.example.demo.Repositories.ReservationRepository;
import com.example.demo.Repositories.SeatRepository;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.mysecurity.JwtHelper;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository, SeatRepository seatRepository){
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
    }

    public boolean reserve(String token, Long seatId) {
        // Get the user
        Optional<User> optionalUser = getUserFromToken(token);
        User user = optionalUser.orElseThrow(() -> new RuntimeException("User not found"));

        // Validate the required seat
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        Seat seat = optionalSeat.orElseThrow(() -> new RuntimeException("Seat not found"));

        // Create a reservation object
        Reservation reservation = new Reservation(user, new Date(), seat, generateSerialNum());
        reservationRepository.save(reservation);
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
