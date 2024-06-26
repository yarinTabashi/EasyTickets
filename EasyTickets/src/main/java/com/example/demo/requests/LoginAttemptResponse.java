package com.example.demo.requests;
import com.example.demo.Entities.LoginAttempt;
import java.time.LocalDateTime;

public record LoginAttemptResponse(LocalDateTime createdAt, boolean success) {
    public static LoginAttemptResponse convertToDTO(LoginAttempt loginAttempt) {
        return new LoginAttemptResponse(loginAttempt.getCreatedAt(), loginAttempt.isSuccess());
    }
}