package com.example.demo.DTOs;

public record TOTPRequest (
        String email,
        int otp){
}
