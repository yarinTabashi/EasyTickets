package com.example.demo.Services;
import com.example.demo.Entities.Reservation;
import com.example.demo.Repositories.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository){
        this.reservationRepository = reservationRepository;
    }

    public void reserve(String token, Long eventId){

    }

    public List<Reservation> getAll(String token){
        return null;
    }
}
