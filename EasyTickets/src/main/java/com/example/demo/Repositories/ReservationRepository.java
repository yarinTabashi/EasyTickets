package com.example.demo.Repositories;
import com.example.demo.Entities.Category;
import com.example.demo.Entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation,Long> {

}
