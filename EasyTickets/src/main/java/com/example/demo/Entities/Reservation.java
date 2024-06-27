package com.example.demo.Entities;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reservation", schema = "public")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private User user;

    @Column(name = "reservation_date")
    private Date reservationDate;
    @Column(name = "seat_id")
    private Seat seat;

    public Reservation(){
    }

    public Reservation(User user, Date reservationDate, Seat seat) {
        this.id = id;
        this.user = user;
        this.reservationDate = reservationDate;
        this.seat = seat;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }
}
