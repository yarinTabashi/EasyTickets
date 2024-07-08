package com.example.demo.Repositories;
import com.example.demo.Entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findBySeatsAvailableIsTrueAndDateGreaterThanEqual(Date currentDate);
}