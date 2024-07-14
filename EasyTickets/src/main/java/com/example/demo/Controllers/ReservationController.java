package com.example.demo.Controllers;
import com.example.demo.Entities.Reservation;
import com.example.demo.Services.ReservationService;
import com.example.demo.Services.SeatService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final SeatService seatService;

    public ReservationController(ReservationService reservationService, SeatService seatService){
        this.reservationService = reservationService;
        this.seatService = seatService;
    }

    @PostMapping("/{seatId}")
    public ResponseEntity<Void> reserve(@RequestHeader("Authorization") String token, @PathVariable Long seatId) {
        try {
            // Validate seat and mark it as unavailable
            seatService.reserveSeat(seatId);

            // Create reservation object
            reservationService.reserve(token, seatId);

            return ResponseEntity.status(HttpStatus.CREATED).build(); // Reservation created successfully
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Seat or user not found
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Invalid input
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // General error
        }
    }

    @GetMapping()
        public List<Reservation> getAll(@RequestHeader("Authorization") String token){
        return reservationService.getAll(token);
    }
}
