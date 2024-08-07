package com.example.demo.Repositories;
import com.example.demo.Entities.Event;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;

@Repository
@EnableCaching
public interface EventRepository extends JpaRepository<Event, Long> {
    @Cacheable(value = "events", key = "#currentDate.getTime()")
    List<Event> findBySeatsAvailableIsTrueAndDateGreaterThanEqual(Date currentDate);
}