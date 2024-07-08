package com.example.demo.Services;
import com.example.demo.Entities.Reservation;
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
        User user;
        if (optionalUser.isPresent()){
            user = optionalUser.get();
        }
        else {
            throw new RuntimeException("User not found");
        }

        // Mark the seat as unavailable


        // Validate the required seat

        // Create a reservation object
        Reservation reservation = new Reservation(user, new Date(), seatRepository.getById(seatId));
        reservationRepository.save(reservation);
        return true;
    }

    public List<Reservation> getAll(String token){
        return null;
    }

    private Optional<User> getUserFromToken(String token) {
        String email = JwtHelper.extractUsername(token.replace("Bearer ", ""));
        return userRepository.findByEmail(email);
    }
}
