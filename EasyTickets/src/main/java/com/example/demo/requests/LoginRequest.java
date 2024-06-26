package com.example.demo.requests;
import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        String email,
        String password) {

}