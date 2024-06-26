package com.example.demo.requests;

public record LoginResponse(
        String email,
        String token) {

}