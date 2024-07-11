package com.example.demo.requests;

public record TOTPRequest (
        String email,
        int otp){
}
