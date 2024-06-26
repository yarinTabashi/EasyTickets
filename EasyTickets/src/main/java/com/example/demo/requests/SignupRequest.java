package com.example.demo.requests;

public record SignupRequest(
        String first_name,
        String last_name,
        String username,
        String email,
        String password) {
}