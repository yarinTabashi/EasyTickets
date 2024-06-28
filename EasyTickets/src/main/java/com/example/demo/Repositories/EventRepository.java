package com.example.demo.Repositories;

import com.example.demo.Entities.Category;
import com.example.demo.Entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    //boolean existsBySeatsAvailableIsTrue();
    List<Event> findBySeatsAvailableIsTrue();

    //List<Event> findByCategoryIdIn(Set<Long> categoryIds);

//    @Query("SELECT DISTINCT e FROM event e " +
//            "LEFT JOIN FETCH e.seat s " +
//            "WHERE e.date >= :currentDate " +
//            "AND s.available = true " +
//            "ORDER BY CASE WHEN e.category IN :public.user_preferences THEN 0 ELSE 1 END, e.date ASC")
//    List<Event> findUpcomingEventsWithAvailableSeatsOrderedByPreference(
//            @Param("currentDate") Date currentDate,
//            @Param("likedCategories") Set<Category> likedCategories);
}