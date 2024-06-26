package com.example.demo.Entities;

public class Seat {
    private int seatX;
    private int seatY;

    public Seat(int seatX, int seatY){
        this.seatX = seatX;
        this.seatY = seatY;
    }

    public int getSeatX() {
        return seatX;
    }

    public void setSeatX(int seatX) {
        this.seatX = seatX;
    }

    public int getSeatY() {
        return seatY;
    }

    public void setSeatY(int seatY) {
        this.seatY = seatY;
    }
}
