package com.example.demo.CustomExceptions;

public record ApiErrorResponse(int errorCode, String description) {
}