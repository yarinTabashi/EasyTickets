package com.example.demo.Services;
import com.example.demo.Entities.Event;
import com.example.demo.Entities.Seat;
import com.example.demo.Repositories.SeatRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SeatService {
    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository){
        this.seatRepository = seatRepository;
    }

    public List<Seat> getSeatsByEvent(Long eventId){
        return seatRepository.findByEventId(eventId);
    }

    public boolean reserveSeat(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        if (optionalSeat.isPresent()) {
            Seat seat = optionalSeat.get();
            if (seat.isAvailable()) {
                seat.setAvailable(false); // Mark seat as reserved
                seatRepository.save(seat);
                return true; // Reservation successful
            } else {
                return false; // Seat is already reserved
            }
        } else {
            throw new IllegalArgumentException("Seat not found with ID: " + seatId);
        }
    }

    // Create seats for new event
    public void createSeats(Event event) {
        List<Seat> seats = new ArrayList<>();

        int totalRows = 10;
        int seatsPerRow = 10;

        for (int row = 1; row <= totalRows; row++) {
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                Seat seat = new Seat(row, seatNum, true, BigDecimal.valueOf(50.00), event);
                seats.add(seat);
            }
        }

        // Save all seats for the event
        seatRepository.saveAll(seats);
    }
}
