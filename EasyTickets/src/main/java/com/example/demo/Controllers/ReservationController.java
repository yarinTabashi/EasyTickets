package com.example.demo.Controllers;
import com.example.demo.Entities.Reservation;
import com.example.demo.Services.ReservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService){
        this.reservationService = reservationService;
    }

    @PostMapping("/reserve/{event-id}")
    public void reserve(@RequestHeader("Authorization") String token, @RequestParam Long eventId){
        reservationService.reserve(token, eventId);
    }

    @GetMapping()
    public List<Reservation> reserve(@RequestHeader String token){
        return reservationService.getAll(token);
    }
}
