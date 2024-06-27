package com.example.demo.Controllers;
import com.example.demo.Entities.Seat;
import com.example.demo.Services.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/seats")
public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatService seatService){
        this.seatService = seatService;
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<List<Seat>> getSeatsByEvent(@PathVariable Long eventId) {
        try {
            List<Seat> seats = seatService.getSeatsByEvent(eventId);
            return ResponseEntity.ok(seats);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping("/reserve/{seatId}")
    public ResponseEntity<String> reserveSeat(@PathVariable Long seatId) {
        try {
            seatService.reserveSeat(seatId);
            return ResponseEntity.ok("Seat reserved successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}