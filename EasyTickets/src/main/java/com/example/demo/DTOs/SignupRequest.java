package com.example.demo.DTOs;

public record SignupRequest(
        String first_name,
        String last_name,
        String username,
        String email,
        String password) {
}