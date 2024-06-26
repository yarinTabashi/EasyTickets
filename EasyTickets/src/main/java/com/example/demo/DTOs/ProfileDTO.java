package com.example.demo.DTOs;

// This DTO represents the user fields are mutable and can be returned to the frontend.
public record ProfileDTO(String first_name, String last_name, String email) {
}
