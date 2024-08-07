package com.example.demo.Repositories;
import com.example.demo.Entities.Event;
import com.example.demo.Entities.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByEventAndAvailableTrue(Event event);
    List<Seat> findByEventId(Long eventId);
    int countByEventIdAndAvailableTrue(Long id);
    Optional<Seat> findById(Long seatId);
}