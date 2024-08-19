package com.example.demo.Controllers;
import com.example.demo.Entities.Event;
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

    /**
     * Reserve a seat for the user (every seat belongs to an event)
     * @param token Authorization token
     * @param seatId Seat ID to reserve
     * @return HTTP response
     */
    @PostMapping("/{seatId}")
    public ResponseEntity<Void> reserve(@RequestHeader("Authorization") String token, @PathVariable Long seatId) {
        try {
            // Validate seat and mark it as unavailable
            if (!seatService.isAvailable(seatId)){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
            seatService.reserveSeat(seatId);

            // Create reservation object
            boolean status = reservationService.reserve(token, seatId);
            if (status){
                return ResponseEntity.status(HttpStatus.OK).build(); // Reservation created successfully
            }
            else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
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

    /**
     * Get the closest upcoming event of the user.
     * @param token Authorization token
     * @return HTTP response
     */
    @GetMapping("/closest")
    public ResponseEntity<Event> getCloseEvent(@RequestHeader("Authorization") String token) {
        try {
            Event event = reservationService.getCloseEvent(token);
            if (event != null) {
                return ResponseEntity.ok(event); // Return the event if found
            } else {
                return ResponseEntity.notFound().build(); // Return 404 if no event found
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Handle internal server error
        }
    }
}
