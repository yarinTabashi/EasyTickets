package com.example.demo.Controllers;
import com.example.demo.Entities.Reservation;
import com.example.demo.Services.ReservationService;
import com.example.demo.Services.SeatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

public class ReservationController {
    private final ReservationService reservationService;
    private final SeatService seatService;

    public ReservationController(ReservationService reservationService, SeatService seatService){
        this.reservationService = reservationService;
        this.seatService = seatService;
    }

    @PostMapping("/reserve/{seat-id}")
    public void reserve(@RequestHeader("Authorization") String token, @RequestParam Long seatId) {
        // Validate seat and mark it as unavailable
        seatService.reserveSeat(seatId);

        // Create reservation object
        reservationService.reserve(token, seatId);
    }

    @GetMapping()
    public List<Reservation> getAll(@RequestHeader String token){
        return reservationService.getAll(token);
    }
}
