package com.example.demo.Services;
import com.example.demo.Entities.Event;
import com.example.demo.Entities.Seat;
import com.example.demo.Repositories.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;

    public SeatService(SeatRepository seatRepository){
        this.seatRepository = seatRepository;
    }

    public List<Seat> getSeatsByEvent(Long eventId){
        return seatRepository.findByEventId(eventId);
    }

    public boolean isLastSeat(Long eventId){
        System.out.println(seatRepository.countByEventIdAndAvailableTrue(eventId));
        return seatRepository.countByEventIdAndAvailableTrue(eventId) < 1;
    }

    /**
     * Check if a seat is available
     * @param seatId The ID of the seat to be checked
     * @return true if the seat is available, false otherwise
     * */
    @Transactional
    public boolean isAvailable(Long seatId){
        // Fetch the seat by ID
        //Optional<Seat> optionalSeat = seatRepository.findById(seatId);
        Optional<Seat> optionalSeat = seatRepository.findByIdForUpdate(seatId);

        if (optionalSeat.isPresent()){
            Seat seat = optionalSeat.get();
            if (seat.isAvailable()){
                return true;
            }
        }
        return false;
    }

    /**
     * Reserve a seat for an event
     * @param seatId The ID of the seat to be reserved
     * @return true if the seat was successfully reserved, false otherwise
     * */
    @Transactional
    public boolean checkAvailableAndReserveSeat(Long seatId) {
        Optional<Seat> optionalSeat = seatRepository.findByIdForUpdate(seatId);

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

    /**
     *  Create seats for an event
     *  @param event The event for which seats are to be created
     * */
    public void createSeats(Event event) {
        List<Seat> seats = new ArrayList<>();

        int totalRows = 3;
        int seatsPerRow = 3;

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
