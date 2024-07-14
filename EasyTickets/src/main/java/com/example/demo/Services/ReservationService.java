package com.example.demo.Services;
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
        Reservation reservation = new Reservation(user, new Date(), seat);
        reservationRepository.save(reservation);
        return true;
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
}
