package com.example.demo.Controllers;
import com.example.demo.Services.AuthService;
import com.example.demo.Services.EmailService;
import com.example.demo.mysecurity.JwtHelper;
import com.example.demo.requests.LoginRequest;
import com.example.demo.requests.LoginResponse;
import com.example.demo.requests.SignupRequest;
import com.example.demo.requests.TOTPRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

/**
 * Controller class for handling authentication-related HTTP requests.
 * Provides endpoints for user login, signup, password update, OTP generation and verification.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final EmailService emailService;

    int mytry; /*Commit try 15/07/2024 19:04*/
    public AuthController(AuthenticationManager authenticationManager, AuthService authService, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.authService = authService;
        this.emailService = emailService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        } catch (BadCredentialsException e) {
            authService.addLoginAttempt(request.email(), false);
            throw e;
        }

        String token = JwtHelper.generateToken(request.email());
        authService.addLoginAttempt(request.email(), true);

        return ResponseEntity.ok(new LoginResponse(request.email(), token));
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<LoginResponse> login(@RequestBody SignupRequest request) throws Exception {
        try {
            authService.signup(request);
        } catch (BadCredentialsException e) {
            authService.addLoginAttempt(request.email(), false);
            throw e;
        }

        String token = JwtHelper.generateToken(request.email());
        authService.addLoginAttempt(request.email(), true);

        return ResponseEntity.ok(new LoginResponse(request.email(), token));
    }

    @PostMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@RequestHeader(name = "Authorization") String token,
                                               @RequestBody String newPassword) {
        authService.updatePassword(token, newPassword);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Generates totp and send it with an email to the user.
    @PostMapping("/send-otp")
    public ResponseEntity<Void> totpVerification(@RequestBody String email) throws IOException {
        int totp = authService.getTOTP(email);
        if (totp != -1){
            System.out.println("OTP is: " + totp);
            //emailService.sendOtpEmail("yarintabashi@gmail.com", Integer.toString(totp)); // Send TOTP via email
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<LoginResponse> verifyTotp(@RequestBody TOTPRequest totpRequest) throws IOException {
        if (authService.verifyTOTP(totpRequest.email(), totpRequest.otp())){
            String token = JwtHelper.generateToken(totpRequest.email());
            authService.addLoginAttempt(totpRequest.email(), true);
            return ResponseEntity.ok(new LoginResponse(totpRequest.email(), token));
        }
        return ResponseEntity.notFound().build(); // Return 404 if OTP is incorrect
    }
}