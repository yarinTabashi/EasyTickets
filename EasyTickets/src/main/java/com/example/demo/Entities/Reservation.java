package com.example.demo.Entities;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reservation", schema = "public")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "`user`")
    private User user;

    @Column(name = "reservation_date")
    private Date reservationDate;

    @ManyToOne
    @JoinColumn(name = "seat")
    private Seat seat;

    @Column(name = "serial_num")
    private Integer serialNum;

    public Reservation(){
    }

    public Reservation(User user, Date reservationDate, Seat seat, Integer serialNum) {
        this.user = user;
        this.reservationDate = reservationDate;
        this.seat = seat;
        this.serialNum = serialNum;
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

    public Integer getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(Integer serialNum) {
        this.serialNum = serialNum;
    }
}
